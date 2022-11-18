package com.sdworx.model;

import java.util.ArrayList;
import java.util.List;

public class OrderReturn {
	private Product product;
	private List<Coin> coins = new ArrayList<>();
	
	public OrderReturn(Product p, List<Coin> coins) {
		this.product = p;
		if (coins != null && !coins.isEmpty()) {
			this.coins.addAll(coins);
		}
	}
	
	public Product getProduct() {
		return product;
	}

	public List<Coin> getCoins() {
		return coins;
	}

}
