package com.mbh.CryptoTrade.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mbh.CryptoTrade.exception.ApiError;

public class ApiErrorTest {

	@Test
	public void that_apiError_can_be_constructed_correctly() {
		String message = "Unable to find price history";
		String cause = "Server down";
		ApiError apiError = new ApiError(message, cause);
		assertEquals(message, apiError.getMessage());
		assertEquals(cause, apiError.getCause());
	}

	@Test
	public void that_apiError_setters_work_correctly() {
		String message = "Unable to find price history";
		String cause = "Coin does not exist";
		ApiError apiError = new ApiError(null, null);
		apiError.setMessage(message);
		apiError.setCause(cause);
		assertEquals(message, apiError.getMessage());
		assertEquals(cause, apiError.getCause());
	}

}
