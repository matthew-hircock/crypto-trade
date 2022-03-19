package com.mbh.CryptoTrade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import com.mbh.CryptoTrade.api.ICryptoApiService;
import com.mbh.CryptoTrade.dal.CoinRepository;
import com.mbh.CryptoTrade.exception.NotFoundException;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Currency;
import com.mbh.CryptoTrade.view.ICoinDetailsView;

@SpringBootTest
public class CoinServiceTest {

	@Autowired
	private CoinService coinService;

	@MockBean
	private ICryptoApiService mockCryptoApiService;
	@MockBean
	private CoinRepository mockCoinRepo;

	@Test
	public void that_getCoinById_throws_notFoundException_when_no_coin_exists() {
		String id = "fakecoin";
		assertThrows(NotFoundException.class, () -> coinService.getCoinById(id, null));
		verify(mockCoinRepo).findById(id);
	}

	@Test
	public void that_getCoinById_returns_coinDetails_with_currentPrice_when_exists_in_repository() {
		Currency currency = Currency.AUD;
		BigDecimal currentPrice = BigDecimal.valueOf(150);
		String id = "bitcoin";
		Coin coin = new Coin();
		when(mockCoinRepo.findById(id)).thenReturn(Optional.of(coin));
		when(mockCryptoApiService.getCoinPriceById(id, currency)).thenReturn(currentPrice);

		ICoinDetailsView coinDetails = coinService.getCoinById(id, currency);

		verify(mockCoinRepo).findById(id);
		assertEquals(coin.getId(), coinDetails.getId());
		assertEquals(coin.getSymbol(), coinDetails.getSymbol());
		assertEquals(coin.getName(), coinDetails.getName());
		assertEquals(currentPrice, coinDetails.getCurrentPrice());
	}

	@ParameterizedTest // pageNumber,pageSize
	@CsvSource({ "7,10", "1,20", "15,100" })
	public void that_getAllCoins_returns_all_coins_from_apiService(int pageNumber, int pageSize) {
		Currency currency = Currency.AUD;
		String coinId1 = "bitcoin";
		String coinId2 = "etherium";
		List<String> coinIds = new ArrayList<>(Arrays.asList(coinId1, coinId2));
		Coin coin1 = new Coin(coinId1, "btc", "Bitcoin");
		Coin coin2 = new Coin(coinId2, "eth", "Etherium");
		List<Coin> coins = new ArrayList<>(Arrays.asList(coin1, coin2));
		Page<Coin> page = new PageImpl<>(coins);
		BigDecimal coinPrice1 = new BigDecimal("105.60");
		BigDecimal coinPrice2 = new BigDecimal("11.60");
		Map<String, BigDecimal> coinPrices = new HashMap<>();
		coinPrices.put(coinId1, coinPrice1);
		coinPrices.put(coinId1, coinPrice2);
		when(mockCoinRepo.findAll(PageRequest.of(pageNumber, pageSize, Direction.ASC, "name"))).thenReturn(page);
		when(mockCryptoApiService.getCoinPricesByIds(coinIds, currency)).thenReturn(coinPrices);

		Page<ICoinDetailsView> foundCoins = coinService.getCoinsByPage(pageNumber, pageSize, currency);

		verify(mockCoinRepo).findAll(PageRequest.of(pageNumber, pageSize, Direction.ASC, "name"));
		verify(mockCryptoApiService).getCoinPricesByIds(coinIds, currency);
		List<ICoinDetailsView> allCoins = foundCoins.toList();
		for (int i = 0; i < allCoins.size(); i++) {
			Coin coin = coins.get(i);
			ICoinDetailsView coinDetails = allCoins.get(i);
			assertEquals(coin.getId(), coinDetails.getId());
			assertEquals(coin.getSymbol(), coinDetails.getSymbol());
			assertEquals(coin.getName(), coinDetails.getName());
			assertEquals(coinPrices.get(coinDetails.getId()), coinDetails.getCurrentPrice());
		}
	}

}
