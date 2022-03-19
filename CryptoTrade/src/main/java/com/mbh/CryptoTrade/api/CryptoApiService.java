package com.mbh.CryptoTrade.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.mbh.CryptoTrade.exception.NotFoundException;
import com.mbh.CryptoTrade.model.Currency;

@Service
public class CryptoApiService implements ICryptoApiService {

	static final String PRICE_BY_ID = "/simple/price";
	static final String PRICE_HISTORY_BY_ID = "/coins/{id}/market_chart";
	static final String IDS = "ids";
	static final String VS_CURRENCIES = "vs_currencies";
	static final String VS_CURRENCY = "vs_currency";
	static final String DAYS = "days";
	private WebClient webClient;

	@Autowired
	public CryptoApiService(WebClient webClient) {
		super();
		this.webClient = webClient;
	}

	@Override
	public BigDecimal getCoinPriceById(String coinId, Currency currency) {
		String priceCurrency = (currency != null) ? currency.getValue() : Currency.AUD.getValue();
		JsonNode node = webClient.get()
				.uri(uriBuilder -> uriBuilder.path(PRICE_BY_ID).queryParam(IDS, coinId)
						.queryParam(VS_CURRENCIES, priceCurrency).build())
				.retrieve().bodyToMono(JsonNode.class).block();
		BigDecimal coinPrice = extractCoinPrice(coinId, priceCurrency, node);
		if (coinPrice == null) {
			throw new NotFoundException("unable to find price for coin with id = " + coinId);
		}
		return coinPrice;
	}

	private BigDecimal extractCoinPrice(String id, String currency, JsonNode node) {
		BigDecimal currentPrice = null;
		if (node != null) {
			JsonNode coinIdNode = node.get(id);
			JsonNode price = null;
			if (coinIdNode != null && (price = coinIdNode.get(currency)) != null) {
				currentPrice = price.decimalValue();
			}
		}
		return currentPrice;
	}

	@Override
	public Map<String, BigDecimal> getCoinPricesByIds(List<String> ids, Currency currency) {
		String priceCurrency = (currency != null) ? currency.getValue() : Currency.AUD.getValue();
		StringBuffer coinIds = new StringBuffer();
		ids.forEach(id -> coinIds.append(id + ","));
		if (coinIds.lastIndexOf(",") != -1) {
			coinIds.deleteCharAt(coinIds.lastIndexOf(",")); // remove extra trailing comma
		}
		JsonNode node = webClient.get()
				.uri(uriBuilder -> uriBuilder.path(PRICE_BY_ID).queryParam(IDS, coinIds)
						.queryParam(VS_CURRENCIES, priceCurrency).build())
				.retrieve().bodyToMono(JsonNode.class).block();
		return extractCoinPrices(ids, priceCurrency, node);
	}

	private Map<String, BigDecimal> extractCoinPrices(List<String> ids, String currency, JsonNode node) {
		Map<String, BigDecimal> coinPrices = new HashMap<>();
		for (String id : ids) {
			if (node != null && node.has(id)) {
				coinPrices.put(id, extractCoinPrice(id, currency, node));
			}
		}
		return coinPrices;
	}

	@Override
	public Map<Long, BigDecimal> getCoinPriceHistoryById(String coinId, Currency currency, int days) {
		String priceCurrency = (currency != null) ? currency.getValue() : Currency.AUD.getValue();
		// ensure map is sorted in ascending order by key (time)
		Map<Long, BigDecimal> priceHistory = new TreeMap<>((k1, k2) -> k1.compareTo(k2));
		JsonNode node = webClient.get()
				.uri(uriBuilder -> uriBuilder.path(PRICE_HISTORY_BY_ID).queryParam(VS_CURRENCY, priceCurrency)
						.queryParam(DAYS, days).build(coinId))
				.retrieve().onStatus(HttpStatus::is4xxClientError, ClientResponse::createException)
				.bodyToMono(JsonNode.class).block();
		if (node != null && node.has("prices")) {
			for (JsonNode n : node.get("prices")) {
				JsonNode timeNode = n.get(0);
				JsonNode priceNode = n.get(1);
				if ((timeNode != null && timeNode.isLong()) && (priceNode != null && !priceNode.isNull())) {
					priceHistory.put(timeNode.asLong(), new BigDecimal(priceNode.asText()));
				}
			}
		}
		return priceHistory;
	}

}
