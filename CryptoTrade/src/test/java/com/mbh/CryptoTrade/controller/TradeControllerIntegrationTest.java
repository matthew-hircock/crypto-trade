package com.mbh.CryptoTrade.controller;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.mbh.CryptoTrade.api.CryptoApiService;
import com.mbh.CryptoTrade.controller.TradeController;
import com.mbh.CryptoTrade.dal.CoinRepository;
import com.mbh.CryptoTrade.dal.HoldingRepository;
import com.mbh.CryptoTrade.dal.TradeRepository;
import com.mbh.CryptoTrade.dal.TraderRepository;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Currency;
import com.mbh.CryptoTrade.model.Holding;
import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.model.Trader;

@SpringBootTest
@AutoConfigureMockMvc
public class TradeControllerIntegrationTest {

	private static final String USERNAME = "matthew.h";

	@Autowired
	private TraderRepository traderRepo;
	@Autowired
	private CoinRepository coinRepo;
	@Autowired
	private TradeRepository tradeRepo;
	@Autowired
	private HoldingRepository holdingRepo;

	@MockBean
	private CryptoApiService mockCryptoApiService;

	@Autowired
	private MockMvc mvc;

	@Transactional
	@Test
	@WithMockUser(username = USERNAME)
	public void that_getTradesPageByTrader_gets_tradeDetails_and_returns_tradesPage_with_status200() throws Exception {
		Trader trader = traderRepo.save(new Trader(0, USERNAME, null));
		Coin coin1 = coinRepo.save(new Coin("bitcoin", "btc", "Bitcoin"));
		Coin coin2 = coinRepo.save(new Coin("ethereum", "eth", "Ethereum"));
		Trade trade1 = tradeRepo.save(new Trade(0, 100, BigDecimal.valueOf(234), TradeType.BUY, coin1, trader));
		Trade trade2 = tradeRepo.save(new Trade(0, 236, BigDecimal.valueOf(34), TradeType.BUY, coin2, trader));

		mvc.perform(get("/trades")).andDo(print()).andExpectAll(status().isOk(),
				view().name(TradeController.TRADES_PAGE), model().attribute("trades", hasProperty("number", is(0))),
				model().attribute("trades", hasProperty("size", is(10))),
				model().attribute("trades",
						hasProperty("content",
								hasItems(hasProperty("id", is(trade1.getId())),
										hasProperty("transactionDateTime", is(trade1.getTransactionDateTime())),
										hasProperty("amount", is(trade1.getAmount())),
										hasProperty("unitPrice", is(trade1.getUnitPrice())),
										hasProperty("coin", hasProperty("id", is(coin1.getId()))),
										hasProperty("id", is(trade2.getId())),
										hasProperty("transactionDateTime", is(trade2.getTransactionDateTime())),
										hasProperty("amount", is(trade2.getAmount())),
										hasProperty("unitPrice", is(trade2.getUnitPrice())),
										hasProperty("coin", hasProperty("id", is(coin2.getId())))))));
	}

	@Transactional
	@Test
	@WithMockUser(username = USERNAME)
	public void that_getTradesPageByTrader_gets_tradeDetails_and_returns_empty_tradesPage_with_status200()
			throws Exception {
		int pageNumber = 15;
		int pageSize = 50;

		mvc.perform(get("/trades").queryParam("pageNumber", Integer.toString(pageNumber)).queryParam("pageSize",
				Integer.toString(pageSize))).andDo(print()).andExpectAll(status().isOk(),
						view().name(TradeController.TRADES_PAGE),
						model().attribute("trades", hasProperty("number", is(pageNumber))),
						model().attribute("trades", hasProperty("size", is(pageSize))));
	}

	@Test
	@WithMockUser
	public void that_getTradeById_throws_returns_notFoundPage_with_status404_when_trade_does_not_exist()
			throws Exception {
		mvc.perform(get("/trades/70")).andDo(print()).andExpectAll(status().isNotFound(), view().name("notFound"),
				model().attributeExists("error"));
	}

