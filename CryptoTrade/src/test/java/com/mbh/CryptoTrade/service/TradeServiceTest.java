package com.mbh.CryptoTrade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import com.mbh.CryptoTrade.api.ICryptoApiService;
import com.mbh.CryptoTrade.component.TradeFactory;
import com.mbh.CryptoTrade.dal.CoinRepository;
import com.mbh.CryptoTrade.dal.HoldingRepository;
import com.mbh.CryptoTrade.dal.TradeRepository;
import com.mbh.CryptoTrade.dal.TraderRepository;
import com.mbh.CryptoTrade.exception.InvalidTradeException;
import com.mbh.CryptoTrade.exception.NotFoundException;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Currency;
import com.mbh.CryptoTrade.model.Holding;
import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.setup.Constants;
import com.mbh.CryptoTrade.validation.TradeValidator;
import com.mbh.CryptoTrade.view.ICoinDetailsView;
import com.mbh.CryptoTrade.view.ITradeDetailsView;
import com.mbh.CryptoTrade.view.TradeForm;

@SpringBootTest
public class TradeServiceTest {

	private static final String USERNAME = "matthew.h";
	private static final String COIN_ID = "bitcoin";
	private static final Coin COIN = new Coin(COIN_ID, "btc", "Bitcoin");
	private static final Currency CURRENCY = Currency.AUD;
	private static final int DECIMAL_PLACES = 6;

	@Autowired
	private TradeService tradeService;

	@MockBean
	private TraderRepository mockTraderRepo;
	@MockBean
	private TradeRepository mockTradeRepo;
	@MockBean
	private HoldingRepository mockHoldingRepo;
	@MockBean
	private CoinRepository mockCoinRepo;
	@MockBean
	private ICryptoApiService mockCryptoApiService;
	@MockBean
	private TradeValidator mockTradeValidator;
	@MockBean
	private TradeFactory mockTradeFactory;

	@Mock
	private Trader mockTrader;
	@Mock
	private Holding mockHolding;
	@Mock
	private Trade mockTrade;

	@ParameterizedTest // pageNumber,pageSize
	@CsvSource({ "7,10", "1,20", "15,100" })
	public void that_getTradesForTraderByPage_returns_page_of_trades_from_repository(int pageNumber, int pageSize) {
		Coin coin1 = new Coin("bitcoin", "btc", "Bitcoin");
		Coin coin2 = new Coin("etherium", "eth", "Etherium");
		Trade trade1 = new Trade(5, 100, BigDecimal.valueOf(150), TradeType.BUY, coin1, null);
		Trade trade2 = new Trade(6, 125, BigDecimal.valueOf(1000), TradeType.SELL, coin2, null);
		List<Trade> trades = Arrays.asList(trade1, trade2);
		Page<Trade> page = new PageImpl<>(trades);
		when(mockTradeRepo.findByTraderUsername(USERNAME,
				PageRequest.of(pageNumber, pageSize, Direction.DESC, "transactionDateTime"))).thenReturn(page);

		Page<ITradeDetailsView> foundTrades = tradeService.getTradesForTraderByPage(USERNAME, pageNumber, pageSize);

		verify(mockTradeRepo).findByTraderUsername(USERNAME,
				PageRequest.of(pageNumber, pageSize, Direction.DESC, "transactionDateTime"));
		List<ITradeDetailsView> allTrades = foundTrades.toList();
		for (int i = 0; i < allTrades.size(); i++) {
			Trade trade = trades.get(i);
			ITradeDetailsView tradeDetails = allTrades.get(i);
			assertEquals(trade.getId(), tradeDetails.getId());
			assertEquals(trade.getAmount(), tradeDetails.getAmount());
			assertEquals(trade.getUnitPrice(), tradeDetails.getUnitPrice());
			assertEquals(trade.getType(), tradeDetails.getType());
			assertEquals(trade.getTransactionDateTime(), tradeDetails.getTransactionDateTime());
			Coin coin = trade.getCoin();
			ICoinDetailsView coinDetails = tradeDetails.getCoin();
			assertEquals(coin.getId(), coinDetails.getId());
			assertEquals(coin.getSymbol(), coinDetails.getSymbol());
			assertEquals(coin.getName(), coinDetails.getName());
		}
	}

