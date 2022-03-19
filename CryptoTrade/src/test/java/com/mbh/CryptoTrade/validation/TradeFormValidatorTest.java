package com.mbh.CryptoTrade.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mbh.CryptoTrade.exception.InvalidTradeException;
import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.validation.TradeFormValidator;
import com.mbh.CryptoTrade.view.TradeForm;

@SpringBootTest
public class TradeFormValidatorTest {

	@Autowired
	private TradeFormValidator tradeFormValidator;

	@Test
	public void that_validate_throws_invalidTradeException_with_errors_when_coinId_is_null() {
		TradeForm tradeForm = new TradeForm(null, BigDecimal.valueOf(53), 100, TradeType.BUY);
		InvalidTradeException e = assertThrows(InvalidTradeException.class,
				() -> tradeFormValidator.validate(tradeForm));
		assertEquals(1, e.getErrors().size());
	}

	@Test
	public void that_validate_throws_invalidTradeException_with_errors_when_amount_is_less_than_zero() {
		TradeForm tradeForm = new TradeForm("bitcoin", BigDecimal.valueOf(25), -5, TradeType.BUY);
		InvalidTradeException e = assertThrows(InvalidTradeException.class,
				() -> tradeFormValidator.validate(tradeForm));
		assertEquals(1, e.getErrors().size());
	}

	@Test
	public void that_validate_throws_invalidTradeException_with_errors_when_type_is_null() {
		TradeForm tradeForm = new TradeForm("bitcoin", BigDecimal.valueOf(6), 25, null);
		InvalidTradeException e = assertThrows(InvalidTradeException.class,
				() -> tradeFormValidator.validate(tradeForm));
		assertEquals(1, e.getErrors().size());
	}

	@Test
	public void that_validate_throws_invalidTradeException_with_multiple_errors_when_multiple_invalid_form_fields() {
		TradeForm tradeForm = new TradeForm(null, BigDecimal.valueOf(210), -5, null);
		InvalidTradeException e = assertThrows(InvalidTradeException.class,
				() -> tradeFormValidator.validate(tradeForm));
		assertEquals(3, e.getErrors().size());
	}

	@ParameterizedTest // "coinId,amount,tradeType"
	@CsvSource({ "bitcoin,10,BUY", "dogecoin,14,SELL", "etherium,1223,BUY" })
	public void that_validate_does_not_throw_for_valid_transactions(String coinId, long amount, TradeType tradeType)
			throws InvalidTradeException {
		TradeForm tradeForm = new TradeForm(coinId, BigDecimal.valueOf(125), amount, tradeType);
		tradeFormValidator.validate(tradeForm);
	}

}
