package com.mbh.CryptoTrade.view;

import java.math.BigDecimal;

import com.mbh.CryptoTrade.model.Holding;

public class HoldingDetailsView implements IHoldingDetailsView {

	private Holding holding;
	private ICoinDetailsView coin;

	public HoldingDetailsView(Holding holding) {
		super();
		this.holding = holding;
		this.coin = new CoinDetailsView(holding.getCoin());
	}

	@Override
	public long getAmount() {
		return holding.getAmount();
	}

	@Override
	public BigDecimal getAverageUnitPrice() {
		return holding.getAverageUnitPrice();
	}

	@Override
	public ICoinDetailsView getCoin() {
		return coin;
	}

}
