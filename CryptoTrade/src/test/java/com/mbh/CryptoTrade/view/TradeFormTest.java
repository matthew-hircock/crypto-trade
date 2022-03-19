package com.mbh.CryptoTrade.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.view.TradeForm;

public class TradeFormTest {

	@Test
	public void that_tradeForm_is_constructed_correctly() {
		String coinId = "dogecoin";
		BigDecimal currentPrice = new BigDecimal("12345.60");
		long amount = 1234;
		TradeType type = TradeType.BUY;
		TradeForm tradeForm = new TradeForm(coinId, currentPrice, amount, type);
		assertEquals(coinId, tradeForm.getCoinId());
		assertEquals(currentPrice, tradeForm.getCurrentPrice());
		assertEquals(amount, tradeForm.getAmount());
		assertEquals(type, tradeForm.getType());
	}
	
	@Test
	public void that_tradeForm_setters_work_correctly() {
		String coinId = "ethereum";
		BigDecimal currentPrice = new BigDecimal("15.25");
		long amount = 12134;
		TradeType type = TradeType.SELL;
		TradeForm tradeForm = new TradeForm();
		tradeForm.setCoinId(coinId);
		tradeForm.setCurrentPrice(currentPrice);
		tradeForm.setAmount(amount);
		tradeForm.setType(type);
		assertEquals(coinId, tradeForm.getCoinId());
		assertEquals(currentPrice, tradeForm.getCurrentPrice());
		assertEquals(amount, tradeForm.getAmount());
		assertEquals(type, tradeForm.getType());
	}
	
}
