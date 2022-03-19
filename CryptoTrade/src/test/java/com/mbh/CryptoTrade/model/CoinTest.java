package com.mbh.CryptoTrade.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mbh.CryptoTrade.model.Coin;

public class CoinTest {

	@Test
	public void that_coin_is_constructed_correctly() {
		String id = "bitcoin";
		String symbol = "btc";
		String name = "Bitcoin";
		Coin coin = new Coin(id, symbol, name);
		assertEquals(id, coin.getId());
		assertEquals(symbol, coin.getSymbol());
		assertEquals(name, coin.getName());
	}
	
	@Test
	public void that_coin_setters_work_correctly() {
		String id = "ethereum";
		String symbol = "eth";
		String name = "Ethereum";
		Coin coin = new Coin();
		coin.setId(id);
		coin.setSymbol(symbol);
		coin.setName(name);
		assertEquals(id, coin.getId());
		assertEquals(symbol, coin.getSymbol());
		assertEquals(name, coin.getName());
	}
	
}
