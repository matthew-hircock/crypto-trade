package com.mbh.CryptoTrade.view;

import java.math.BigDecimal;

import com.mbh.CryptoTrade.model.Currency;
import com.mbh.CryptoTrade.model.Trader;

public class TraderDetailsView implements ITraderDetailsView {

	private Trader trader;

	public TraderDetailsView(Trader trader) {
		super();
		this.trader = trader;
	}

	@Override
	public String getUsername() {
		return trader.getUsername();
	}

	@Override
	public BigDecimal getBalance() {
		return trader.getBalance();
	}

	@Override
	public Currency getCurrency() {
		return trader.getCurrency();
	}

}
