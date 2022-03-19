package com.mbh.CryptoTrade.component;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.view.TradeForm;

@Component
public class TradeFactory {

	/**
	 * @param tradeForm
	 *            the form containing the amount and trade type (BUY or SELL)
	 * @param unitPrice
	 *            the price for each unit being traded
	 * @param coin
	 *            the coin being traded
	 * @param trader
	 *            the trader making the trade
	 * @return a transient trade object (id of 0) containing the relevant trade
	 *         information
	 */
	public Trade createTrade(TradeForm tradeForm, BigDecimal unitPrice, Coin coin, Trader trader) {
		return new Trade(0, tradeForm.getAmount(), unitPrice, tradeForm.getType(), coin, trader);
	}

}
