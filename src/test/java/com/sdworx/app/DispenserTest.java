package com.sdworx.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sdworx.exception.OutOfStockException;
import com.sdworx.exception.UnsufficientAmountException;
import com.sdworx.model.Coin;
import com.sdworx.model.OrderReturn;
import com.sdworx.model.Product;
import com.sdworx.model.State;

/*
 * 
 * We should write the script test from text that check the action, value, return, etc. But due to limited time,
 * I only write some tests here.
 */
public class DispenserTest {
	
	private Dispenser dispenser;
	
	@AfterEach
	public void afterEach() {
		if (dispenser != null) {
			dispenser.preDestroy();
			
			Assertions.assertEquals(State.OUT_OF_SERVICE, dispenser.getState());
		}
	}
	
	@Test
	public void testCancel() {
		dispenser = new DispenserImpl(Collections.emptyList(), Collections.emptyList());
		List<Coin> insertedCoins = Arrays.asList(Coin.FIVE_CENT, Coin.FIFTY_CENT, Coin.ONE_EURO);
		dispenser.insertCoin(Coin.FIVE_CENT);
		dispenser.insertCoin(Coin.FIFTY_CENT);
		dispenser.insertCoin(Coin.ONE_EURO);
		
		List<Coin> returns = dispenser.cancel();
		
		int totalAmountInserted = insertedCoins.stream().map(coin -> coin.getValue()).reduce(0, Integer::sum);
		int totalAmountReturned = returns.stream().map(coin -> coin.getValue()).reduce(0, Integer::sum);
		Assertions.assertEquals(totalAmountInserted, totalAmountReturned);
	}
	
	@Test
	public void testDisplayTotalAmountOfCoinsInserted() {
		dispenser = new DispenserImpl(Collections.emptyList(), Collections.emptyList());
		dispenser.insertCoin(Coin.TEN_CENT);
		dispenser.insertCoin(Coin.FIFTY_CENT);
		dispenser.insertCoin(Coin.ONE_EURO);
		
		Assertions.assertEquals(160, dispenser.displayTotalAmountOfCoinsInserted());
	}
	

	@Test
	public void testType() throws UnsufficientAmountException, OutOfStockException {
		dispenser = new DispenserImpl(Arrays.asList(Coin.FIVE_CENT, Coin.TWENTY_CENT, Coin.TWENTY_CENT, Coin.FIFTY_CENT, Coin.ONE_EURO, Coin.TWO_EURO),
				Arrays.asList(Product.ORANGE, Product.ORANGE, Product.COCA, Product.RED_BULL, Product.WATER));
		// total insert = 200
		insert200Cents(dispenser);
		
		OrderReturn orderReturn = dispenser.type(Product.ORANGE);
		Assertions.assertEquals(Product.ORANGE, orderReturn.getProduct());
		Assertions.assertEquals(1, orderReturn.getCoins().size());
		Assertions.assertEquals(Coin.FIVE_CENT, orderReturn.getCoins().get(0));
		
		// now in the Dispenser, there are 1 TWO_EURO, 2 ONE_EURO, 2 FIFTY_CENT, 4 TWENTY_CENT, 1 TEN_CENT
		
		// insert again 200 cents
		insert200Cents(dispenser);
		OrderReturn orderReturn2 = dispenser.type(Product.ORANGE);
		Assertions.assertEquals(Product.ORANGE, orderReturn2.getProduct());
		// now OrderReturn2 should return 0 coin as there is no longer FIVE cent in the Dispenser
		Assertions.assertTrue(orderReturn2.getCoins().isEmpty());
		
		// now in the Dispenser, there are 1 TWO_EURO, 3 ONE_EURO, 3 FIFTY_CENT, 6 TWENTY_CENT, 2 TEN_CENT
		
		// insert again 200 cents
		insert200Cents(dispenser);
		boolean foundException = false;
		try {
			dispenser.type(Product.ORANGE);
		} catch (OutOfStockException e) {
			foundException = true;
		}
		Assertions.assertTrue(foundException);
		
		List<Coin> cancelCoins = dispenser.cancel();
		int totalBack = cancelCoins.stream().map(coin -> coin.getValue()).reduce(0, Integer::sum);
		Assertions.assertEquals(200, totalBack); // should receive 200 cent back
		
		// now in the Dispenser, there are 1 TWO_EURO, 3 ONE_EURO, 3 FIFTY_CENT, 6 TWENTY_CENT, 2 TEN_CENT
		insertNew200Cents(dispenser);
		
		OrderReturn orderReturn3 = dispenser.type(Product.WATER);
		Assertions.assertEquals(Product.WATER, orderReturn3.getProduct());
		// now OrderReturn3 should receive 1 ONE_EURO, 1 FIFTY_CENT
		Assertions.assertEquals(2, orderReturn3.getCoins().size());
		Assertions.assertTrue(orderReturn3.getCoins().containsAll(Arrays.asList(Coin.ONE_EURO, Coin.FIFTY_CENT)));
		
		// now in the Dispenser, there are 2 TWO_EURO, 2 ONE_EURO, 2 FIFTY_CENT, 6 TWENTY_CENT, 2 TEN_CENT
		
		// check return 1 TWENTY_CENT (best possible change)
		insert150Cents(dispenser);
		OrderReturn orderReturn4 = dispenser.type(Product.RED_BULL);
		Assertions.assertEquals(Product.RED_BULL, orderReturn4.getProduct());
		Assertions.assertEquals(1, orderReturn4.getCoins().size());
		Assertions.assertEquals(Coin.TWENTY_CENT, orderReturn4.getCoins().get(0));
		
		// we should add more test to cover all possible combinations  ..
		
	}
	
