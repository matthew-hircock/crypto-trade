package com.mbh.CryptoTrade.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.security.TraderDetails;

public class TraderDetailsTest {

	@Test
	public void that_traderDetails_has_details_of_trader() {
		Trader trader = new Trader(0L, "matthew", "password123");
		TraderDetails traderDetails = new TraderDetails(trader);
		
		assertEquals(trader.getUsername(), traderDetails.getUsername());
		assertEquals(trader.getPassword(), traderDetails.getPassword());
		
		assertTrue(traderDetails.isAccountNonExpired());
		assertTrue(traderDetails.isAccountNonLocked());
		assertTrue(traderDetails.isCredentialsNonExpired());
		assertTrue(traderDetails.isEnabled());

		// following can be changed if roles are added
		assertTrue(traderDetails.getAuthorities().isEmpty());
	}
	
}
