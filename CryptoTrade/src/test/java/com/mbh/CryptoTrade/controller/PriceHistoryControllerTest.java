package com.mbh.CryptoTrade.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mbh.CryptoTrade.controller.PriceHistoryController;
import com.mbh.CryptoTrade.service.PriceHistoryService;

@SpringBootTest
public class PriceHistoryControllerTest {

	@Autowired
	private PriceHistoryController priceHistoryController;

	@MockBean
	private PriceHistoryService mockPriceHistoryService;

	@Test
	public void that_getCoinPriceHistoryById_returns_coinPriceHistory_from_service() {
		String coinId = "bitcoin";
		int days = 7;
		Map<Long, BigDecimal> priceHistory = new HashMap<>();
		priceHistory.put(12345678L, BigDecimal.valueOf(125));
		priceHistory.put(13579135L, BigDecimal.valueOf(145));
		priceHistory.put(15924845L, BigDecimal.valueOf(110));
		when(mockPriceHistoryService.getPriceHistoryByCoinId(coinId, null, days)).thenReturn(priceHistory);
		Map<Long, BigDecimal> foundPriceHistory = priceHistoryController.getCoinPriceHistoryById(coinId, days);
		verify(mockPriceHistoryService).getPriceHistoryByCoinId(coinId, null, days);
		assertEquals(priceHistory, foundPriceHistory);
	}

}