	@Transactional
	@Test
	@WithMockUser(username = USERNAME)
	public void that_getTradeById_gets_tradeDetails_and_returns_tradePage_with_status200_when_trade_exists()
			throws Exception {
		BigDecimal unitPrice = BigDecimal.valueOf(234);
		TradeType type = TradeType.BUY;
		Trader trader = traderRepo.save(new Trader(0L, USERNAME, null));
		Coin coin = coinRepo.save(new Coin("bitcoin", "btc", "Bitcoin"));
		Trade trade = tradeRepo.save(new Trade(0, 100, unitPrice, type, coin, trader));

		mvc.perform(get("/trades/" + trade.getId())).andDo(print()).andExpectAll(status().isOk(), view().name("trade"),
				model().attribute("trade", hasProperty("id", is(trade.getId()))),
				model().attribute("trade", hasProperty("transactionDateTime", is(trade.getTransactionDateTime()))),
				model().attribute("trade", hasProperty("amount", is(trade.getAmount()))),
				model().attribute("trade", hasProperty("unitPrice", is(trade.getUnitPrice()))),
				model().attribute("trade", hasProperty("coin", hasProperty("id", is(coin.getId())))));
	}

	@Test
	@WithMockUser
	public void that_getBuyTradeRequestPage_returns_notFoundPage_with_status404_when_coin_not_found() throws Exception {
		String coinId = "fakecoin";
		mvc.perform(get("/trade/" + coinId + "/buy")).andDo(print()).andExpectAll(status().isNotFound(),
				view().name("notFound"), model().attributeExists("error"));
	}

	@Transactional
	@Test
	@WithMockUser
	public void that_getBuyTradeRequestPage_returns_createTradePage_with_status200() throws Exception {
		Coin coin = coinRepo.save(new Coin("ethereum", "eth", "Ethereum"));
		BigDecimal currentPrice = new BigDecimal("123.45");
		when(mockCryptoApiService.getCoinPriceById(coin.getId(), null)).thenReturn(currentPrice);

		mvc.perform(get("/trade/" + coin.getId() + "/buy")).andDo(print()).andExpectAll(status().isOk(),
				view().name("createTrade"), model().attribute("trade", hasProperty("coinId", is(coin.getId()))),
				model().attribute("trade", hasProperty("currentPrice", is(currentPrice))),
				model().attribute("trade", hasProperty("amount", is(1L))),
				model().attribute("trade", hasProperty("type", is(TradeType.BUY))));
	}

	@Test
	@WithMockUser
	public void that_getSellTradeRequestPage_returns_notFoundPage_with_status404_when_coin_not_found() throws Exception {
		String coinId = "fakecoin";
		mvc.perform(get("/trade/" + coinId + "/sell")).andDo(print()).andExpectAll(status().isNotFound(),
				view().name("notFound"), model().attributeExists("error"));
	}
	
	@Transactional
	@Test
	@WithMockUser
	public void that_getSellTradeRequestPage_returns_createTradePage_with_status200() throws Exception {
		Coin coin = coinRepo.save(new Coin("bitcoin", "btc", "Bitcoin"));
		BigDecimal currentPrice = new BigDecimal("15.50");
		when(mockCryptoApiService.getCoinPriceById(coin.getId(), null)).thenReturn(currentPrice);

		mvc.perform(get("/trade/" + coin.getId() + "/sell")).andDo(print()).andExpectAll(status().isOk(),
				view().name("createTrade"), model().attribute("trade", hasProperty("coinId", is(coin.getId()))),
				model().attribute("trade", hasProperty("currentPrice", is(currentPrice))),
				model().attribute("trade", hasProperty("amount", is(1L))),
				model().attribute("trade", hasProperty("type", is(TradeType.SELL))));
	}

	@Test
	@WithMockUser
	public void that_postProcessTradeRequest_returns_createTradePage_when_tradeForm_is_invalid() throws Exception {
		String coinId = "ethereum";
		TradeType type = TradeType.BUY;
		mvc.perform(post("/trade").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("coinId", coinId)
				.param("amount", "-100").param("type", type.toString())).andDo(print())
				.andExpectAll(view().name("createTrade"), model().attributeExists("errors"),
						model().attribute("trade", hasProperty("coinId", is(coinId))),
						model().attribute("trade", hasProperty("type", is(type))));
	}

	@Test
	@WithMockUser
	public void that_postProcessTradeRequest_returns_notFoundPage_with_status404_when_coin_not_found()
			throws Exception {
		String coinId = "fakecoin";
		TradeType type = TradeType.BUY;
		mvc.perform(post("/trade").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("coinId", coinId)
				.param("amount", "100").param("type", type.toString())).andDo(print())
				.andExpectAll(status().isNotFound(), view().name("notFound"), model().attributeExists("error"));
	}

