package com.mbh.CryptoTrade.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mbh.CryptoTrade.dal.HoldingRepository;
import com.mbh.CryptoTrade.exception.InvalidTradeException;
import com.mbh.CryptoTrade.model.Coin;
import com.mbh.CryptoTrade.model.Holding;
import com.mbh.CryptoTrade.model.Trade;
import com.mbh.CryptoTrade.model.TradeType;
import com.mbh.CryptoTrade.model.Trader;
import com.mbh.CryptoTrade.validation.TradeValidator;

@SpringBootTest
public class TradeValidatorTest {

	@Autowired
	private TradeValidator tradeValidator;

	@MockBean
	private HoldingRepository mockHoldingRepo;

	@Mock
	private Trader mockTrader;
	@Mock
	private Holding mockHolding;

	@ParameterizedTest // "amount,unitPrice,balance"
	@CsvSource({ "100,10.5,1000", "500,6.5,2500.50", "135,0.5,65.60", "200,3.5,699.90" })
	public void that_validate_throws_invalidTradeException_when_cost_is_more_than_balance(long amount,
			BigDecimal unitPrice, BigDecimal balance) {
		Coin coin = new Coin("bitcoin", "btc", "Bitcoin");
		Trade trade = new Trade(0, amount, unitPrice, TradeType.BUY, coin, mockTrader);
		when(mockTrader.getBalance()).thenReturn(balance);
		assertThrows(InvalidTradeException.class, () -> tradeValidator.validate(trade));

	}

	@ParameterizedTest // "amountSelling,amountHeld"
	@CsvSource({ "100,99", "140,20", "24,23", "2,1" })
	public void that_validate_throws_invalidTradeException_when_selling_more_than_held(long amountSelling,
			long amountHeld) {
		BigDecimal unitPrice = new BigDecimal("125.00");
		Coin coin = new Coin("bitcoin", "btc", "Bitcoin");
		Trade trade = new Trade(0, amountSelling, unitPrice, TradeType.SELL, coin, mockTrader);
		when(mockHoldingRepo.findByCoinAndTrader(coin, mockTrader)).thenReturn(Optional.of(mockHolding));
		when(mockHolding.getAmount()).thenReturn(amountHeld);
		assertThrows(InvalidTradeException.class, () -> tradeValidator.validate(trade));
	}

	@Test
	public void that_validate_throwsinvalidTransactionException_when_selling_a_coin_not_in_holdings() {
		Coin coin = new Coin("bitcoin", "btc", "Bitcoin");
		Trade trade = new Trade(0, 100, new BigDecimal("125.00"), TradeType.SELL, coin, mockTrader);
		assertThrows(InvalidTradeException.class, () -> tradeValidator.validate(trade));
	}

	@ParameterizedTest // "amount,unitPrice,balance"
	@CsvSource({ "100,10.5,1050", "500,6.5,50000", "135,0.5,67.5", "200,3.5,705.90" })
	public void that_buyTrade_cost_less_than_balance_is_valid(long amount, BigDecimal unitPrice, BigDecimal balance)
			throws InvalidTradeException {
		Coin coin = new Coin("bitcoin", "btc", "Bitcoin");
		Trade trade = new Trade(0, amount, unitPrice, TradeType.BUY, coin, mockTrader);
		when(mockHoldingRepo.findByCoinAndTrader(coin, mockTrader)).thenReturn(Optional.of(mockHolding));
		when(mockTrader.getBalance()).thenReturn(balance);
		tradeValidator.validate(trade);
	}

	@ParameterizedTest // "amountSelling,amountHeld"
	@CsvSource({ "100,100", "140,220", "24,25", "2,3" })
	public void that_sellTrade_for_amount_less_than_amount_held_is_valid(long amountSelling, long amountHeld) throws InvalidTradeException {
		BigDecimal unitPrice = new BigDecimal("125.00");
		Coin coin = new Coin("bitcoin", "btc", "Bitcoin");
		Trade trade = new Trade(0, amountSelling, unitPrice, TradeType.SELL, coin, mockTrader);
		when(mockHoldingRepo.findByCoinAndTrader(coin, mockTrader)).thenReturn(Optional.of(mockHolding));
		when(mockHolding.getAmount()).thenReturn(amountHeld);
		tradeValidator.validate(trade);
	}

}
