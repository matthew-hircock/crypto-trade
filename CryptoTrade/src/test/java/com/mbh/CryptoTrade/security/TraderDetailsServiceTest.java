package com.mbh.CryptoTrade.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mbh.CryptoTrade.dal.TraderRepository;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.security.TraderDetailsService;

@SpringBootTest
public class TraderDetailsServiceTest {

	@Autowired
	private TraderDetailsService traderDetailsService;

	@MockBean
	private TraderRepository mockTraderRepo;

	@Test
	public void that_loadUserByUsername_throws_when_no_user_found() {
		String username = "matthew";
		assertThrows(UsernameNotFoundException.class, () -> traderDetailsService.loadUserByUsername(username));
		verify(mockTraderRepo).findByUsername(username);
	}

	@Test
	public void that_loadUserByUsername_returns_userDetails_when_user_found() {
		String username = "matthew";
		String password = "password123";
		Trader trader = new Trader(0L, username, password);
		when(mockTraderRepo.findByUsername(username)).thenReturn(Optional.of(trader));
		UserDetails userDetails = traderDetailsService.loadUserByUsername(username);
		verify(mockTraderRepo).findByUsername(username);
		assertEquals(username, userDetails.getUsername());
		assertEquals(password, userDetails.getPassword());
	}

}
