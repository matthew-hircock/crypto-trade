package com.mbh.CryptoTrade.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.mbh.CryptoTrade.setup.Constants;

@Entity
public class Holding {
	
	@Id
	@GeneratedValue
	private long id;
	private long amount;
	@Column(precision = 19, scale = 6)
	private BigDecimal averageUnitPrice;
	@ManyToOne
	@JoinColumn(name = "coin_id")
	private Coin coin;
	@ManyToOne
	@JoinColumn(name = "trader_id")
	private Trader trader;

	public Holding() {
		super();
	}

	public Holding(long amount, BigDecimal averageUnitPrice, Coin coin, Trader trader) {
		super();
		this.amount = amount;
		this.averageUnitPrice = averageUnitPrice;
		this.coin = coin;
		this.trader = trader;
	}

	public void adjustHolding(long adjustmentAmount, BigDecimal unitPrice) {
		if (adjustmentAmount >= 0) {
			BigDecimal adjustedAveragePrice = (new BigDecimal(amount).multiply(averageUnitPrice)
					.add(new BigDecimal(adjustmentAmount).multiply(unitPrice)))
							.divide(new BigDecimal(amount + adjustmentAmount), Constants.getMathContext());
			setAverageUnitPrice(adjustedAveragePrice);
		}
		setAmount(amount + adjustmentAmount);
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

	public BigDecimal getAverageUnitPrice() {
		return averageUnitPrice;
	}

	public void setAverageUnitPrice(BigDecimal averageUnitPrice) {
		this.averageUnitPrice = averageUnitPrice;
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