	@Transactional
	@Test
	@WithMockUser(username = USERNAME) // buying more than can be afforded
	public void that_postProcessTradeRequest_returns_createTradePage_when_tradeRequest_is_invalid_as_buying_more_than_can_afford()
			throws Exception {
		String coinId = "ethereum";
		Currency currency = Currency.AUD;
		BigDecimal coinPrice = BigDecimal.valueOf(10000);
		TradeType type = TradeType.BUY;

		coinRepo.save(new Coin(coinId, "eth", "Ethereum"));

		Trader trader = new Trader(0L, USERNAME, null);
		trader.setBalance(BigDecimal.valueOf(5000));
		trader.setCurrency(currency);
		traderRepo.save(trader);

		when(mockCryptoApiService.getCoinPriceById(coinId, currency)).thenReturn(coinPrice);

		mvc.perform(post("/trade").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("coinId", coinId)
				.param("amount", "1").param("type", type.toString())).andDo(print())
				.andExpectAll(view().name("createTrade"), model().attributeExists("errors"),
						model().attribute("trade", hasProperty("coinId", is(coinId))),
						model().attribute("trade", hasProperty("type", is(type))));
	}

	@Transactional
	@Test
	@WithMockUser(username = USERNAME) // selling a coin that is not held
	public void that_postProcessTradeRequest_returns_createTradePage_when_tradeRequest_is_invalid_as_not_holding_coin()
			throws Exception {
		String coinId = "ethereum";
		Currency currency = Currency.AUD;
		BigDecimal coinPrice = BigDecimal.valueOf(10);
		TradeType type = TradeType.SELL;

		coinRepo.save(new Coin(coinId, "eth", "Ethereum"));

		Trader trader = new Trader(0L, USERNAME, null);
		trader.setBalance(BigDecimal.valueOf(5000));
		trader.setCurrency(currency);
		traderRepo.save(trader);

		when(mockCryptoApiService.getCoinPriceById(coinId, currency)).thenReturn(coinPrice);

		mvc.perform(post("/trade").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("coinId", coinId)
				.param("amount", "1").param("type", type.toString())).andDo(print())
				.andExpectAll(view().name("createTrade"), model().attributeExists("errors"),
						model().attribute("trade", hasProperty("coinId", is(coinId))),
						model().attribute("trade", hasProperty("type", is(type))));
	}

	@Transactional
	@Test
	@WithMockUser(username = USERNAME) // selling more than held
	public void that_postProcessTradeRequest_returns_createTradePage_when_tradeRequest_is_invalid_as_not_holding_enough_to_sell()
			throws Exception {
		String coinId = "ethereum";
		Currency currency = Currency.AUD;
		BigDecimal coinPrice = BigDecimal.valueOf(10);
		TradeType type = TradeType.SELL;

		Coin coin = coinRepo.save(new Coin(coinId, "eth", "Ethereum"));

		Trader trader = new Trader(0L, USERNAME, null);
		trader.setBalance(BigDecimal.valueOf(5000));
		trader.setCurrency(currency);
		trader = traderRepo.save(trader);

		holdingRepo.save(new Holding(5, BigDecimal.valueOf(125), coin, trader));

		when(mockCryptoApiService.getCoinPriceById(coinId, currency)).thenReturn(coinPrice);

		mvc.perform(post("/trade").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("coinId", coinId)
				.param("amount", "10").param("type", type.toString())).andDo(print())
				.andExpectAll(view().name("createTrade"), model().attributeExists("errors"),
						model().attribute("trade", hasProperty("coinId", is(coinId))),
						model().attribute("trade", hasProperty("type", is(type))));
	}

	@Transactional
	@Test
	@WithMockUser(username = USERNAME)
	public void that_postProcessTradeRequest_returns_returns_tradePage_and_creates_new_holding_when_tradeRequest_is_valid()
			throws Exception {
		String coinId = "ethereum";
		Currency currency = Currency.AUD;
		BigDecimal coinPrice = BigDecimal.valueOf(10);
		long tradeAmount = 15;
		TradeType type = TradeType.BUY;
		Coin coin = coinRepo.save(new Coin(coinId, "eth", "Ethereum"));

		Trader trader = new Trader(0L, USERNAME, null);
		trader.setBalance(BigDecimal.valueOf(5000));
		trader.setCurrency(currency);
		trader = traderRepo.save(trader);

		when(mockCryptoApiService.getCoinPriceById(coinId, currency)).thenReturn(coinPrice);

		MvcResult mvcResult = mvc
				.perform(post("/trade").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("coinId", coinId)
						.param("amount", Long.toString(tradeAmount)).param("type", type.toString()))
				.andDo(print()).andExpect(status().is3xxRedirection()).andReturn();

		List<Trade> trades = tradeRepo.findByCoinAndTrader(coin, trader);
		assertEquals(1, trades.size());
		Trade trade = trades.get(0);
		assertEquals("redirect:/trades/" + trade.getId(), mvcResult.getModelAndView().getViewName());
		assertEquals(tradeAmount, trade.getAmount());
		assertEquals(0, coinPrice.compareTo(trade.getUnitPrice()));
		assertEquals(type, trade.getType());

		Optional<Holding> holding = holdingRepo.findByCoinAndTrader(coin, trader);
		assertTrue(holding.isPresent());
		holding.ifPresent(h -> {
			assertEquals(tradeAmount, h.getAmount());
			assertEquals(0, coinPrice.compareTo(h.getAverageUnitPrice())); // avoid issues with equals() and scaling
		});
	}

