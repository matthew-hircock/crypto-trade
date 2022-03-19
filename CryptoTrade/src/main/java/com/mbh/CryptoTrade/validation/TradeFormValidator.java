package com.mbh.CryptoTrade.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.mbh.CryptoTrade.exception.InvalidTradeException;
import com.mbh.CryptoTrade.view.TradeForm;

@Component
public class TradeFormValidator {

	/**
	 * @param tradeForm
	 *            the trade form to be validated
	 * @throws InvalidTradeException
	 *             If there is at least one field in the trade form is invalid (e.g.
	 *             coin id is null, amount is less than 1, trade type is not BUY or
	 *             SELL)
	 */
	public void validate(TradeForm tradeForm) throws InvalidTradeException {
		List<String> errors = new ArrayList<>();
		if (tradeForm.getCoinId() == null) {
			errors.add("Invalid coin");
		}
		if (tradeForm.getAmount() <= 0) {
			errors.add("Must trade more than 1 unit");
		}
		if (tradeForm.getType() == null) {
			errors.add("Trade must be either buy or sell");
		}
		if (!errors.isEmpty()) {
			throw new InvalidTradeException(errors);
		}
	}

}
