package com.mbh.CryptoTrade.validation;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mbh.CryptoTrade.dal.HoldingRepository;
import com.mbh.CryptoTrade.exception.InvalidTradeException;
import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.model.Trader;

@Component
public class TradeValidator {

	private HoldingRepository holdingRepo;

	@Autowired
	public TradeValidator(HoldingRepository holdingRepo) {
		super();
		this.holdingRepo = holdingRepo;
	}

	/**
	 * @param trade
	 *            the trade to be validated
	 * @throws InvalidTradeException
	 *             If the trade is a buy trade and the total cost is greater than
	 *             trader balance, or if the trade is a sell trade and either the
	 *             coin is not held or the amount selling is greater than the amount
	 *             held
	 */
	public void validate(Trade trade) throws InvalidTradeException {
		Trader trader = trade.getTrader();
		BigDecimal tradeCost = trade.getUnitPrice().multiply(BigDecimal.valueOf(trade.getAmount()));
		// if buying more than can be afforded
		if (trade.getType() == TradeType.BUY && trader.getBalance().compareTo(tradeCost) < 0) {
			throw new InvalidTradeException("Insufficient funds");

		}
		// if selling more than held
		if (trade.getType() == TradeType.SELL) {
			// get total number of units held
			long unitsHeld = holdingRepo.findByCoinAndTrader(trade.getCoin(), trader)
					.orElseThrow(() -> new InvalidTradeException("You do not own this stock")).getAmount();
			if (unitsHeld < trade.getAmount()) {
				throw new InvalidTradeException("You do not own enough to sell");
			}
		}
	}

}
