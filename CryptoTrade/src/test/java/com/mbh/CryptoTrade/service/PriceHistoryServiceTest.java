package com.mbh.CryptoTrade.service;

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

import com.mbh.CryptoTrade.api.CryptoApiService;
import com.mbh.CryptoTrade.model.Currency;
import com.mbh.CryptoTrade.service.PriceHistoryService;

@SpringBootTest
public class PriceHistoryServiceTest {

	@Autowired
	private PriceHistoryService priceHistoryService;

	@MockBean
	private CryptoApiService mockCryptoApiService;

	@Test
	public void that_getPriceHistoryByCoinId_returns_priceHistoryData_from_cryptoApiService() {
		String coinId = "bitcoin";
		Currency currency = Currency.AUD;
		int days = 7;
		Map<Long, BigDecimal> priceHistory = new HashMap<>();
		priceHistory.put(12345678L, BigDecimal.valueOf(125));
		priceHistory.put(13579135L, BigDecimal.valueOf(145));
		priceHistory.put(15924845L, BigDecimal.valueOf(110));
		when(mockCryptoApiService.getCoinPriceHistoryById(coinId, currency, days)).thenReturn(priceHistory);
		Map<Long, BigDecimal> foundPriceHistory = priceHistoryService.getPriceHistoryByCoinId(coinId, currency, days);
		verify(mockCryptoApiService).getCoinPriceHistoryById(coinId, currency, days);
		assertEquals(priceHistory, foundPriceHistory);
	}

}
