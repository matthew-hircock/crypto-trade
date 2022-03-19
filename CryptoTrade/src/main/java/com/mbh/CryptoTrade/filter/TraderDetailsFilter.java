package com.mbh.CryptoTrade.filter;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mbh.CryptoTrade.exception.NotFoundException;
import com.mbh.CryptoTrade.service.TraderService;

@Component
public class TraderDetailsFilter extends OncePerRequestFilter {

	private TraderService traderService;

	@Autowired
	public TraderDetailsFilter(TraderService traderService) {
		super();
		this.traderService = traderService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Principal userPrincipal = request.getUserPrincipal();
		if (userPrincipal != null) {
			try {
				// ensure the trader's balance is available on every request
				request.setAttribute("trader", traderService.getTraderByUsername(userPrincipal.getName()));
			} catch (NotFoundException e) {

			}
		}
		filterChain.doFilter(request, response);
	}

}
