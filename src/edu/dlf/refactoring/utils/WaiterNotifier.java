package edu.dlf.refactoring.utils;

import org.apache.log4j.Logger;

import edu.dlf.refactoring.design.ServiceLocator;

public class WaiterNotifier {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final Object lock = new Object();
	private boolean wasSignalled = false;

	public void doWait() {
		synchronized (lock) {
			while (!wasSignalled) {
				try {
					lock.wait();
				} catch (Exception e) {
					logger.fatal(e);
				}
			}
			wasSignalled = false;
		}
	}

	public void doNotify() {
		synchronized (lock) {
			wasSignalled = true;
			lock.notify();
		}
	}
}