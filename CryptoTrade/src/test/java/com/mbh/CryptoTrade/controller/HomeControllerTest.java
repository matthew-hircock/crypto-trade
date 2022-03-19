package com.mbh.CryptoTrade.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import com.mbh.CryptoTrade.controller.HomeController;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Holding;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.service.TraderService;
import com.mbh.CryptoTrade.view.HoldingDetailsView;
import com.mbh.CryptoTrade.view.IHoldingDetailsView;
import com.mbh.CryptoTrade.view.ITraderDetailsView;
import com.mbh.CryptoTrade.view.TraderDetailsView;

@SpringBootTest
public class HomeControllerTest {

	private static final String TRADER_USERNAME = "matthew.h";

	@Autowired
	private HomeController homeController;

	@MockBean
	private TraderService mockTraderService;

	@Mock
	private Authentication mockAuth;
	@Mock
	private Model mockModel;

	@Test
	public void that_getHomePage_adds_trader_and_holdings_and_returns_homePage() {
		Trader trader = new Trader(14, TRADER_USERNAME, "password");
		ITraderDetailsView traderDetails = new TraderDetailsView(trader);
		Holding holding1 = new Holding(100, BigDecimal.valueOf(150), new Coin("bitcoin", "btc", "Bitcoin"), trader);
		Holding holding2 = new Holding(100, BigDecimal.valueOf(150), new Coin("ethereum", "eth", "Ethereum"), trader);
		List<IHoldingDetailsView> holdings = Arrays.asList(new HoldingDetailsView(holding1),
				new HoldingDetailsView(holding2));
		when(mockAuth.getName()).thenReturn(TRADER_USERNAME);
		when(mockTraderService.getTraderByUsername(TRADER_USERNAME)).thenReturn(traderDetails);
		when(mockTraderService.getHoldingsByTrader(TRADER_USERNAME)).thenReturn(holdings);

		String nextPage = homeController.getHomePage(mockAuth, mockModel);

		verify(mockTraderService).getTraderByUsername(TRADER_USERNAME);
		verify(mockTraderService).getHoldingsByTrader(TRADER_USERNAME);
		verify(mockModel).addAttribute(HomeController.TRADER_KEY, traderDetails);
		verify(mockModel).addAttribute(HomeController.HOLDINGS_KEY, holdings);
		assertEquals(HomeController.HOME_PAGE, nextPage);
	}

}
