package com.mbh.CryptoTrade.view;

import java.math.BigDecimal;

import com.mbh.CryptoTrade.model.TradeType;

public class TradeForm {

	private String coinId;
	private BigDecimal currentPrice;
	private long amount;
	private TradeType type;

	public TradeForm() {
		super();
	}

	public TradeForm(String coinId, BigDecimal currentPrice, long amount, TradeType type) {
		super();
		this.coinId = coinId;
		this.currentPrice = currentPrice;
		this.amount = amount;
		this.type = type;
	}

	public String getCoinId() {
		return coinId;
	}

	public void setCoinId(String coinId) {
		this.coinId = coinId;
	}

	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public TradeType getType() {
		return type;
	}

	public void setType(TradeType type) {
		this.type = type;
	}

}
