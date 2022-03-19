package com.mbh.CryptoTrade.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.Model;

import com.mbh.CryptoTrade.controller.CoinController;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Currency;
import com.mbh.CryptoTrade.service.CoinService;
import com.mbh.CryptoTrade.view.CoinDetailsView;
import com.mbh.CryptoTrade.view.ICoinDetailsView;

@SpringBootTest
public class CoinControllerTest {

	@Autowired
	private CoinController coinController;

	@MockBean
	private CoinService mockCoinService;
	@MockBean
	private Model mockModel;

	@Test
	public void that_getAllCoinsPage_adds_all_coins_to_model_and_returns_coinsPage() {
		Currency currency = null;
		int pageNumber = 5;
		int pageSize = 10;
		Page<ICoinDetailsView> coins = new PageImpl<>(
				Arrays.asList(new CoinDetailsView(new Coin()), new CoinDetailsView(new Coin())));
		when(mockCoinService.getCoinsByPage(pageNumber, pageSize, currency)).thenReturn(coins);
		
		String nextPage = coinController.getAllCoinsPage(mockModel, pageNumber, pageSize);
		
		verify(mockCoinService).getCoinsByPage(pageNumber, pageSize, currency);
		verify(mockModel).addAttribute(CoinController.COINS_KEY, coins);
		assertEquals(CoinController.COINS_PAGE, nextPage);
	}

	@Test
	public void that_getCoinByIdPage_adds_coin_and_empty_trades_to_model_and_returns_coinPage() {
		String id = "bitcoin";
		ICoinDetailsView coin = new CoinDetailsView(new Coin());
		when(mockCoinService.getCoinById(id, null)).thenReturn(coin);
		
		String nextPage = coinController.getCoinByIdPage(id, mockModel);
		
		verify(mockCoinService).getCoinById(id, null);
		verify(mockModel).addAttribute(CoinController.COIN_KEY, coin);
		assertEquals(CoinController.COIN_PAGE, nextPage);
	}

}
