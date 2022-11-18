package com.sdworx.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.sdworx.exception.OutOfStockException;
import com.sdworx.exception.UnsufficientAmountException;
import com.sdworx.model.Coin;
import com.sdworx.model.OrderReturn;
import com.sdworx.model.Product;
import com.sdworx.model.State;

public class DispenserImpl implements Dispenser {
	private static final long TIME_OUT = 2 * 60 * 1000; // 2 minutes
	private static final long CHECK_PERIOD = 100; // check timeout every 100 ms
	
	private List<Coin> coins = new ArrayList<>();
	private List<Product> products = new ArrayList<>();
	private List<Coin> insertedCoins = new ArrayList<>();
	
	private int currentBest = Integer.MAX_VALUE;
	private long lastAction;
	private State state = State.IDLE;
	
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	
	public DispenserImpl(List<Coin> coins, List<Product> products) {
		this.coins.addAll(coins);
		this.products.addAll(products);
		scheduledExecutorService.scheduleAtFixedRate(() -> checkTimeoutAndReset(), CHECK_PERIOD, CHECK_PERIOD, TimeUnit.MILLISECONDS);
	}
	
	
	@Override
	public synchronized void insertCoin(Coin coin) {
		if (coin != null) {
			state = State.INSERTING_COIN;
			insertedCoins.add(coin);
			lastAction = System.currentTimeMillis();
		}
	}
	
	@Override
	public synchronized void addProduct(Product product) {
		if (product != null) {
			this.products.add(product);
		}
	}


	@Override
	public synchronized void addProducts(List<Product> products) {
		if (products != null && !products.isEmpty()) {
			this.products.addAll(products);
		}
	}
	
	@Override
	public synchronized List<Coin> cancel() {
		state = State.CANCEL;
		List<Coin> results = new ArrayList<>();
		results.addAll(insertedCoins);
		insertedCoins.clear();
		lastAction = System.currentTimeMillis();
		return results;
	}
	
	@Override
	public synchronized OrderReturn type(Product product) throws UnsufficientAmountException, OutOfStockException {
		state = State.ORDERING;
		lastAction = System.currentTimeMillis();
		currentBest = Integer.MAX_VALUE;
		OrderReturn orderReturn = null;
		if (!products.contains(product)) {
			throw new OutOfStockException("Product " + product.getName() + " is out of stock");
		}
		int totalAmountInserted = displayTotalAmountOfCoinsInserted();
		if (totalAmountInserted < product.getPrice()) {
			throw new UnsufficientAmountException("Unsufficient amount");
		} if (totalAmountInserted == product.getPrice()) {
			orderReturn = new OrderReturn(product, Collections.emptyList());
		} else {
			orderReturn = calculateReturn(totalAmountInserted - product.getPrice(), product);
		}
		
		products.remove(product);
		orderReturn.getCoins().forEach(coin -> coins.remove(coin));
		coins.addAll(insertedCoins);
		insertedCoins.clear();
		state = State.RETURN_ORDER;
		return orderReturn;
	}


	@Override
	public synchronized int displayTotalAmountOfCoinsInserted() {
		return insertedCoins.stream().map(coin -> coin.getValue()).reduce(0, Integer::sum);
	}

	
	@Override
	public State getState() {
		return state;
	}
	
	
	@Override
	public int countTotalProducts() {
		return products.size();
	}


	@Override
	public int countProducts(Product product) {
		return (int)products.stream().filter(element -> element.equals(product)).count();
	}
	
	
	@Override
	public synchronized void preDestroy() {
		this.scheduledExecutorService.shutdown();
		state = State.OUT_OF_SERVICE;
	}
	
	
	private OrderReturn calculateReturn(int remainder, Product product) {
		List <Coin> availableCoins = new ArrayList<>();
		availableCoins.addAll(coins);
		availableCoins.addAll(insertedCoins);
		availableCoins.sort((coin1, coin2) -> coin1.getValue() - coin2.getValue());
		
		List<Coin> bestSolutions = new ArrayList<>();
		
		findSolutionToReturnCoins(remainder, availableCoins, bestSolutions, new ArrayList<>());
		
		OrderReturn orderReturn = new OrderReturn(product, bestSolutions);
		
		return orderReturn;
	}
	
	private void findSolutionToReturnCoins(int remainder, List<Coin> sortedAvailableCoins,
			List<Coin> currentBestSolutions, List<Coin> tempSolutions) {
		if (remainder == 0) {
			currentBest = 0;
			currentBestSolutions.clear();
			currentBestSolutions.addAll(tempSolutions);
			return;
		}
		if (currentBest == 0) {
			return;
		}
		// list of coins that are smaller than the remainder
		List<Coin> newSortedAvailableCoins = sortedAvailableCoins.stream().filter(coin -> coin.getValue() <= remainder).collect(Collectors.toList());
		if (newSortedAvailableCoins.isEmpty()) {
			if (currentBest > remainder) {
				currentBest = remainder;
				currentBestSolutions.clear();
				currentBestSolutions.addAll(tempSolutions);
			}
			return;
		}

		// try all possible solutions
		for (int i = newSortedAvailableCoins.size() - 1; i >= 0; i--) {
			List<Coin> tempNewSortedAvailableCoins = new ArrayList<>();
			tempNewSortedAvailableCoins.addAll(newSortedAvailableCoins);
			tempNewSortedAvailableCoins.remove(i);
			List<Coin> tempTempSolutions = new ArrayList<>();
			tempTempSolutions.addAll(tempSolutions);
			tempTempSolutions.add(newSortedAvailableCoins.get(i));
			findSolutionToReturnCoins(remainder - newSortedAvailableCoins.get(i).getValue(), tempNewSortedAvailableCoins, currentBestSolutions, tempTempSolutions);
			if (currentBest == 0) {
				break;
			}
		}
	}
	
	private synchronized void checkTimeoutAndReset() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastAction >= TIME_OUT) {
			coins.addAll(insertedCoins);
			insertedCoins.clear();
			state = State.IDLE;
		}
		
	}

}
