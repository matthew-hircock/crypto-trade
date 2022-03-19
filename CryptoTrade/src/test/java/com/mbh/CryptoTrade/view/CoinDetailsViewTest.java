package com.mbh.CryptoTrade.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.view.CoinDetailsView;
import com.mbh.CryptoTrade.view.ICoinDetailsView;

public class CoinDetailsViewTest {

	@Test
	public void that_coinDetailsView_can_be_constructed_correctly() {
		Coin coin = new Coin("bitcoin", "btc", "Bitcoin");
		ICoinDetailsView coinDetails = new CoinDetailsView(coin);
		assertEquals(coin.getId(), coinDetails.getId());
		assertEquals(coin.getSymbol(), coinDetails.getSymbol());
		assertEquals(coin.getName(), coinDetails.getName());
		assertNull(coinDetails.getCurrentPrice());
	}

	@Test
	public void that_coinDetailsView_can_be_constructed_correctly_with_current_price() {
		BigDecimal currentPrice = BigDecimal.valueOf(100);
		Coin coin = new Coin("dogecoin", "doge", "Dogecoin");
		ICoinDetailsView coinDetails = new CoinDetailsView(coin, currentPrice);
		assertEquals(coin.getId(), coinDetails.getId());
		assertEquals(coin.getSymbol(), coinDetails.getSymbol());
		assertEquals(coin.getName(), coinDetails.getName());
		assertEquals(currentPrice, coinDetails.getCurrentPrice());
	}
	
}
