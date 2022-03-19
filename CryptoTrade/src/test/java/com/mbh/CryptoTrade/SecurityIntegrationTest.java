package com.mbh.CryptoTrade;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

	private static String[] unsecuredUrls = { "/login", "/coins" };
	private static String[] securedUrls = { "/trades", "/home" };

	@Autowired
	private MockMvc mvc;

	@Test
	public void that_get_unsecured_url_with_noAuthentication_returns_status200() throws Exception {
		for (String unsecuredUrl : unsecuredUrls) {
			mvc.perform(get(unsecuredUrl)).andDo(print()).andExpect(status().isOk());
		}
	}

	@Test
	public void that_get_secured_url_with_noAuthentication_returns_redirectLoginPage_with_status3xx() throws Exception {
		for (String securedUrl : securedUrls) {
			mvc.perform(get(securedUrl)).andDo(print()).andExpect(status().is3xxRedirection());
		}
	}

}
