package com.sdworx.app;

import java.util.Arrays;

import com.sdworx.exception.OutOfStockException;
import com.sdworx.exception.UnsufficientAmountException;
import com.sdworx.model.Coin;
import com.sdworx.model.OrderReturn;
import com.sdworx.model.Product;

public class Main {

	public static void main(String[] args) {
		// This is main entry to run via Docker
		Dispenser dispenser = new DispenserImpl(Arrays.asList(Coin.FIVE_CENT, Coin.TWENTY_CENT, Coin.TWENTY_CENT, Coin.FIFTY_CENT, Coin.ONE_EURO, Coin.TWO_EURO),
				Arrays.asList(Product.ORANGE, Product.ORANGE, Product.COCA, Product.RED_BULL, Product.WATER));
		// total insert = 200
		insert200Cents(dispenser);
		
		try {
			OrderReturn orderReturn = dispenser.type(Product.ORANGE);
			System.out.println("Money back = " + orderReturn.getCoins().get(0));
		} catch (UnsufficientAmountException | OutOfStockException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

	}
	
	private static void insert200Cents(Dispenser dispenser) {
		dispenser.insertCoin(Coin.TEN_CENT);
		dispenser.insertCoin(Coin.TWENTY_CENT);
		dispenser.insertCoin(Coin.TWENTY_CENT);
		dispenser.insertCoin(Coin.FIFTY_CENT);
		dispenser.insertCoin(Coin.ONE_EURO);
	}

}
