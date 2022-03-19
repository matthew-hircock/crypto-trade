package com.mbh.CryptoTrade.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import com.mbh.CryptoTrade.exception.InvalidTradeException;
import com.mbh.CryptoTrade.exception.NotFoundException;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.service.CoinService;
import com.mbh.CryptoTrade.service.TradeService;
import com.mbh.CryptoTrade.validation.TradeFormValidator;
import com.mbh.CryptoTrade.view.CoinDetailsView;
import com.mbh.CryptoTrade.view.ICoinDetailsView;
import com.mbh.CryptoTrade.view.ITradeDetailsView;
import com.mbh.CryptoTrade.view.TradeDetailsView;
import com.mbh.CryptoTrade.view.TradeForm;

@SpringBootTest
public class TradeControllerTest {

	private static final String TRADER_USERNAME = "matthew.h";

	@Autowired
	private TradeController tradeController;

	@MockBean
	private TradeService mockTradeService;
	@MockBean
	private TradeFormValidator mockTradeFormValidator;
	@MockBean
	private CoinService mockCoinService;

	@Mock
	private Authentication mockAuth;
	@Mock
	private Model mockModel;

	@Captor
	private ArgumentCaptor<TradeForm> tradeFormCaptor;

	@Test
	public void that_getTradesPageByTrader_adds_page_of_trades_from_service_and_returns_trades() {
		int pageNumber = 5;
		int pageSize = 10;
		Page<ITradeDetailsView> trades = new PageImpl<>(
				Arrays.asList(new TradeDetailsView(new Trade()), new TradeDetailsView(new Trade())));
		when(mockAuth.getName()).thenReturn(TRADER_USERNAME);
		when(mockTradeService.getTradesForTraderByPage(TRADER_USERNAME, pageNumber, pageSize)).thenReturn(trades);

		String nextPage = tradeController.getTradesPageByTrader(mockAuth, mockModel, pageNumber, pageSize);

		verify(mockTradeService).getTradesForTraderByPage(TRADER_USERNAME, pageNumber, pageSize);
		verify(mockModel).addAttribute(TradeController.TRADES_KEY, trades);
		assertEquals(TradeController.TRADES_PAGE, nextPage);
	}

	@Test
	public void that_getTradeById_adds_trade_to_model_and_returns_tradePage() {
		long tradeId = 45;
		Trade trade = new Trade();
		TradeDetailsView tradeDetails = new TradeDetailsView(trade);
		when(mockAuth.getName()).thenReturn(TRADER_USERNAME);
		when(mockTradeService.getTradeByIdAndTraderUsername(tradeId, TRADER_USERNAME)).thenReturn(tradeDetails);

		String nextPage = tradeController.getTradeById(tradeId, mockAuth, mockModel);

		verify(mockTradeService).getTradeByIdAndTraderUsername(tradeId, TRADER_USERNAME);
		verify(mockModel).addAttribute(TradeController.TRADE_KEY, tradeDetails);
		assertEquals(TradeController.TRADE_PAGE, nextPage);
	}

	@Test
	public void that_getBuyTradeRequestPage_throws_when_coin_does_not_exist() {
		String coinId = "bitcoin";
		when(mockCoinService.getCoinById(coinId, null)).thenThrow(new NotFoundException("Coin not found"));

		assertThrows(NotFoundException.class, () -> tradeController.getBuyTradeRequestPage(coinId, mockModel));

		verify(mockCoinService).getCoinById(coinId, null);
	}

	@Test
	public void that_getBuyTradeRequestPage_adds_buy_trade_to_model_and_returns_createTradePage() {
		String coinId = "bitcoin";
		Coin coin = new Coin(coinId, "BTC", "Bitcoin");
		BigDecimal currentPrice = new BigDecimal("1200.50");
		ICoinDetailsView coinDetails = new CoinDetailsView(coin, currentPrice);
		when(mockCoinService.getCoinById(coinId, null)).thenReturn(coinDetails);

		String nextPage = tradeController.getBuyTradeRequestPage(coinId, mockModel);

		verify(mockCoinService).getCoinById(coinId, null);
		verify(mockModel).addAttribute(eq(TradeController.TRADE_FORM_KEY), tradeFormCaptor.capture());
		TradeForm tradeForm = tradeFormCaptor.getValue();
		assertEquals(TradeType.BUY, tradeForm.getType());
		assertEquals(coinId, tradeForm.getCoinId());
		assertEquals(currentPrice, tradeForm.getCurrentPrice());
		assertEquals(1, tradeForm.getAmount());
		assertEquals(TradeController.CREATE_TRADE_PAGE, nextPage);
	}

