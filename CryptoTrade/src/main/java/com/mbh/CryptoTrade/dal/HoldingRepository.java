package com.mbh.CryptoTrade.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Holding;
import com.mbh.CryptoTrade.model.Trader;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

	Optional<Holding> findByCoinAndTrader(Coin coin, Trader trader);
	
	List<Holding> findByTraderUsername(String username);
	
}
