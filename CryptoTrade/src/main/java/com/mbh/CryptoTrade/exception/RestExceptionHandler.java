package com.mbh.CryptoTrade.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class RestExceptionHandler {

	private static Logger logger = LogManager.getLogger(RestExceptionHandler.class);

	@ExceptionHandler(WebClientResponseException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handle(WebClientResponseException e) {
		// return status of WebClientResponse
		// TODO: return a message too?
		logger.info("WebClientResponseException: statusCode = {}, message = {}", e.getRawStatusCode(), e.getMessage());
		return new ApiError("Unable to find coin price history", e.getMessage());
	}

}
