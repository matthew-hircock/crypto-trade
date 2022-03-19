package com.mbh.CryptoTrade.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Coin {

	@Id
	private String id;
	private String symbol;
	private String name;

	public Coin() {
		super();
	}

	public Coin(String id, String symbol, String name) {
		super();
		this.id = id;
		this.symbol = symbol;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
