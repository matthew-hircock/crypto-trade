package com.mbh.CryptoTrade.service;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mbh.CryptoTrade.api.ICryptoApiService;
import com.mbh.CryptoTrade.component.TradeFactory;
import com.mbh.CryptoTrade.dal.CoinRepository;
import com.mbh.CryptoTrade.dal.HoldingRepository;
import com.mbh.CryptoTrade.dal.TradeRepository;
import com.mbh.CryptoTrade.dal.TraderRepository;
import com.mbh.CryptoTrade.exception.InvalidTradeException;
import com.mbh.CryptoTrade.exception.NotFoundException;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Holding;
import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.setup.Constants;
import com.mbh.CryptoTrade.validation.TradeValidator;
import com.mbh.CryptoTrade.view.ITradeDetailsView;
import com.mbh.CryptoTrade.view.TradeDetailsView;
import com.mbh.CryptoTrade.view.TradeForm;

@Service
public class TradeService {

	private static final Logger logger = LogManager.getLogger(TradeService.class);

	private TraderRepository traderRepo;
	private TradeRepository tradeRepo;
	private HoldingRepository holdingRepo;
	private CoinRepository coinRepo;
	private ICryptoApiService cryptoApiService;
	private TradeFactory tradeFactory;
	private TradeValidator tradeValidator;

	@Autowired
	public TradeService(TraderRepository traderRepo, TradeRepository tradeRepo, HoldingRepository holdingRepo,
			CoinRepository coinRepo, ICryptoApiService cryptoApiService, TradeValidator tradeValidator,
			TradeFactory tradeFactory) {
		super();
		this.traderRepo = traderRepo;
		this.tradeRepo = tradeRepo;
		this.holdingRepo = holdingRepo;
		this.coinRepo = coinRepo;
		this.cryptoApiService = cryptoApiService;
		this.tradeValidator = tradeValidator;
		this.tradeFactory = tradeFactory;
	}

	/**
	 * @param traderUsername
	 *            the username of the trader
	 * @param pageNumber
	 *            the page number being requested (indices start at 0)
	 * @param pageSize
	 *            the maximum number of elements on each page
	 * @return a page containing trade details of trades made by the trader
	 */
	public Page<ITradeDetailsView> getTradesForTraderByPage(String traderUsername, int pageNumber, int pageSize) {
		Page<Trade> trades = tradeRepo.findByTraderUsername(traderUsername,
				PageRequest.of(pageNumber, pageSize, Direction.DESC, "transactionDateTime"));
		return trades.map(t -> new TradeDetailsView(t));
	}

	/**
	 * @param tradeId
	 *            the id of the trade
	 * @param traderUsername
	 *            the username of the trader
	 * @return the trade details
	 * @throws NotFoundException
	 *             If the trade does not exist or was not made by the trader
	 */
	public TradeDetailsView getTradeByIdAndTraderUsername(long tradeId, String traderUsername) {
		return new TradeDetailsView(tradeRepo.findByIdAndTraderUsername(tradeId, traderUsername)
				.orElseThrow(() -> new NotFoundException("trade not found")));
	}

	/**
	 * @param traderUsername
	 *            the username of the trader making the trade
	 * @param tradeForm
	 *            a trade form for the trade being made
	 * @return the id of the newly constructed trade
	 * @throws InvalidTradeException
	 *             If the trade form or trade is invalid
	 */
	@Transactional
	public long processTrade(String traderUsername, TradeForm tradeForm) throws InvalidTradeException {
		String coinId = tradeForm.getCoinId();
		Trader trader = traderRepo.findByUsername(traderUsername).orElseThrow(
				() -> new NotFoundException("trader with username '" + traderUsername + "' was not found"));
		Coin coin = coinRepo.findById(coinId)
				.orElseThrow(() -> new NotFoundException("coin with id '" + coinId + "' was not found"));
		BigDecimal unitPrice = cryptoApiService.getCoinPriceById(coinId, trader.getCurrency());
		Trade trade = tradeFactory.createTrade(tradeForm, unitPrice, coin, trader);

		// may need to restructure when adding limit orders
		tradeValidator.validate(trade);
		updateTraderBalance(trader, trade);
		// only do if trade - not limit order
		Holding holding = updateHolding(trade, coin, trader);

		if (holding.getAmount() == 0) {
			holdingRepo.delete(holding);
		} else {
			holdingRepo.save(holding);
		}
		traderRepo.save(trader);
		return tradeRepo.save(trade).getId();
	}

	/**
	 * @param trade
	 *            the trade being made
	 * @param coin
	 *            the coin being traded
	 * @param trader
	 *            the trader making the trade
	 * @return either a new holding (if the coin was not previously owned), or
	 *         trader holding with an updated amount
	 */
	Holding updateHolding(Trade trade, Coin coin, Trader trader) {
		Holding holding = holdingRepo.findByCoinAndTrader(coin, trader)
				.orElseGet(() -> new Holding(0, BigDecimal.valueOf(0), coin, trader));
		long adjustmentAmount = (trade.getType() == TradeType.BUY) ? trade.getAmount() : -trade.getAmount();
		holding.adjustHolding(adjustmentAmount, trade.getUnitPrice());
		return holding;
	}

	void updateTraderBalance(Trader trader, Trade trade) {
		BigDecimal totalCost = trade.getUnitPrice().multiply(BigDecimal.valueOf(trade.getAmount()),
				Constants.getMathContext());
		if (trade.getType() == TradeType.BUY) {
			logger.debug("trader {} balance: ${}", trader.getUsername(), trader.getBalance());
			logger.debug("Deducting ${} from trader {}", totalCost, trader.getUsername());
			trader.subtractFromBalance(totalCost);
			logger.debug("trader {} balance: ${}", trader.getUsername(), trader.getBalance());
		} else {
			logger.debug("trader {} balance: ${}", trader.getUsername(), trader.getBalance());
			logger.debug("Adding ${} to trader {}", totalCost, trader.getUsername());
			trader.addToBalance(totalCost);
			logger.debug("trader {} balance: ${}", trader.getUsername(), trader.getBalance());
		}
	}

}
