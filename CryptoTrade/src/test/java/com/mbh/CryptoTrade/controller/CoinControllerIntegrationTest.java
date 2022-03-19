package com.mbh.CryptoTrade.controller;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mbh.CryptoTrade.api.CryptoApiService;
import com.mbh.CryptoTrade.dal.CoinRepository;
import com.mbh.CryptoTrade.model.Coin;

@SpringBootTest
@AutoConfigureMockMvc
public class CoinControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private CoinRepository coinRepo;

	@MockBean
	private CryptoApiService mockCryptoApiService;

	@Transactional
	@Test
	public void that_getCoinByIdPage_gets_coinDetails_and_returns_coinPage_with_status200() throws Exception {
		String coinId = "dogecoin";
		String symbol = "doge";
		String name = "Dogecoin";
		BigDecimal coinPrice = new BigDecimal("0.1654");
		Coin coin = new Coin(coinId, symbol, name);
		coinRepo.save(coin);
		when(mockCryptoApiService.getCoinPriceById(coinId, null)).thenReturn(coinPrice);
		mvc.perform(get("/coins/" + coinId)).andDo(print()).andExpectAll(status().isOk(), view().name("coin"))
				.andExpectAll(model().attribute("coin", hasProperty("id", is(coinId))),
						model().attribute("coin", hasProperty("symbol", is(symbol))),
						model().attribute("coin", hasProperty("name", is(name))),
						model().attribute("coin", hasProperty("currentPrice", is(coinPrice))));
	}

	@Test
	public void that_getCoinByIdPage_returns_notFoundPage_with_status404_when_no_coin_in_database() throws Exception {
		String coinId = "fakecoin";
		mvc.perform(get("/coins/" + coinId)).andDo(print()).andExpectAll(status().isNotFound(), view().name("notFound"),
				model().attributeExists("error"));
	}

	@Transactional
	@Test
	public void that_getCoinsByPage_gets_coinDetails_and_returns_coinsPage_with_status200() throws Exception {
		String coinId1 = "bitcoin";
		Coin coin1 = new Coin(coinId1, "btc", "Bitcoin");
		String coinId2 = "ethereum";
		Coin coin2 = new Coin(coinId2, "eth", "Ethereum");
		BigDecimal coinPrice1 = new BigDecimal("53727");
		BigDecimal coinPrice2 = new BigDecimal("3620.82");
		coinRepo.saveAll(Arrays.asList(coin1, coin2));
		Map<String, BigDecimal> coinPrices = new HashMap<>();
		coinPrices.put(coinId1, coinPrice1);
		coinPrices.put(coinId2, coinPrice2);
		when(mockCryptoApiService.getCoinPricesByIds(Arrays.asList(coinId1, coinId2), null)).thenReturn(coinPrices);

		mvc.perform(get("/coins")).andDo(print()).andExpectAll(status().isOk(), view().name("coins"))
				.andExpectAll(model().attribute("coins", hasProperty("number", is(0))),
						model().attribute("coins", hasProperty("size", is(10))),
						model().attribute("coins", hasProperty("content", hasItems(hasProperty("id", is(coinId1)),
								hasProperty("symbol", is(coin1.getSymbol())), hasProperty("name", is(coin1.getName())),
								hasProperty("currentPrice", is(coinPrice1)), hasProperty("id", is(coinId2)),
								hasProperty("symbol", is(coin2.getSymbol())), hasProperty("name", is(coin2.getName())),
								hasProperty("currentPrice", is(coinPrice2))))));
	}

	@Transactional
	@Test
	public void that_getCoinsByPage_gets_coinDetails_and_returns_empty_coinsPage_with_status200() throws Exception {
		int pageNumber = 5;
		int pageSize = 20;

		mvc.perform(get("/coins").queryParam("pageNumber", Integer.toString(pageNumber)).queryParam("pageSize",
				Integer.toString(pageSize))).andDo(print()).andExpectAll(status().isOk(), view().name("coins"))
				.andExpectAll(model().attribute("coins", hasProperty("number", is(pageNumber))),
						model().attribute("coins", hasProperty("size", is(pageSize))));
	}

}
