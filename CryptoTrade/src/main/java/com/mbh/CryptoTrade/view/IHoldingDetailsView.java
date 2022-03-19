package com.mbh.CryptoTrade.view;

import java.math.BigDecimal;

public interface IHoldingDetailsView {

	long getAmount();
	
	BigDecimal getAverageUnitPrice();
	
	ICoinDetailsView getCoin();
	
	
}
