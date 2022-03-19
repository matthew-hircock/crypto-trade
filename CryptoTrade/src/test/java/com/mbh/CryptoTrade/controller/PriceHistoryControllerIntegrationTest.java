package com.mbh.CryptoTrade.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.mbh.CryptoTrade.api.CryptoApiService;

@SpringBootTest
@AutoConfigureMockMvc
public class PriceHistoryControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private CryptoApiService mockCryptoApiService;

	@Test
	public void that_getCoinPriceHistoryById_returns_coinPriceHistory_with_status200_when_coin_exists()
			throws Exception {
		String coinId = "bitcoin";
		int days = 7;
		Map<Long, BigDecimal> priceHistory = new HashMap<>();
		priceHistory.put(123456789L, BigDecimal.valueOf(12345));
		priceHistory.put(135792468L, BigDecimal.valueOf(15085));
		priceHistory.put(147025836L, BigDecimal.valueOf(19025));
		when(mockCryptoApiService.getCoinPriceHistoryById(coinId, null, days)).thenReturn(priceHistory);
		ResultActions resultActions = mvc
				.perform(get("/coins/" + coinId + "/priceHistory").queryParam("days", Integer.toString(days)))
				.andDo(print()).andExpect(status().isOk());
		for (Entry<Long, BigDecimal> entry : priceHistory.entrySet()) {
			resultActions.andExpect(jsonPath("$['" + entry.getKey() + "']").value(entry.getValue()));
		}
	}

	@Test
	public void that_getCoinPriceHistoryById_returns_status404_with_errorMessage_when_coin_doesnt_exist()
			throws Exception {
		String coinId = "fakecoin";
		int days = 14;
		String cause = "Not found";
		when(mockCryptoApiService.getCoinPriceHistoryById(coinId, null, days))
				.thenThrow(new WebClientResponseException(cause, 404, null, null, null, null));
		mvc.perform(get("/coins/" + coinId + "/priceHistory").queryParam("days", Integer.toString(days))).andDo(print())
				.andExpectAll(status().isNotFound(), jsonPath("message").exists(), jsonPath("cause").value(cause));
	}

}
