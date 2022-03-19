package com.mbh.CryptoTrade.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {

	private Logger logger = LogManager.getLogger(ControllerExceptionHandler.class);

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handle(Model model, NotFoundException e) {
		logger.debug("NotFoundException - {}", e.getMessage());
		model.addAttribute("error", e.getMessage());
		return "notFound";
	}
	
	// TODO: handle BindException for TradeForm

}
