package com.mbh.CryptoTrade.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Trade {

	@Id
	@GeneratedValue
	private long id;
	private long amount;
	@Column(precision = 19, scale = 6)
	private BigDecimal unitPrice;
	private TradeType type;
	private LocalDateTime transactionDateTime;
	@ManyToOne
	@JoinColumn(name = "coin_id")
	private Coin coin;
	@ManyToOne
	@JoinColumn(name = "trader_id")
	private Trader trader;

	public Trade() {
		super();
		this.transactionDateTime = LocalDateTime.now();
	}

	public Trade(long id, long amount, BigDecimal unitPrice, TradeType type, Coin coin, Trader trader) {
		this();
		this.id = id;
		this.amount = amount;
		this.unitPrice = unitPrice;
		this.type = type;
		this.coin = coin;
		this.trader = trader;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public TradeType getType() {
		return type;
	}

	public void setType(TradeType type) {
		this.type = type;
	}

	public LocalDateTime getTransactionDateTime() {
		return transactionDateTime;
	}

	public void setTransactionDateTime(LocalDateTime transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}

	public Coin getCoin() {
		return coin;
	}

	public void setCoin(Coin coin) {
		this.coin = coin;
	}

	public Trader getTrader() {
		return trader;
	}

	public void setTrader(Trader trader) {
		this.trader = trader;
	}

}
