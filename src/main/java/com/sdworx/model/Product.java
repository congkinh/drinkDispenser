package com.sdworx.model;

public class Product {
	
	public static final Product COCA = new Product("Coca", 100);
	public static final Product RED_BULL = new Product("Redbull", 125);
	public static final Product WATER = new Product("Water", 50);
	public static final Product ORANGE = new Product("Orange juice", 195);

	private String name;
	private int price; // in cent
	
	private Product(String name, int price) {
		this.name = name;
		this.price = price;
	}
	
	public String getName() {
		return name;
	}
	

	public int getPrice() {
		return price;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof Product) {
			Product p = (Product)o;
			return price == p.price && name.equals(p.name);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return price;
	}
	
}
