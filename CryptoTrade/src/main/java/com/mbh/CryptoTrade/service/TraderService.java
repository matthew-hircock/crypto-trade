package com.mbh.CryptoTrade.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbh.CryptoTrade.dal.HoldingRepository;
import com.mbh.CryptoTrade.dal.TraderRepository;
import com.mbh.CryptoTrade.exception.NotFoundException;
import com.mbh.CryptoTrade.view.HoldingDetailsView;
import com.mbh.CryptoTrade.view.IHoldingDetailsView;
import com.mbh.CryptoTrade.view.ITraderDetailsView;
import com.mbh.CryptoTrade.view.TraderDetailsView;

@Service
public class TraderService {

	private TraderRepository traderRepo;
	private HoldingRepository holdingRepo;

	@Autowired
	public TraderService(TraderRepository traderRepo, HoldingRepository holdingRepo) {
		super();
		this.traderRepo = traderRepo;
		this.holdingRepo = holdingRepo;
	}

	/**
	 * @param traderUsername
	 *            the trader username
	 * @return all holdings of the trader
	 */
	public List<IHoldingDetailsView> getHoldingsByTrader(String traderUsername) {
		// TODO: fetch current price and add to coin in holding or wrap in a portfolio
		return holdingRepo.findByTraderUsername(traderUsername).stream().map(h -> new HoldingDetailsView(h))
				.collect(Collectors.toList());
	}

	/**
	 * @param traderUsername
	 *            the trader username
	 * @return the details of the trader
	 */
	public ITraderDetailsView getTraderByUsername(String traderUsername) {
		return new TraderDetailsView(
				traderRepo.findByUsername(traderUsername).orElseThrow(() -> new NotFoundException("trader not found")));
	}

}
