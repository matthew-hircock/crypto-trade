package com.mbh.CryptoTrade.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mbh.CryptoTrade.service.PriceHistoryService;

@RestController
public class PriceHistoryController {

	private PriceHistoryService priceHistoryService;

	@Autowired
	public PriceHistoryController(PriceHistoryService priceHistoryService) {
		super();
		this.priceHistoryService = priceHistoryService;
	}

	@GetMapping("/coins/{id}/priceHistory")
	@ResponseStatus(HttpStatus.OK)
	public Map<Long, BigDecimal> getCoinPriceHistoryById(@PathVariable String id,
			@RequestParam(defaultValue = "1") int days) {
		return priceHistoryService.getPriceHistoryByCoinId(id, null, days);
	}

}