	@Test
	public void that_getTradeByIdAndTraderUsername_throws_when_trade_not_found() {
		long tradeId = 56;
		assertThrows(NotFoundException.class, () -> tradeService.getTradeByIdAndTraderUsername(tradeId, USERNAME));
		verify(mockTradeRepo).findByIdAndTraderUsername(tradeId, USERNAME);
	}

	@Test
	public void that_getTradeByIdAndTraderUsername_returns_tradeDetails_when_found() {
		long tradeId = 36;
		Coin coin = new Coin("etherium", "eth", "Etherium");
		Trade trade = new Trade(13, 100, BigDecimal.valueOf(150), TradeType.BUY, coin, null);
		when(mockTradeRepo.findByIdAndTraderUsername(tradeId, USERNAME)).thenReturn(Optional.of(trade));
		ITradeDetailsView tradeDetails = tradeService.getTradeByIdAndTraderUsername(tradeId, USERNAME);
		verify(mockTradeRepo).findByIdAndTraderUsername(tradeId, USERNAME);
		assertEquals(trade.getId(), tradeDetails.getId());
		assertEquals(trade.getAmount(), tradeDetails.getAmount());
		assertEquals(trade.getUnitPrice(), tradeDetails.getUnitPrice());
		assertEquals(trade.getType(), tradeDetails.getType());
		assertEquals(trade.getTransactionDateTime(), tradeDetails.getTransactionDateTime());
		ICoinDetailsView coinDetails = tradeDetails.getCoin();
		assertEquals(coin.getId(), coinDetails.getId());
		assertEquals(coin.getSymbol(), coinDetails.getSymbol());
		assertEquals(coin.getName(), coinDetails.getName());
	}

	@Test
	public void that_processTrade_throws_notFoundException_when_trader_not_found() {
		TradeForm tradeForm = new TradeForm(COIN_ID, BigDecimal.valueOf(25), 0L, TradeType.BUY);
		assertThrows(NotFoundException.class, () -> tradeService.processTrade(USERNAME, tradeForm));
		verify(mockTraderRepo).findByUsername(USERNAME);
	}

	@Test
	public void that_processTrade_throws_notFoundException_when_coin_not_found() {
		TradeForm tradeForm = new TradeForm(COIN_ID, BigDecimal.valueOf(125), 0L, TradeType.BUY);
		when(mockTraderRepo.findByUsername(USERNAME)).thenReturn(Optional.of(mockTrader));
		assertThrows(NotFoundException.class, () -> tradeService.processTrade(USERNAME, tradeForm));
		verify(mockTraderRepo).findByUsername(USERNAME);
		verify(mockCoinRepo).findById(COIN_ID);
	}

	@Test
	public void that_processTrade_with_invalid_trade_throws_invalidTradeException() throws InvalidTradeException {
		BigDecimal unitPrice = BigDecimal.valueOf(105);
		TradeForm tradeForm = new TradeForm(COIN_ID, BigDecimal.valueOf(12), 0L, TradeType.BUY);
		when(mockTraderRepo.findByUsername(USERNAME)).thenReturn(Optional.of(mockTrader));
		when(mockCoinRepo.findById(COIN_ID)).thenReturn(Optional.of(COIN));
		when(mockTrader.getCurrency()).thenReturn(CURRENCY);
		when(mockCryptoApiService.getCoinPriceById(COIN_ID, CURRENCY)).thenReturn(unitPrice);
		when(mockTradeFactory.createTrade(tradeForm, unitPrice, COIN, mockTrader)).thenReturn(mockTrade);
		doThrow(new InvalidTradeException("invalid trade")).when(mockTradeValidator).validate(mockTrade);

		assertThrows(InvalidTradeException.class, () -> tradeService.processTrade(USERNAME, tradeForm));
		verify(mockTraderRepo).findByUsername(USERNAME);
		verify(mockCoinRepo).findById(COIN_ID);
		verify(mockCryptoApiService).getCoinPriceById(COIN_ID, CURRENCY);
		verify(mockTradeValidator).validate(mockTrade);
	}

