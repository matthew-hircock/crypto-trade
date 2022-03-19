package com.mbh.CryptoTrade.view;

import java.math.BigDecimal;

import com.mbh.CryptoTrade.model.Currency;

public interface ITraderDetailsView {

	String getUsername();
	
	BigDecimal getBalance();
	
	Currency getCurrency();
	
}
