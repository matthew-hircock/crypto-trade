package com.mbh.CryptoTrade.view;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.mbh.CryptoTrade.model.TradeType;

public interface ITradeDetailsView {

	long getId();
	
	long getAmount();
	
	BigDecimal getUnitPrice();
	
	TradeType getType();
	
	LocalDateTime getTransactionDateTime();
	
	ICoinDetailsView getCoin();
	
}
