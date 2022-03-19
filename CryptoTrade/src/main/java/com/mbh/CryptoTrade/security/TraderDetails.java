package com.mbh.CryptoTrade.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mbh.CryptoTrade.model.Trader;

public class TraderDetails implements UserDetails {

	private Trader trader;

	public TraderDetails(Trader trader) {
		super();
		this.trader = trader;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public String getPassword() {
		return trader.getPassword();
	}

	@Override
	public String getUsername() {
		return trader.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