	@ParameterizedTest // amount,unitPrice
	@CsvSource({ "100,12.5", "35,4.00", "100000,0.018", "500,25.20" })
	public void that_processTrade_updates_trader_balance_and_updates_existing_holding(long amount, BigDecimal unitPrice)
			throws InvalidTradeException {
		long tradeId = 16;
		TradeType tradeType = TradeType.BUY;
		TradeForm tradeForm = new TradeForm(COIN_ID, BigDecimal.valueOf(1), amount, tradeType);
		BigDecimal tradeCost = BigDecimal.valueOf(amount).multiply(unitPrice, Constants.getMathContext());
		when(mockTraderRepo.findByUsername(USERNAME)).thenReturn(Optional.of(mockTrader));
		when(mockCoinRepo.findById(COIN_ID)).thenReturn(Optional.of(COIN));
		when(mockTrader.getCurrency()).thenReturn(CURRENCY);
		when(mockCryptoApiService.getCoinPriceById(COIN_ID, CURRENCY)).thenReturn(unitPrice);
		when(mockTradeFactory.createTrade(tradeForm, unitPrice, COIN, mockTrader)).thenReturn(mockTrade);
		when(mockHoldingRepo.findByCoinAndTrader(COIN, mockTrader)).thenReturn(Optional.of(mockHolding));
		when(mockHolding.getAmount()).thenReturn(10L);
		when(mockTradeRepo.save(mockTrade)).thenReturn(mockTrade);
		when(mockTrade.getId()).thenReturn(tradeId);
		when(mockTrade.getUnitPrice()).thenReturn(unitPrice);
		when(mockTrade.getAmount()).thenReturn(amount);
		when(mockTrade.getType()).thenReturn(tradeType);

		long savedTradeId = tradeService.processTrade(USERNAME, tradeForm);

		verify(mockTraderRepo).findByUsername(USERNAME);
		verify(mockCoinRepo).findById(COIN_ID);
		verify(mockTradeFactory).createTrade(tradeForm, unitPrice, COIN, mockTrader);
		verify(mockTradeValidator).validate(mockTrade);
		verify(mockTrader).subtractFromBalance(tradeCost);
		verify(mockHolding).adjustHolding(amount, unitPrice);
		verify(mockHoldingRepo).save(mockHolding);
		verify(mockTraderRepo).save(mockTrader);
		verify(mockTradeRepo).save(mockTrade);
		assertEquals(tradeId, savedTradeId);
	}

