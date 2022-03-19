package com.mbh.CryptoTrade.setup;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
public class WebClientConfigTest {

	private static final String PING_URL = "/ping";

	@Autowired
	private WebClient webClient;

	@Test
	public void that_webClient_can_ping_api() {
		webClient.get().uri(PING_URL).retrieve().onStatus(status -> !status.is2xxSuccessful(), c -> fail());
	}

}
