package com.mbh.CryptoTrade.view;

import java.math.BigDecimal;

import com.mbh.CryptoTrade.model.Coin;

public class CoinDetailsView implements ICoinDetailsView {

	private Coin coin;
	private BigDecimal currentPrice;

	public CoinDetailsView(Coin coin) {
		super();
		this.coin = coin;
	}

	public CoinDetailsView(Coin coin, BigDecimal currentPrice) {
		super();
		this.coin = coin;
		this.currentPrice = currentPrice;
	}

	@Override
	public String getId() {
		return coin.getId();
	}

	@Override
	public String getSymbol() {
		return coin.getSymbol();
	}

	@Override
	public String getName() {
		return coin.getName();
	}

	@Override
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}

}
