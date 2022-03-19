package com.mbh.CryptoTrade.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Holding;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.setup.Constants;

public class HoldingTest {

	@Test
	public void that_holding_is_constructed_correctly() {
		long amount = 1000;
		BigDecimal averageUnitPrice = new BigDecimal("125.60");
		Coin coin = new Coin("bitcoin", "btc", "Bitcoin");
		Trader trader = new Trader(13, "matthew.h", "password");
		Holding holding = new Holding(amount, averageUnitPrice, coin, trader);
		assertEquals(amount, holding.getAmount());
		assertEquals(averageUnitPrice, holding.getAverageUnitPrice());
		assertEquals(coin, holding.getCoin());
		assertEquals(trader, holding.getTrader());
	}

	@Test
	public void that_holding_setters_work_correctly() {
		long id = 45;
		long amount = 1000;
		BigDecimal averageUnitPrice = new BigDecimal("125.60");
		Coin coin = new Coin("bitcoin", "btc", "Bitcoin");
		Trader trader = new Trader(13, "matthew.h", "password");
		Holding holding = new Holding();
		holding.setId(id);
		holding.setAmount(amount);
		holding.setAverageUnitPrice(averageUnitPrice);
		holding.setCoin(coin);
		holding.setTrader(trader);
		assertEquals(id, holding.getId());
		assertEquals(amount, holding.getAmount());
		assertEquals(averageUnitPrice, holding.getAverageUnitPrice());
		assertEquals(coin, holding.getCoin());
		assertEquals(trader, holding.getTrader());
	}

	@ParameterizedTest // "holdingAmount,adjustmentAmount"
	@CsvSource({ "155,-25", "12423,-3454", "3,-3", "234,-65" })
	public void that_adjustment_amount_less_than_zero_only_updates_amount(long holdingAmount, long adjustmentAmount) {
		BigDecimal unitPrice = BigDecimal.valueOf(150);
		Holding holding = new Holding(holdingAmount, unitPrice, null, null);
		holding.adjustHolding(adjustmentAmount, unitPrice);
		long expectedHoldingAmount = holdingAmount + adjustmentAmount;
		assertEquals(expectedHoldingAmount, holding.getAmount());
		assertEquals(unitPrice, holding.getAverageUnitPrice());
	}

	// important to test values that might give a repeating decimal
	@ParameterizedTest // "holdingAmount,currentAverageUnitPrice,adjustmentAmount,tradeAverageUnitPrice"
	@CsvSource({ "155,10.50,25,12.56", "12423,11,3454,9.56", "3,25,3,45", "234,23.56,65,18.23", "1,11.00,1,9.00" })
	public void that_adjustment_amount_greater_than_zero_updates_amount_and_average_unit_price(long holdingAmount,
			BigDecimal currentAverageUnitPrice, long adjustmentAmount, BigDecimal tradeAverageUnitPrice) {
		Holding holding = new Holding(holdingAmount, currentAverageUnitPrice, null, null);
		holding.adjustHolding(adjustmentAmount, tradeAverageUnitPrice);
		long expectedHoldingAmount = holdingAmount + adjustmentAmount;
		BigDecimal expectedAverageUnitPrice = (new BigDecimal(holdingAmount).multiply(currentAverageUnitPrice)
				.add(new BigDecimal(adjustmentAmount).multiply(tradeAverageUnitPrice)))
						.divide(new BigDecimal(expectedHoldingAmount), Constants.getMathContext());
		assertEquals(expectedHoldingAmount, holding.getAmount());
		assertEquals(expectedAverageUnitPrice, holding.getAverageUnitPrice());
	}

	// constant for math context?

}
