package com.mbh.CryptoTrade.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mbh.CryptoTrade.exception.NotFoundException;
import com.mbh.CryptoTrade.filter.TraderDetailsFilter;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.service.TraderService;
import com.mbh.CryptoTrade.view.ITraderDetailsView;
import com.mbh.CryptoTrade.view.TraderDetailsView;

@SpringBootTest
public class TraderDetailsFilterTest {

	@Autowired
	private TraderDetailsFilter traderDetailsFilter;

	@MockBean
	private TraderService mockTraderService;

	@Mock
	private HttpServletRequest mockRequest;
	@Mock
	private HttpServletResponse mockResponse;
	@Mock
	private FilterChain mockFilterChain;
	@Mock
	private Principal mockPrincipal;

	@Test
	public void that_traderDetails_are_added_to_request_when_principal_is_not_null_and_trader_exists()
			throws Exception {
		String username = "matthew.h";
		Trader trader = new Trader(1, username, "password");
		ITraderDetailsView traderDetails = new TraderDetailsView(trader);
		when(mockRequest.getUserPrincipal()).thenReturn(mockPrincipal);
		when(mockPrincipal.getName()).thenReturn(username);
		when(mockTraderService.getTraderByUsername(username)).thenReturn(traderDetails);
		traderDetailsFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);
		verify(mockRequest).getUserPrincipal();
		verify(mockTraderService).getTraderByUsername(username);
		verify(mockRequest).setAttribute("trader", traderDetails);
		verify(mockFilterChain).doFilter(mockRequest, mockResponse);
	}

	@Test
	public void that_traderDetails_are_not_added_to_request_when_principal_is_null() throws Exception {
		traderDetailsFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);
		verify(mockRequest).getUserPrincipal();
		verify(mockTraderService, never()).getTraderByUsername(anyString());
		verify(mockRequest, never()).setAttribute(eq("trader"), any(ITraderDetailsView.class));
		verify(mockFilterChain).doFilter(mockRequest, mockResponse);
	}

	@Test
	public void that_traderDetails_are_not_added_to_request_when_trader_does_not_exist() throws Exception {
		String username = "matthew.h";
		when(mockRequest.getUserPrincipal()).thenReturn(mockPrincipal);
		when(mockPrincipal.getName()).thenReturn(username);
		when(mockTraderService.getTraderByUsername(username)).thenThrow(new NotFoundException("Trader not found"));
		traderDetailsFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);
		verify(mockRequest).getUserPrincipal();
		verify(mockTraderService).getTraderByUsername(username);
		verify(mockRequest, never()).setAttribute(eq("trader"), any(ITraderDetailsView.class));
		verify(mockFilterChain).doFilter(mockRequest, mockResponse);
	}

}
