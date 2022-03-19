package com.mbh.CryptoTrade.exception;

public class ApiError {

	private String message;
	private String cause;

	public ApiError(String message, String cause) {
		super();
		this.message = message;
		this.cause = cause;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
