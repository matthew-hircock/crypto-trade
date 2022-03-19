package com.mbh.CryptoTrade.model;

public enum Currency {
	AUD("aud");

	private String value;

	private Currency(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
