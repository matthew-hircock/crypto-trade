package com.mbh.CryptoTrade.dal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mbh.CryptoTrade.model.Trader;

@Repository
public interface TraderRepository extends JpaRepository<Trader, Long> {

	Optional<Trader> findByUsername(String username);
	
}
