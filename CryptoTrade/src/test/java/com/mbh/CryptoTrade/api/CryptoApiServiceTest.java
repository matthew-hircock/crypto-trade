package com.mbh.CryptoTrade.api;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.mbh.CryptoTrade.api.CryptoApiService;
import com.mbh.CryptoTrade.exception.NotFoundException;
import com.mbh.CryptoTrade.model.Currency;

@SpringBootTest
public class CryptoApiServiceTest {

	@RegisterExtension
	static WireMockExtension wireMockServer = WireMockExtension.newInstance().options(wireMockConfig().dynamicPort())
			.build();

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("crypto.api.url", wireMockServer::baseUrl);
	}

	@Autowired
	private CryptoApiService cryptoApiService;

	@Test
	public void that_getCoinPriceById_throws_when_coin_not_found_in_api() {
		String coinId = "fakecoin";
		String currency = "aud";
		wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo(CryptoApiService.PRICE_BY_ID))
				.withQueryParam(CryptoApiService.IDS, WireMock.equalTo(coinId))
				.withQueryParam(CryptoApiService.VS_CURRENCIES, WireMock.equalTo(currency))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200).withBody("")));

		assertThrows(NotFoundException.class, () -> cryptoApiService.getCoinPriceById(coinId, Currency.AUD));
	}

	@Test
	public void that_getCoinPriceById_throws_when_no_price_found_for_coin() {
		String coinId = "fakecoin";
		String currency = "aud";
		wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo(CryptoApiService.PRICE_BY_ID))
				.withQueryParam(CryptoApiService.IDS, WireMock.equalTo(coinId))
				.withQueryParam(CryptoApiService.VS_CURRENCIES, WireMock.equalTo(currency))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200)
						.withBody("{\"" + coinId + "\": {}}")));

		assertThrows(NotFoundException.class, () -> cryptoApiService.getCoinPriceById(coinId, Currency.AUD));
	}

	@Test
	public void that_getCoinPriceById_returns_coin_price_from_api() {
		String coinId = "dogecoin";
		BigDecimal coinPrice = new BigDecimal("0.1654");
		String currency = "aud";
		wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo(CryptoApiService.PRICE_BY_ID))
				.withQueryParam(CryptoApiService.IDS, WireMock.equalTo(coinId))
				.withQueryParam(CryptoApiService.VS_CURRENCIES, WireMock.equalTo(currency))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200)
						.withBody("{\"" + coinId + "\": {\"" + currency + "\": " + coinPrice + "}}")));

		BigDecimal foundCoinPrice = cryptoApiService.getCoinPriceById(coinId, Currency.AUD);

		assertEquals(coinPrice, foundCoinPrice);
	}

	@Test
	public void that_getCoinPriceById_returns_coin_prices_in_aud_when_no_currency_selected() {
		String coinId = "dogecoin";
		BigDecimal coinPrice = new BigDecimal("0.1654");
		String currency = "aud";
		wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo(CryptoApiService.PRICE_BY_ID))
				.withQueryParam(CryptoApiService.IDS, WireMock.equalTo(coinId))
				.withQueryParam(CryptoApiService.VS_CURRENCIES, WireMock.equalTo(currency))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200)
						.withBody("{\"" + coinId + "\": {\"" + currency + "\": " + coinPrice + "}}")));

		BigDecimal foundCoinPrice = cryptoApiService.getCoinPriceById(coinId, null);

		assertEquals(coinPrice, foundCoinPrice);
	}

	@Test
	public void that_getCoinPricesByIds_returns_empty_map_when_api_price_data_is_empty() {
		String coinId1 = "fakecoin";
		String coinId2 = "unrealcoin";
		String currency = "aud";
		wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo(CryptoApiService.PRICE_BY_ID))
				.withQueryParam(CryptoApiService.IDS, WireMock.equalTo(coinId1 + "," + coinId2))
				.withQueryParam(CryptoApiService.VS_CURRENCIES, WireMock.equalTo(currency))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200).withBody("{}")));

		Map<String, BigDecimal> coinPrices = cryptoApiService.getCoinPricesByIds(Arrays.asList(coinId1, coinId2),
				Currency.AUD);

		assertTrue(coinPrices.isEmpty());
	}

	@Test
	public void that_getCoinPricesByIds_returns_coin_prices_map_from_api_price_data() {
		String coinId1 = "bitcoin";
		String coinId2 = "ethereum";
		BigDecimal coinPrice1 = new BigDecimal("53727");
		BigDecimal coinPrice2 = new BigDecimal("3620.82");
		String currency = "aud";
		wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo(CryptoApiService.PRICE_BY_ID))
				.withQueryParam(CryptoApiService.IDS, WireMock.equalTo(coinId1 + "," + coinId2))
				.withQueryParam(CryptoApiService.VS_CURRENCIES, WireMock.equalTo(currency))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200)
						.withBody("{\"" + coinId1 + "\": {\"" + currency + "\": " + coinPrice1 + "}, \"" + coinId2
								+ "\": {\"" + currency + "\": " + coinPrice2 + "}}")));

		Map<String, BigDecimal> coinPrices = cryptoApiService.getCoinPricesByIds(Arrays.asList(coinId1, coinId2),
				Currency.AUD);

		BigDecimal foundCoinPrice1 = coinPrices.get(coinId1);
		BigDecimal foundCoinPrice2 = coinPrices.get(coinId2);
		assertNotNull(foundCoinPrice1);
		assertNotNull(foundCoinPrice2);
		assertEquals(coinPrice1, foundCoinPrice1);
		assertEquals(coinPrice2, foundCoinPrice2);
	}

	@Test
	public void that_getCoinPricesByIds_returns_coin_prices_in_aud_when_no_currency_selected() {
		String coinId1 = "bitcoin";
		String coinId2 = "ethereum";
		BigDecimal coinPrice1 = new BigDecimal("53727");
		BigDecimal coinPrice2 = new BigDecimal("3620.82");
		String currency = "aud";
		wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo(CryptoApiService.PRICE_BY_ID))
				.withQueryParam(CryptoApiService.IDS, WireMock.equalTo(coinId1 + "," + coinId2))
				.withQueryParam(CryptoApiService.VS_CURRENCIES, WireMock.equalTo(currency))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200)
						.withBody("{\"" + coinId1 + "\": {\"" + currency + "\": " + coinPrice1 + "}, \"" + coinId2
								+ "\": {\"" + currency + "\": " + coinPrice2 + "}}")));

		Map<String, BigDecimal> coinPrices = cryptoApiService.getCoinPricesByIds(Arrays.asList(coinId1, coinId2), null);

		BigDecimal foundCoinPrice1 = coinPrices.get(coinId1);
		BigDecimal foundCoinPrice2 = coinPrices.get(coinId2);
		assertNotNull(foundCoinPrice1);
		assertNotNull(foundCoinPrice2);
		assertEquals(coinPrice1, foundCoinPrice1);
		assertEquals(coinPrice2, foundCoinPrice2);
	}

	@Test
	public void that_getCoinPriceHistoryById_throws_webClientRepsponseException_when_webClient_returns_status404() {
		String coinId = "fakecoin";
		Currency currency = Currency.AUD;
		int days = 14;

		wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/coins/" + coinId + "/market_chart"))
				.withQueryParam(CryptoApiService.VS_CURRENCY, WireMock.equalTo(currency.getValue()))
				.withQueryParam(CryptoApiService.DAYS, WireMock.equalTo(Integer.toString(days)))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(404)
						.withBody("{\"message\": \"Not found\"}")));

		assertThrows(WebClientResponseException.class,
				() -> cryptoApiService.getCoinPriceHistoryById(coinId, null, days));
	}

	@Test
	public void that_getPriceHistoryById_returns_empty_price_when_webClient_returns_emptyBody_and_status200() {
		String coinId = "coincoin";
		Currency currency = Currency.AUD;
		int days = 1;

		wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/coins/" + coinId + "/market_chart"))
				.withQueryParam(CryptoApiService.VS_CURRENCY, WireMock.equalTo(currency.getValue()))
				.withQueryParam(CryptoApiService.DAYS, WireMock.equalTo(Integer.toString(days)))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200).withBody("{}")));

		Map<Long, BigDecimal> priceHistory = cryptoApiService.getCoinPriceHistoryById(coinId, currency, days);
		assertEquals(0, priceHistory.size());
	}

	@Test
	public void that_getCoinPriceHistoryById_returns_coinPriceHistory() {
		String coinId = "bitcoin";
		String currency = "aud";
		int days = 7;
		long unixTimeStamp1 = 1646701450165L;
		long unixTimeStamp2 = 1646701798807L;
		long unixTimeStamp3 = 1646702033764L;

		BigDecimal price1 = new BigDecimal("52169.970045315786");
		BigDecimal price2 = new BigDecimal("52081.19115056431");
		BigDecimal price3 = new BigDecimal("52218.462326654786");

		wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/coins/" + coinId + "/market_chart"))
				.withQueryParam(CryptoApiService.VS_CURRENCY, WireMock.equalTo(currency))
				.withQueryParam(CryptoApiService.DAYS, WireMock.equalTo(Integer.toString(days)))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200)
						.withBody("{\"prices\": [[" + unixTimeStamp1 + "," + price1 + "],[" + unixTimeStamp2 + ","
								+ price2 + "],[" + unixTimeStamp3 + "," + price3 + "]]}")));

		Map<Long, BigDecimal> priceHistory = cryptoApiService.getCoinPriceHistoryById(coinId, null, days);
		assertEquals(price1, priceHistory.get(unixTimeStamp1));
		assertEquals(price2, priceHistory.get(unixTimeStamp2));
		assertEquals(price3, priceHistory.get(unixTimeStamp3));
	}

	@Test
	public void that_getCoinPriceHistoryById_returns_coinPriceHistory_and_ignores_null_and_empty_fields() {
		String coinId = "dogecoin";
		String currency = "aud";
		int days = 7;
		long unixTimeStamp1 = 1646701450165L;
		long unixTimeStamp2 = 1646701798807L;
		long unixTimeStamp3 = 1646702033764L;

		BigDecimal price1 = new BigDecimal("0.8624");
		BigDecimal price2 = null;
		BigDecimal price3 = new BigDecimal("0.4623");

		wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/coins/" + coinId + "/market_chart"))
				.withQueryParam(CryptoApiService.VS_CURRENCY, WireMock.equalTo(currency))
				.withQueryParam(CryptoApiService.DAYS, WireMock.equalTo(Integer.toString(days)))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200)
						.withBody("{\"prices\": [[" + unixTimeStamp1 + "," + price1 + "],[],[" + unixTimeStamp2 + ","
								+ price2 + "],[" + unixTimeStamp3 + "," + price3 + "]]}")));

		Map<Long, BigDecimal> priceHistory = cryptoApiService.getCoinPriceHistoryById(coinId, null, days);
		assertEquals(price1, priceHistory.get(unixTimeStamp1));
		assertEquals(price3, priceHistory.get(unixTimeStamp3));
		assertEquals(2, priceHistory.size());
	}

}
