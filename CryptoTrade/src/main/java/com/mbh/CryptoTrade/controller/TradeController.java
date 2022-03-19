package com.mbh.CryptoTrade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mbh.CryptoTrade.exception.InvalidTradeException;
import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.service.CoinService;
import com.mbh.CryptoTrade.service.TradeService;
import com.mbh.CryptoTrade.validation.TradeFormValidator;
import com.mbh.CryptoTrade.view.ICoinDetailsView;
import com.mbh.CryptoTrade.view.TradeForm;

@Controller
public class TradeController {

	static final String TRADE_PAGE = "trade";
	static final String TRADES_PAGE = "trades";
	static final String CREATE_TRADE_PAGE = "createTrade";
	// attribute names on the model
	static final String TRADE_KEY = "trade";
	static final String TRADES_KEY = "trades";
	static final String TRADE_FORM_KEY = "trade";
	static final String ERRORS_KEY = "errors";

	private TradeService tradeService;
	private CoinService coinService;
	private TradeFormValidator tradeFormValidator;

	@Autowired
	public TradeController(TradeService tradeService, CoinService coinService, TradeFormValidator tradeFormValidator) {
		super();
		this.tradeService = tradeService;
		this.coinService = coinService;
		this.tradeFormValidator = tradeFormValidator;
	}

	@GetMapping("/trades")
	public String getTradesPageByTrader(Authentication auth, Model model,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
		model.addAttribute(TRADES_KEY, tradeService.getTradesForTraderByPage(auth.getName(), pageNumber, pageSize));
		return TRADES_PAGE;
	}

	@GetMapping("/trades/{id}")
	public String getTradeById(@PathVariable long id, Authentication auth, Model model) {
		model.addAttribute(TRADE_KEY, tradeService.getTradeByIdAndTraderUsername(id, auth.getName()));
		return TRADE_PAGE;
	}

	@GetMapping("/trade/{id}/buy")
	public String getBuyTradeRequestPage(@PathVariable String id, Model model) {
		ICoinDetailsView coinDetails = coinService.getCoinById(id, null);
		model.addAttribute(TRADE_FORM_KEY, new TradeForm(id, coinDetails.getCurrentPrice(), 1, TradeType.BUY));
		return CREATE_TRADE_PAGE;
	}

	@GetMapping("/trade/{id}/sell")
	public String getSellTradeRequestPage(@PathVariable String id, Model model) {
		ICoinDetailsView coinDetails = coinService.getCoinById(id, null);
		model.addAttribute(TRADE_FORM_KEY, new TradeForm(id, coinDetails.getCurrentPrice(), 1, TradeType.SELL));
		return CREATE_TRADE_PAGE;
	}

	@PostMapping("/trade")
	public String processTradeRequest(Authentication auth, Model model, @ModelAttribute TradeForm tradeForm) {
		String nextPage = CREATE_TRADE_PAGE;
		try {
			tradeFormValidator.validate(tradeForm);
			long tradeId = tradeService.processTrade(auth.getName(), tradeForm); // handle market vs limit order
			nextPage = "redirect:/trades/" + tradeId;
		} catch (InvalidTradeException e) {
			model.addAttribute(ERRORS_KEY, e.getErrors());
			model.addAttribute(TRADE_FORM_KEY, tradeForm);
		}
		return nextPage;
	}

}
