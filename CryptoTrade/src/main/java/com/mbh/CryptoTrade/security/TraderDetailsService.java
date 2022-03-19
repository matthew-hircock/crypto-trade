package com.mbh.CryptoTrade.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mbh.CryptoTrade.dal.TraderRepository;

@Service
public class TraderDetailsService implements UserDetailsService {

	private TraderRepository traderRepo;

	@Autowired
	public TraderDetailsService(TraderRepository traderRepo) {
		super();
		this.traderRepo = traderRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return new TraderDetails(traderRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("username not found")));
	}

}
