package com.sdworx.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CoinTest {
	
	@Test
	public void testEquals() {
		Coin coin1 = Coin.FIVE_CENT;
		Coin coin2 = Coin.TEN_CENT;
		Coin coin3 = Coin.FIVE_CENT;
		
		Assertions.assertEquals(coin1, coin3);
		Assertions.assertNotEquals(coin1, coin2);
	}
	
	@Test
	public void testGetValue() {
		Assertions.assertEquals(5, Coin.FIVE_CENT.getValue());
		Assertions.assertEquals(10, Coin.TEN_CENT.getValue());
		Assertions.assertEquals(20, Coin.TWENTY_CENT.getValue());
		Assertions.assertEquals(50, Coin.FIFTY_CENT.getValue());
		Assertions.assertEquals(100, Coin.ONE_EURO.getValue());
		Assertions.assertEquals(200, Coin.TWO_EURO.getValue());
	}
	
	@Test
	public void testHashCode() {
		Coin coin = Coin.ONE_EURO;
		Assertions.assertEquals(100, coin.getValue());
	}

}
