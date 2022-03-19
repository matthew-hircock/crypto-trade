package com.mbh.CryptoTrade.setup;

import java.math.MathContext;
import java.math.RoundingMode;

public class Constants {

	private static MathContext mathContext = new MathContext(8, RoundingMode.HALF_UP);

	public static MathContext getMathContext() {
		return mathContext;
	}

}
