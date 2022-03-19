package com.mbh.CryptoTrade.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.mbh.CryptoTrade.model.Currency;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.view.ITraderDetailsView;
import com.mbh.CryptoTrade.view.TraderDetailsView;

public class TraderDetailsViewTest {

	@Test
	public void that_traderDetailsView_is_constructed_correctly() {
		Trader trader = new Trader(1L, "matthew", "password");
		trader.setBalance(BigDecimal.valueOf(100000));
		trader.setCurrency(Currency.AUD);
		ITraderDetailsView traderDetails = new TraderDetailsView(trader);
		assertEquals(trader.getUsername(), traderDetails.getUsername());
		assertEquals(trader.getBalance(), traderDetails.getBalance());
		assertEquals(trader.getCurrency(), traderDetails.getCurrency());
	}

}
