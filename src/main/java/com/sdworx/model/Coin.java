package com.sdworx.model;

public class Coin {
	
	public static final Coin FIVE_CENT = new Coin(5);
	public static final Coin TEN_CENT = new Coin(10);
	public static final Coin TWENTY_CENT = new Coin(20);
	public static final Coin FIFTY_CENT = new Coin(50);
	public static final Coin ONE_EURO = new Coin(100);
	public static final Coin TWO_EURO = new Coin(200);
	
	private int value; // in cent
	
	private Coin(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof Coin) {
			return ((Coin) o).value == value;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return value;
	}
}
