package com.mbh.CryptoTrade.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mbh.CryptoTrade.model.Coin;

@Repository
public interface CoinRepository extends JpaRepository<Coin, String> {

	Page<Coin> findAll(Pageable pageable);
	
}