	@Transactional
	@Test
	@WithMockUser(username = USERNAME)
	public void that_postProcessTradeRequest_returns_returns_tradePage_and_updates_existing_holding_when_tradeRequest_is_valid()
			throws Exception {
		String coinId = "ethereum";
		Currency currency = Currency.AUD;
		BigDecimal coinPrice = BigDecimal.valueOf(10);
		long tradeAmount = 15;
		long holdingAmount = 100;
		TradeType type = TradeType.BUY;
		Coin coin = coinRepo.save(new Coin(coinId, "eth", "Ethereum"));

		Trader trader = new Trader(0L, USERNAME, null);
		trader.setBalance(BigDecimal.valueOf(5000));
		trader.setCurrency(currency);
		trader = traderRepo.save(trader);

		Holding holding = holdingRepo.save(new Holding(holdingAmount, BigDecimal.valueOf(123), coin, trader));

		when(mockCryptoApiService.getCoinPriceById(coinId, currency)).thenReturn(coinPrice);

		MvcResult mvcResult = mvc
				.perform(post("/trade").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("coinId", coinId)
						.param("amount", Long.toString(tradeAmount)).param("type", type.toString()))
				.andDo(print()).andExpect(status().is3xxRedirection()).andReturn();

		List<Trade> trades = tradeRepo.findByCoinAndTrader(coin, trader);
		assertEquals(1, trades.size());
		Trade trade = trades.get(0);
		assertEquals("redirect:/trades/" + trade.getId(), mvcResult.getModelAndView().getViewName());
		assertEquals(tradeAmount, trade.getAmount());
		assertEquals(0, coinPrice.compareTo(trade.getUnitPrice()));
		assertEquals(type, trade.getType());

		Optional<Holding> optionalHolding = holdingRepo.findById(holding.getId());
		assertTrue(optionalHolding.isPresent());
		optionalHolding.ifPresent(h -> {
			assertEquals(holdingAmount + tradeAmount, h.getAmount());
		});
	}

	@Transactional
	@Test
	@WithMockUser(username = USERNAME)
	public void that_postProcessTradeRequest_returns_returns_tradePage_and_deletes_holding_when_selling_entire_amount_and_tradeRequest_is_valid()
			throws Exception {
		String coinId = "ethereum";
		Currency currency = Currency.AUD;
		BigDecimal coinPrice = BigDecimal.valueOf(10);
		long tradeAmount = 15;
		TradeType type = TradeType.SELL;
		Coin coin = coinRepo.save(new Coin(coinId, "eth", "Ethereum"));

		Trader trader = new Trader(0L, USERNAME, null);
		trader.setBalance(BigDecimal.valueOf(5000));
		trader.setCurrency(currency);
		trader = traderRepo.save(trader);

		Holding holding = holdingRepo.save(new Holding(tradeAmount, BigDecimal.valueOf(123), coin, trader));

		when(mockCryptoApiService.getCoinPriceById(coinId, currency)).thenReturn(coinPrice);

		MvcResult mvcResult = mvc
				.perform(post("/trade").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("coinId", coinId)
						.param("amount", Long.toString(tradeAmount)).param("type", type.toString()))
				.andDo(print()).andExpect(status().is3xxRedirection()).andReturn();

		List<Trade> trades = tradeRepo.findByCoinAndTrader(coin, trader);
		assertEquals(1, trades.size());
		Trade trade = trades.get(0);
		assertEquals("redirect:/trades/" + trade.getId(), mvcResult.getModelAndView().getViewName());
		assertEquals(tradeAmount, trade.getAmount());
		assertEquals(0, coinPrice.compareTo(trade.getUnitPrice()));
		assertEquals(type, trade.getType());

		Optional<Holding> optionalHolding = holdingRepo.findById(holding.getId());
		assertFalse(optionalHolding.isPresent()); // should be removed as everything is sold
	}

}
