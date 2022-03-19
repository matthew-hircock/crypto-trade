package com.mbh.CryptoTrade.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvalidTradeException extends Exception {

	private List<String> errors = new ArrayList<>();

	public InvalidTradeException(String error) {
		super();
		this.errors = Arrays.asList(error);
	}

	public InvalidTradeException(List<String> errors) {
		super();
		this.errors = errors;
	}

	public List<String> getErrors() {
		return errors;
	}

}
