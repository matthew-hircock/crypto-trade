package com.mbh.CryptoTrade.service;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbh.CryptoTrade.api.ICryptoApiService;
import com.mbh.CryptoTrade.model.Currency;

@Service
public class PriceHistoryService {

	private ICryptoApiService cryptoApiService;

	@Autowired
	public PriceHistoryService(ICryptoApiService cryptoApiService) {
		super();
		this.cryptoApiService = cryptoApiService;
	}

	/**
	 * @param coinId
	 *            the id of the coin being fetched
	 * @param currency
	 *            the currency the price history will be fetched in
	 * @param days
	 *            the number of days of data being fetched
	 * @return the price history for the coin, containing the date time (as a Unix
	 *         timestamp) and price
	 */
	public Map<Long, BigDecimal> getPriceHistoryByCoinId(String coinId, Currency currency, int days) {
		return cryptoApiService.getCoinPriceHistoryById(coinId, currency, days);
	}

}
