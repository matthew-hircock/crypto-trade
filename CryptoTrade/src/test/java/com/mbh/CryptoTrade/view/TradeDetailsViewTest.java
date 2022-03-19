package com.mbh.CryptoTrade.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.view.ICoinDetailsView;
import com.mbh.CryptoTrade.view.ITradeDetailsView;
import com.mbh.CryptoTrade.view.TradeDetailsView;

public class TradeDetailsViewTest {

	@Test
	public void that_tradeDetailsView_can_be_constructed_correctly() {
		Trader trader = new Trader();
		Coin coin = new Coin("bitcoin", "btc", "Bitcoin");
		Trade trade = new Trade(6, 100, BigDecimal.valueOf(100), TradeType.BUY, coin, trader);
		ITradeDetailsView tradeDetails = new TradeDetailsView(trade);
		assertEquals(trade.getId(), tradeDetails.getId());
		assertEquals(trade.getAmount(), tradeDetails.getAmount());
		assertEquals(trade.getUnitPrice(), tradeDetails.getUnitPrice());
		assertEquals(trade.getType(), tradeDetails.getType());
		assertEquals(trade.getTransactionDateTime(), tradeDetails.getTransactionDateTime());
		ICoinDetailsView coinDetails = tradeDetails.getCoin();
		assertEquals(coin.getId(), coinDetails.getId());
		assertEquals(coin.getSymbol(), coinDetails.getSymbol());
		assertEquals(coin.getName(), coinDetails.getName());
	}

}