	@Test
	public void that_getSellTradeRequestPage_throws_when_coin_does_not_exist() {
		String coinId = "bitcoin";
		when(mockCoinService.getCoinById(coinId, null)).thenThrow(new NotFoundException("Coin not found"));

		assertThrows(NotFoundException.class, () -> tradeController.getBuyTradeRequestPage(coinId, mockModel));

		verify(mockCoinService).getCoinById(coinId, null);
	}

	@Test
	public void that_getSellTradeRequestPage_adds_sell_trade_to_model_and_returns_createTradePage_when_coin_exists() {
		String coinId = "bitcoin";
		Coin coin = new Coin(coinId, "BTC", "Bitcoin");
		BigDecimal currentPrice = new BigDecimal("12.50");
		ICoinDetailsView coinDetails = new CoinDetailsView(coin, currentPrice);
		when(mockCoinService.getCoinById(coinId, null)).thenReturn(coinDetails);

		String nextPage = tradeController.getSellTradeRequestPage(coinId, mockModel);

		verify(mockCoinService).getCoinById(coinId, null);
		verify(mockModel).addAttribute(eq(TradeController.TRADE_FORM_KEY), tradeFormCaptor.capture());
		TradeForm tradeForm = tradeFormCaptor.getValue();
		assertEquals(TradeType.SELL, tradeForm.getType());
		assertEquals(coinId, tradeForm.getCoinId());
		assertEquals(currentPrice, tradeForm.getCurrentPrice());
		assertEquals(1, tradeForm.getAmount());
		assertEquals(TradeController.CREATE_TRADE_PAGE, nextPage);
	}

	@Test
	public void that_processTradeRequest_returns_createTradePage_withErrors_when_tradeForm_is_invalid()
			throws InvalidTradeException {
		TradeForm invalidTradeForm = new TradeForm(null, BigDecimal.valueOf(15000), -10, TradeType.SELL);
		List<String> errors = Arrays.asList("No coin id", "Invalid amount");
		when(mockAuth.getName()).thenReturn(TRADER_USERNAME);
		doThrow(new InvalidTradeException(errors)).when(mockTradeFormValidator).validate(invalidTradeForm);

		String nextPage = tradeController.processTradeRequest(mockAuth, mockModel, invalidTradeForm);

		verify(mockTradeFormValidator).validate(invalidTradeForm);
		verify(mockModel).addAttribute(TradeController.ERRORS_KEY, errors);
		verify(mockModel).addAttribute(TradeController.TRADE_FORM_KEY, invalidTradeForm);
		assertEquals(TradeController.CREATE_TRADE_PAGE, nextPage);
	}

	@Test
	public void that_processTradeRequest_returns_createTradePage_withErrors_when_trade_is_invalid()
			throws InvalidTradeException {
		TradeForm tradeForm = new TradeForm("bitcoin", BigDecimal.valueOf(5125), 10L, TradeType.SELL);
		List<String> errors = Arrays.asList("Invalid trade request");
		when(mockAuth.getName()).thenReturn(TRADER_USERNAME);
		doThrow(new InvalidTradeException(errors)).when(mockTradeService).processTrade(TRADER_USERNAME, tradeForm);

		String nextPage = tradeController.processTradeRequest(mockAuth, mockModel, tradeForm);

		verify(mockTradeFormValidator).validate(tradeForm);
		verify(mockTradeService).processTrade(TRADER_USERNAME, tradeForm);
		verify(mockModel).addAttribute(TradeController.ERRORS_KEY, errors);
		verify(mockModel).addAttribute(TradeController.TRADE_FORM_KEY, tradeForm);
		assertEquals(TradeController.CREATE_TRADE_PAGE, nextPage);
	}

	@Test
	public void that_processTradeRequest_returns_redirectToCoinPage_when_trade_is_valid() throws InvalidTradeException {
		long tradeId = 73;
		String expectedNextPage = "redirect:/trades/" + tradeId;
		TradeForm tradeForm = new TradeForm("dogecoin", BigDecimal.valueOf(15), 10L, TradeType.BUY);
		when(mockAuth.getName()).thenReturn(TRADER_USERNAME);
		when(mockTradeService.processTrade(TRADER_USERNAME, tradeForm)).thenReturn(tradeId);

		String nextPage = tradeController.processTradeRequest(mockAuth, mockModel, tradeForm);

		verify(mockTradeFormValidator).validate(tradeForm);
		verify(mockTradeService).processTrade(TRADER_USERNAME, tradeForm);
		assertEquals(expectedNextPage, nextPage);
	}

}
