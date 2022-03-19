package com.mbh.CryptoTrade.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Holding;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.view.HoldingDetailsView;
import com.mbh.CryptoTrade.view.ICoinDetailsView;
import com.mbh.CryptoTrade.view.IHoldingDetailsView;

public class HoldingDetailsViewTest {

	@Test
	public void that_holdingDetailsView_is_constructed_correctly() {
		Trader trader = new Trader();
		Coin coin = new Coin("bitcoin", "btc", "Bitcoin");
		Holding holding = new Holding(100, BigDecimal.valueOf(150), coin, trader);
		IHoldingDetailsView holdingDetails = new HoldingDetailsView(holding);
		assertEquals(holding.getAmount(), holdingDetails.getAmount());
		assertEquals(holding.getAverageUnitPrice(), holdingDetails.getAverageUnitPrice());
		ICoinDetailsView coinDetails = holdingDetails.getCoin();
		assertEquals(coin.getId(), coinDetails.getId());
		assertEquals(coin.getSymbol(), coinDetails.getSymbol());
		assertEquals(coin.getName(), coinDetails.getName());
	}
	
	// test with current price for coin?
	
}
