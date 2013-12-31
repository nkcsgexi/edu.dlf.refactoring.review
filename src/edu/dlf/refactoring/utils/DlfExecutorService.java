package edu.dlf.refactoring.utils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;

public final class DlfExecutorService implements ExecutorService{

	private final ExecutorService internalExecutor;
	private final Logger logger;

	@Inject
	public DlfExecutorService(Logger logger) {
		this.logger = logger;
		this.internalExecutor = MoreExecutors.listeningDecorator(Executors.
			newFixedThreadPool(1));
	}
	
	@Override
	public void execute(Runnable command) {
		this.internalExecutor.execute(command);
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		return this.internalExecutor.awaitTermination(timeout, unit);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		return this.internalExecutor.invokeAll(tasks);
	}

	@Override
	public <T> List<Future<T>> invokeAll(
			Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		return this.internalExecutor.invokeAll(tasks, timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		return this.internalExecutor.invokeAny(tasks);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return this.internalExecutor.invokeAny(tasks, timeout, unit);
	}

	@Override
	public boolean isShutdown() {
		return this.internalExecutor.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return this.internalExecutor.isTerminated();
	}

	@Override
	public void shutdown() {
		this.internalExecutor.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return this.internalExecutor.shutdownNow();
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return this.internalExecutor.submit(task);
	}

	@Override
	public Future<?> submit(Runnable task) {
		return this.submit(task);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return this.submit(task, result);
	}
}
