package com.sdworx.app;

import java.util.List;

import com.sdworx.exception.OutOfStockException;
import com.sdworx.exception.UnsufficientAmountException;
import com.sdworx.model.Coin;
import com.sdworx.model.OrderReturn;
import com.sdworx.model.Product;
import com.sdworx.model.State;

public interface Dispenser {
	void addProduct(Product product);
	void addProducts(List<Product> products);
	State getState();
	void insertCoin(Coin coin);
	List<Coin> cancel();
	OrderReturn type(Product p) throws UnsufficientAmountException, OutOfStockException;
	int displayTotalAmountOfCoinsInserted();
	int countTotalProducts();
	int countProducts(Product product);
	void preDestroy();

}
