package com.mbh.CryptoTrade.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mbh.CryptoTrade.component.TradeFactory;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.view.TradeForm;

@SpringBootTest
public class TradeFactoryTest {

	@Autowired
	private TradeFactory tradeFactory;

	@Test
	public void that_createTrade_can_create_trade_successfully() {
		String coinId = "bitcoin";
		BigDecimal unitPrice = BigDecimal.valueOf(150);
		TradeForm tradeForm = new TradeForm(coinId, BigDecimal.valueOf(125), 100, TradeType.BUY);
		Coin coin = new Coin(coinId, "btc", "Bitcoin");
		Trader trader = new Trader(13, "matt.h", "password");
		Trade trade = tradeFactory.createTrade(tradeForm, unitPrice, coin, trader);
		assertEquals(tradeForm.getAmount(), trade.getAmount());
		assertEquals(tradeForm.getType(), trade.getType());
		assertEquals(unitPrice, trade.getUnitPrice());
		assertEquals(coin, trade.getCoin());
		assertEquals(trader, trade.getTrader());
	}

}
