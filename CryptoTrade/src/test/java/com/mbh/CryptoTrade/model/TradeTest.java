package com.mbh.CryptoTrade.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.model.Trader;

public class TradeTest {

	@Test
	public void that_trade_is_constructed_correctly() {
		long id = 4;
		long amount = 1234;
		BigDecimal unitPrice = new BigDecimal("42.5");
		TradeType type = TradeType.BUY;
		Coin coin = new Coin("ethereum", "eth", "Ethereum");
		Trader trader = new Trader(12, "matthew.h", "password");
		Trade trade = new Trade(id, amount, unitPrice, type, coin, trader);
		assertEquals(id, trade.getId());
		assertEquals(amount, trade.getAmount());
		assertEquals(unitPrice, trade.getUnitPrice());
		assertEquals(type, trade.getType());
		assertEquals(coin, trade.getCoin());
		assertEquals(trader, trade.getTrader());
		assertNotNull(trade.getTransactionDateTime());
	}

	@Test
	public void that_trade_setters_work_correctly() {
		long id = 66;
		long amount = 13234;
		BigDecimal unitPrice = new BigDecimal("1.5");
		TradeType type = TradeType.SELL;
		LocalDateTime transactionDateTime = LocalDateTime.now();
		Coin coin = new Coin("ethereum", "eth", "Ethereum");
		Trader trader = new Trader(12, "matthew.h", "password");
		Trade trade = new Trade();
		trade.setId(id);
		trade.setAmount(amount);
		trade.setUnitPrice(unitPrice);
		trade.setType(type);
		trade.setTransactionDateTime(transactionDateTime);
		trade.setCoin(coin);
		trade.setTrader(trader);
		assertEquals(id, trade.getId());
		assertEquals(amount, trade.getAmount());
		assertEquals(unitPrice, trade.getUnitPrice());
		assertEquals(type, trade.getType());
		assertEquals(transactionDateTime, trade.getTransactionDateTime());
		assertEquals(coin, trade.getCoin());
		assertEquals(trader, trade.getTrader());
	}

}
