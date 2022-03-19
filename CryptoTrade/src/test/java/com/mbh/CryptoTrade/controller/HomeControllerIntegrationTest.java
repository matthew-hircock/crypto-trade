package com.mbh.CryptoTrade.controller;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.mbh.CryptoTrade.dal.CoinRepository;
import com.mbh.CryptoTrade.dal.HoldingRepository;
import com.mbh.CryptoTrade.dal.TraderRepository;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Currency;
import com.mbh.CryptoTrade.model.Holding;
import com.mbh.CryptoTrade.model.Trader;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerIntegrationTest {

	private static final String USERNAME = "matthew.h";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private TraderRepository traderRepo;
	@Autowired
	private CoinRepository coinRepo;
	@Autowired
	private HoldingRepository holdingRepo;

	@Transactional
	@Test
	@WithMockUser(username = USERNAME)
	public void that_getHomePage_adds_traderDetails_and_holdings_to_model_and_returns_homePage_with_status200()
			throws Exception {
		BigDecimal balance = BigDecimal.valueOf(10000);
		Currency currency = Currency.AUD;

		Trader trader = new Trader(0, USERNAME, null);
		trader.setBalance(balance);
		trader.setCurrency(currency);
		trader = traderRepo.save(trader);

		Coin coin1 = coinRepo.save(new Coin("bitcoin", "btc", "Bitcoin"));
		Coin coin2 = coinRepo.save(new Coin("ethereum", "eth", "Ethereum"));

		Holding holding1 = holdingRepo.save(new Holding(100, BigDecimal.valueOf(150), coin1, trader));
		Holding holding2 = holdingRepo.save(new Holding(15, BigDecimal.valueOf(18), coin2, trader));

		mvc.perform(get("/home")).andDo(print()).andExpectAll(status().isOk(), view().name("home"))
				.andExpectAll(model().attribute("trader", hasProperty("username", is(USERNAME))),
						model().attribute("trader", hasProperty("balance", is(balance))),
						model().attribute("trader", hasProperty("currency", is(currency))))
				.andExpectAll(model().attribute("holdings",
						hasItems(hasProperty("amount", is(holding1.getAmount())),
								hasProperty("averageUnitPrice", is(holding1.getAverageUnitPrice())),
								hasProperty("coin", hasProperty("id", is(coin1.getId()))),
								hasProperty("amount", is(holding2.getAmount())),
								hasProperty("averageUnitPrice", is(holding2.getAverageUnitPrice())),
								hasProperty("coin", hasProperty("id", is(coin2.getId()))))));
	}

}