	@Test
	public void testAddProduct() {
		dispenser = new DispenserImpl(Collections.emptyList(), Collections.emptyList());
		dispenser.addProduct(Product.COCA);
		Assertions.assertEquals(1, dispenser.countProducts(Product.COCA));
		Assertions.assertEquals(1, dispenser.countTotalProducts());
		Assertions.assertEquals(0, dispenser.countProducts(Product.WATER));
		Assertions.assertEquals(0, dispenser.countProducts(Product.ORANGE));
		Assertions.assertEquals(0, dispenser.countProducts(Product.RED_BULL));
	}
	
	
	@Test
	public void testAddProducts() {
		dispenser = new DispenserImpl(Collections.emptyList(), Collections.emptyList());
		dispenser.addProducts(Arrays.asList(Product.COCA, Product.ORANGE));
		Assertions.assertEquals(1, dispenser.countProducts(Product.COCA));
		Assertions.assertEquals(1, dispenser.countProducts(Product.ORANGE));
		Assertions.assertEquals(2, dispenser.countTotalProducts());
		Assertions.assertEquals(0, dispenser.countProducts(Product.WATER));
		Assertions.assertEquals(0, dispenser.countProducts(Product.RED_BULL));
	}
	
	@Test
	public void testState() throws UnsufficientAmountException, OutOfStockException {
		dispenser = new DispenserImpl(Arrays.asList(Coin.FIVE_CENT, Coin.TWENTY_CENT, Coin.TWENTY_CENT, Coin.FIFTY_CENT, Coin.ONE_EURO, Coin.TWO_EURO),
				Arrays.asList(Product.ORANGE, Product.ORANGE, Product.COCA, Product.RED_BULL, Product.WATER));
		
		Assertions.assertEquals(State.IDLE, dispenser.getState());
		dispenser.insertCoin(Coin.ONE_EURO);
		Assertions.assertEquals(State.INSERTING_COIN, dispenser.getState());
		
		OrderReturn orderReturn = dispenser.type(Product.WATER);
		Assertions.assertNotNull(orderReturn);
		Assertions.assertEquals(State.RETURN_ORDER, dispenser.getState());
		
		dispenser.insertCoin(Coin.TWO_EURO);
		dispenser.cancel();
		Assertions.assertEquals(State.CANCEL, dispenser.getState());
		
	}
	
	private void insert200Cents(Dispenser dispenser) {
		dispenser.insertCoin(Coin.TEN_CENT);
		dispenser.insertCoin(Coin.TWENTY_CENT);
		dispenser.insertCoin(Coin.TWENTY_CENT);
		dispenser.insertCoin(Coin.FIFTY_CENT);
		dispenser.insertCoin(Coin.ONE_EURO);
	}
	
	private void insert150Cents(Dispenser dispenser) {
		dispenser.insertCoin(Coin.FIFTY_CENT);
		dispenser.insertCoin(Coin.ONE_EURO);
	}
	
	private void insertNew200Cents(Dispenser dispenser) {
		dispenser.insertCoin(Coin.TWO_EURO);
	}

}
