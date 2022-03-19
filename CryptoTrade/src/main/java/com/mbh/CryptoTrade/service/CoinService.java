package com.mbh.CryptoTrade.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.mbh.CryptoTrade.api.ICryptoApiService;
import com.mbh.CryptoTrade.dal.CoinRepository;
import com.mbh.CryptoTrade.exception.NotFoundException;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Currency;
import com.mbh.CryptoTrade.view.CoinDetailsView;
import com.mbh.CryptoTrade.view.ICoinDetailsView;

@Service
public class CoinService {

	private static Logger logger = LogManager.getLogger(CoinService.class);
	private ICryptoApiService cryptoApiService;
	private CoinRepository coinRepo;

	@Autowired
	public CoinService(ICryptoApiService cryptoApiService, CoinRepository coinRepo) {
		super();
		this.cryptoApiService = cryptoApiService;
		this.coinRepo = coinRepo;
	}

	/**
	 * @param id
	 *            the id of the coin
	 * @param currency
	 *            the currency the coin is being priced in
	 * @return the coin details (id, name, symbol, current price)
	 * @throws NotFoundException
	 *             If the coin can not be found
	 */
	public ICoinDetailsView getCoinById(String id, Currency currency) {
		Coin coin = coinRepo.findById(id).orElseThrow(() -> new NotFoundException("id '" + id + "' not found"));
		BigDecimal currentPrice = cryptoApiService.getCoinPriceById(id, currency);
		return new CoinDetailsView(coin, currentPrice);
	}

	/**
	 * @param pageNumber
	 *            the page number being requested (indices starting from 0)
	 * @param pageSize
	 *            the number of elements on each page
	 * @param currency
	 *            the currency all coins will be priced in
	 * @return a page containing each coins details (id, name, symbol, current
	 *         price)
	 */
	public Page<ICoinDetailsView> getCoinsByPage(int pageNumber, int pageSize, Currency currency) {
		Page<Coin> coins = coinRepo.findAll(PageRequest.of(pageNumber, pageSize, Direction.ASC, "name"));
		List<String> coinIds = coins.stream().map(c -> c.getId()).collect(Collectors.toList());
		logger.debug("Getting coin prices - ids = {}, currency = {}", coinIds, currency);
		Map<String, BigDecimal> coinPrices = cryptoApiService.getCoinPricesByIds(coinIds, currency);
		return coins.map(c -> new CoinDetailsView(c, coinPrices.get(c.getId())));
	}

}
