package com.mbh.CryptoTrade.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import com.mbh.CryptoTrade.exception.ControllerExceptionHandler;
import com.mbh.CryptoTrade.exception.NotFoundException;

@SpringBootTest
public class ControllerExceptionHandlerTest {

	@Autowired
	private ControllerExceptionHandler controllerExceptionHandler;

	@Mock
	private Model mockModel;

	@Test
	public void that_handle_notFoundException_adds_message_to_model_and_returns_notFoundPage() {
		NotFoundException e = new NotFoundException("Entity not found");
		String nextPage = controllerExceptionHandler.handle(mockModel, e);
		verify(mockModel).addAttribute("error", e.getMessage());
		assertEquals("notFound", nextPage);
	}

}
