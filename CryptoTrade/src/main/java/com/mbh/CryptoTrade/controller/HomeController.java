package com.mbh.CryptoTrade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mbh.CryptoTrade.service.TradeService;
import com.mbh.CryptoTrade.service.TraderService;

@Controller
public class HomeController {

	static final String HOME_PAGE = "home";
	// attribute names on the model
	static final String TRADER_KEY = "trader";
	static final String HOLDINGS_KEY = "holdings";
	private TraderService traderService;

	@Autowired
	public HomeController(TraderService traderService, TradeService tradeService) {
		super();
		this.traderService = traderService;
	}

	@GetMapping("/home")
	public String getHomePage(Authentication auth, Model model) {
		String traderUsername = auth.getName();
		model.addAttribute("trader", traderService.getTraderByUsername(traderUsername));
		model.addAttribute("holdings", traderService.getHoldingsByTrader(traderUsername));
		return HOME_PAGE;
	}


}
