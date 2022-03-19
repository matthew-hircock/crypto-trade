package com.mbh.CryptoTrade.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.TradeType;

public class TradeDetailsView implements ITradeDetailsView {

	private Trade trade;
	private ICoinDetailsView coin;

	public TradeDetailsView(Trade trade) {
		super();
		this.trade = trade;
		this.coin = new CoinDetailsView(trade.getCoin());
	}

	@Override
	public long getId() {
		return trade.getId();
	}

	@Override
	public long getAmount() {
		return trade.getAmount();
	}

	@Override
	public BigDecimal getUnitPrice() {
		return trade.getUnitPrice();
	}

	@Override
	public TradeType getType() {
		return trade.getType();
	}

	@Override
	public LocalDateTime getTransactionDateTime() {
		return trade.getTransactionDateTime();
	}

	@Override
	public ICoinDetailsView getCoin() {
		return coin;
	}

}
