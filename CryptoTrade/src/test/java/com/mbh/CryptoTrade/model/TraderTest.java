package com.mbh.CryptoTrade.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.mbh.CryptoTrade.model.Currency;
import com.mbh.CryptoTrade.model.Trader;

public class TraderTest {

	private static final Currency CURRENCY = Currency.AUD;

	@Test
	public void that_trader_can_be_cosntructed_correctly() {
		long id = 15;
		String username = "matthew.h";
		String password = "password";
		Trader trader = new Trader(id, username, password);
		assertEquals(id, trader.getId());
		assertEquals(username, trader.getUsername());
		assertEquals(password, trader.getPassword());
	}

	@Test
	public void that_trader_setters_work_correctly() {
		long id = 15;
		String username = "matthew.h";
		String password = "password";
		BigDecimal balance = BigDecimal.valueOf(1250);
		Trader trader = new Trader();
		trader.setId(id);
		trader.setUsername(username);
		trader.setPassword(password);
		trader.setBalance(balance);
		trader.setCurrency(CURRENCY);
		assertEquals(id, trader.getId());
		assertEquals(username, trader.getUsername());
		assertEquals(password, trader.getPassword());
		assertEquals(balance, trader.getBalance());
		assertEquals(CURRENCY, trader.getCurrency());
	}

	@ParameterizedTest // balance,amountToAdd
	@CsvSource({ "10000,1500", "1560.50,360.25", "10023,50.70" })
	public void that_addToBalance_adds_amount_to_balance(BigDecimal balance, BigDecimal amountToAdd) {
		Trader trader = new Trader();
		trader.setBalance(balance);
		trader.addToBalance(amountToAdd);
		assertEquals(trader.getBalance(), balance.add(amountToAdd));
	}

	@ParameterizedTest // balance,amountToSubtract
	@CsvSource({ "10000,1500", "1560.50,360.25", "10023,50.70" })
	public void that_subtractFromBalance_subracts_amount_from_balance(BigDecimal balance, BigDecimal amountToSubtract) {
		Trader trader = new Trader();
		trader.setBalance(balance);
		trader.subtractFromBalance(amountToSubtract);
		assertEquals(trader.getBalance(), balance.subtract(amountToSubtract));
	}

}
