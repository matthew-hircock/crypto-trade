package com.mbh.CryptoTrade.view;

import java.math.BigDecimal;

public interface ICoinDetailsView {

	String getId();
	
	String getSymbol();
	
	String getName();
	
	BigDecimal getCurrentPrice();
	
}
