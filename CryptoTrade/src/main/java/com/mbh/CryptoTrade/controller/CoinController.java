package com.mbh.CryptoTrade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.mbh.CryptoTrade.service.CoinService;

@Controller
public class CoinController {

	static final String COIN_PAGE = "coin";
	static final String COINS_PAGE = "coins";
	// attribute names on the model
	static final String COINS_KEY = "coins";
	static final String COIN_KEY = "coin";
	
	private CoinService coinService;

	@Autowired
	public CoinController(CoinService coinService) {
		super();
		this.coinService = coinService;
	}

	@GetMapping("/coins")
	public String getAllCoinsPage(Model model, @RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize) {
		// fix up page numbers
		model.addAttribute(COINS_KEY, coinService.getCoinsByPage(pageNumber, pageSize, null));
		return COINS_PAGE;
	}

	@GetMapping("/coins/{id}")
	public String getCoinByIdPage(@PathVariable String id, Model model) {
		model.addAttribute(COIN_KEY, coinService.getCoinById(id, null));
		return COIN_PAGE;
	}

}