	@ParameterizedTest // amount,unitPrice
	@CsvSource({ "100,12.5", "35,4.00", "100000,0.018", "500,25.20" })
	public void that_processTrade_updates_trader_balance_and_holding_and_deletes_holding_when_all_sold(long amount,
			BigDecimal unitPrice) throws InvalidTradeException {
		long tradeId = 134;
		TradeType tradeType = TradeType.SELL;
		TradeForm tradeForm = new TradeForm(COIN_ID, BigDecimal.valueOf(1500), amount, tradeType);
		BigDecimal tradeCost = BigDecimal.valueOf(amount).multiply(unitPrice, Constants.getMathContext());
		Trade trade = new Trade(15, amount, unitPrice, tradeType, COIN, mockTrader);
		when(mockTraderRepo.findByUsername(USERNAME)).thenReturn(Optional.of(mockTrader));
		when(mockCoinRepo.findById(COIN_ID)).thenReturn(Optional.of(COIN));
		when(mockTrader.getCurrency()).thenReturn(CURRENCY);
		when(mockCryptoApiService.getCoinPriceById(COIN_ID, CURRENCY)).thenReturn(unitPrice);
		when(mockHoldingRepo.findByCoinAndTrader(COIN, mockTrader)).thenReturn(Optional.of(mockHolding));
		when(mockHolding.getAmount()).thenReturn(0L);
		when(mockTradeFactory.createTrade(tradeForm, unitPrice, COIN, mockTrader)).thenReturn(trade);
		when(mockTradeRepo.save(trade)).thenReturn(mockTrade);
		when(mockTrade.getId()).thenReturn(tradeId);

		tradeService.processTrade(USERNAME, tradeForm);

		verify(mockTraderRepo).findByUsername(USERNAME);
		verify(mockCoinRepo).findById(COIN_ID);
		verify(mockTradeFactory).createTrade(tradeForm, unitPrice, COIN, mockTrader);
		verify(mockTradeValidator).validate(trade);
		verify(mockTrader).addToBalance(tradeCost);
		verify(mockHolding).adjustHolding(-amount, unitPrice);
		verify(mockHoldingRepo).delete(mockHolding);
		verify(mockTraderRepo).save(mockTrader);
		verify(mockTradeRepo).save(trade);
	}

	@Test
	public void that_updateHolding_creates_and_returns_new_holding_when_doesnt_exist() {
		long buyAmount = 100L;
		BigDecimal unitPrice = BigDecimal.valueOf(15).setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
		when(mockTrade.getType()).thenReturn(TradeType.BUY);
		when(mockTrade.getAmount()).thenReturn(buyAmount);
		when(mockTrade.getUnitPrice()).thenReturn(unitPrice);

		Holding holding = tradeService.updateHolding(mockTrade, COIN, mockTrader);

		verify(mockHoldingRepo).findByCoinAndTrader(COIN, mockTrader);
		assertEquals(buyAmount, holding.getAmount());
		assertEquals(0, holding.getId());
		assertEquals(unitPrice, holding.getAverageUnitPrice());
	}

	@Test
	public void that_updateHolding_updates_and_returns_existing_holding() {
		long sellAmount = 100L;
		BigDecimal unitPrice = BigDecimal.valueOf(15).setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
		when(mockHoldingRepo.findByCoinAndTrader(COIN, mockTrader)).thenReturn(Optional.of(mockHolding));
		when(mockTrade.getType()).thenReturn(TradeType.SELL);
		when(mockTrade.getAmount()).thenReturn(sellAmount);
		when(mockTrade.getUnitPrice()).thenReturn(unitPrice);

		Holding holding = tradeService.updateHolding(mockTrade, COIN, mockTrader);

		verify(mockHoldingRepo).findByCoinAndTrader(COIN, mockTrader);
		verify(mockHolding).adjustHolding(-sellAmount, unitPrice);
		assertEquals(holding, mockHolding);
	}

	@Test
	public void that_updateTradeBalance_adds_tradeCost_to_balance_when_sell_trade() {
		long amount = 100;
		BigDecimal unitPrice = new BigDecimal("0.19234");
		BigDecimal totalCost = BigDecimal.valueOf(amount).multiply(unitPrice, Constants.getMathContext());
		Trade trade = new Trade(0, amount, unitPrice, TradeType.SELL, null, mockTrader);
		tradeService.updateTraderBalance(mockTrader, trade);
		verify(mockTrader).addToBalance(totalCost);
	}

	@Test
	public void that_updateTradeBalance_subtracts_tradeCost_from_balance_when_buy_trade() {
		long amount = 1012;
		BigDecimal unitPrice = new BigDecimal("12.345");
		BigDecimal totalCost = BigDecimal.valueOf(amount).multiply(unitPrice, Constants.getMathContext());
		Trade trade = new Trade(0, amount, unitPrice, TradeType.BUY, null, mockTrader);
		tradeService.updateTraderBalance(mockTrader, trade);
		verify(mockTrader).subtractFromBalance(totalCost);
	}

}
