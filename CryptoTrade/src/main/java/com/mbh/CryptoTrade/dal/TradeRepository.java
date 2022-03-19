package com.mbh.CryptoTrade.dal;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.Trader;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

	List<Trade> findByCoinAndTrader(Coin coin, Trader trader);

	List<Trade> findByTraderUsername(String traderUsername);
	
	Page<Trade> findByTraderUsername(String traderUsername, Pageable pageable);

	Optional<Trade> findByIdAndTraderUsername(long id, String traderUsername);
	
}
