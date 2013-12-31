package edu.dlf.refactoring.utils;

import org.apache.log4j.Logger;

import edu.dlf.refactoring.design.ServiceLocator;

public abstract class WorkQueueItem implements Runnable{

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final String itemName;
	
	public WorkQueueItem(String itemName) {
		this.itemName = itemName;
	}
	
	@Override
	public final void run() {
		logger.info(itemName + " starts.");
		long startTime = System.currentTimeMillis();
		try{
			internalRun();
		}catch(Exception e) {
			logger.fatal("Item name: " + this.itemName + "\n" +e);
		}
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		logger.debug("Time for " + this.itemName + ": " + elapsedTime);
		callBack();
		logger.info(itemName + " ends.");
	}
	
	abstract protected void internalRun();
	protected void callBack() {}
}
