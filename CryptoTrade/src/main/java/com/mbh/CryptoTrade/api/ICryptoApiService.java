package com.mbh.CryptoTrade.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.mbh.CryptoTrade.model.Currency;

public interface ICryptoApiService {

	/**
	 * @param ids
	 *            a list ids for the coins being priced
	 * @param currency
	 *            the currency to price the coins against
	 * @return a map containing each coin and current price
	 */
	Map<String, BigDecimal> getCoinPricesByIds(List<String> ids, Currency currency);

	/**
	 * @param coinId
	 *            the id of the coin being priced
	 * @param currency
	 *            the currency to price the coin against
	 * @return the current price of the coin, or null if the coin is not found
	 */
	BigDecimal getCoinPriceById(String coinId, Currency currency);

	/**
	 * @param coinId
	 *            the id of the coin whose price history is being fetched
	 * @param currency
	 *            the currency to get the price history in
	 * @param days
	 *            the number of days of data to be fetched
	 * @return the price history for the coin, containing the date time (as a Unix
	 *         timestamp) and price
	 */
	Map<Long, BigDecimal> getCoinPriceHistoryById(String coinId, Currency currency, int days);

}
