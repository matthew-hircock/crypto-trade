package com.mbh.CryptoTrade.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Trader {

	@Id
	@GeneratedValue
	private long id;
	private String username;
	private String password;
	@Column(precision = 19, scale = 8)
	private BigDecimal balance;
	private Currency currency;

	public Trader() {
		super();
	}

	public Trader(long id, String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}
	
	public void addToBalance(BigDecimal amount) {
		setBalance(balance.add(amount));
	}
	
	public void subtractFromBalance(BigDecimal amount) {
		setBalance(balance.subtract(amount));
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

}
