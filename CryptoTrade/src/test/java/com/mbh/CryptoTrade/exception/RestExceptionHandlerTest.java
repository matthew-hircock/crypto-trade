package com.mbh.CryptoTrade.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.mbh.CryptoTrade.exception.ApiError;
import com.mbh.CryptoTrade.exception.RestExceptionHandler;

@SpringBootTest
public class RestExceptionHandlerTest {

	@Autowired
	private RestExceptionHandler restExceptionHandler;
	
	@Mock
	private WebClientResponseException mockWebClientResponseException;

	@Test
	public void that_handle_webClientResponseException_returns_apiError_with_cause_as_message_from_exception() {
		String cause = "Coin not found";
		when(mockWebClientResponseException.getMessage()).thenReturn(cause);
		ApiError apiError = restExceptionHandler.handle(mockWebClientResponseException);
		assertEquals(cause, apiError.getCause());
		assertNotNull(apiError.getMessage());
	}

}
