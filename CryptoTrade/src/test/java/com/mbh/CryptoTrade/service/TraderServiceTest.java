package com.mbh.CryptoTrade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mbh.CryptoTrade.dal.HoldingRepository;
import com.mbh.CryptoTrade.dal.TraderRepository;
import com.mbh.CryptoTrade.exception.NotFoundException;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Currency;
import com.mbh.CryptoTrade.model.Holding;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.service.TraderService;
import com.mbh.CryptoTrade.view.ICoinDetailsView;
import com.mbh.CryptoTrade.view.IHoldingDetailsView;
import com.mbh.CryptoTrade.view.ITraderDetailsView;

@SpringBootTest
public class TraderServiceTest {

	private static final String TRADER_USERNAME = "matthew.h";

	@Autowired
	private TraderService traderService;

	@MockBean
	private TraderRepository mockTraderRepo;
	@MockBean
	private HoldingRepository mockHoldingRepo;

	@Test
	public void that_getTraderByUsername_throws_when_trader_does_not_exist() {
		assertThrows(NotFoundException.class, () -> traderService.getTraderByUsername(TRADER_USERNAME));
		verify(mockTraderRepo).findByUsername(TRADER_USERNAME);
	}

	@Test
	public void that_getTraderByUsername_returns_traderDetails_when_trader_exists() {
		Trader trader = new Trader(13, TRADER_USERNAME, "password");
		trader.setBalance(BigDecimal.valueOf(234));
		trader.setCurrency(Currency.AUD);
		when(mockTraderRepo.findByUsername(TRADER_USERNAME)).thenReturn(Optional.of(trader));
		ITraderDetailsView traderDetails = traderService.getTraderByUsername(TRADER_USERNAME);
		verify(mockTraderRepo).findByUsername(TRADER_USERNAME);
		assertEquals(trader.getUsername(), traderDetails.getUsername());
		assertEquals(trader.getBalance(), traderDetails.getBalance());
		assertEquals(trader.getCurrency(), traderDetails.getCurrency());
	}

	@Test
	public void that_getHoldingsByTrader_returns_a_list_of_holdingDetails() {
		Trader trader = new Trader(13, TRADER_USERNAME, "password");
		List<Holding> holdings = Arrays.asList(
				new Holding(200, BigDecimal.valueOf(34), new Coin("bitcoin", "btc", "Bitcoin"), trader),
				new Holding(3400, BigDecimal.valueOf(14), new Coin("ethereum", "eth", "Ethereum"), trader));
		when(mockHoldingRepo.findByTraderUsername(TRADER_USERNAME)).thenReturn(holdings);
		List<IHoldingDetailsView> holdingsDetails = traderService.getHoldingsByTrader(TRADER_USERNAME);
		verify(mockHoldingRepo).findByTraderUsername(TRADER_USERNAME);
		assertEquals(holdings.size(), holdingsDetails.size());
		for (int i = 0; i < holdingsDetails.size(); i++) {
			Holding holding = holdings.get(i);
			IHoldingDetailsView holdingDetails = holdingsDetails.get(i);
			assertEquals(holding.getAmount(), holdingDetails.getAmount());
			assertEquals(holding.getAverageUnitPrice(), holdingDetails.getAverageUnitPrice());
			Coin coin = holding.getCoin();
			ICoinDetailsView coinDetails = holdingDetails.getCoin();
			assertEquals(coin.getId(), coinDetails.getId());
			assertEquals(coin.getSymbol(), coinDetails.getSymbol());
			assertEquals(coin.getName(), coinDetails.getName());
		}
	}

}
