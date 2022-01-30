package com.iptiq.loadbalancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.iptiq.loadbalancer.common.IProvider;
import com.iptiq.loadbalancer.invocationstrategy.IProviderInvocation;

class HeartBeatChecker extends TimerTask {

	private final IProviderInvocation accessor;
	private final LoadBalancerParams params;
	private Map<IProvider, Integer> inactiveProviders = new HashMap<IProvider, Integer>();
	private ExecutorService executor;
	private Map<IProvider, Future<Boolean>> checkMap = new HashMap<IProvider, Future<Boolean>>();

	public HeartBeatChecker(IProviderInvocation accessor, LoadBalancerParams params) {
		this.accessor = accessor;
		this.params = params;
		this.executor = Executors.newFixedThreadPool(params.getMaxProviderCount());
	}

	@Override
	public void run() {

		checkActiveProviders();

		checkInactiveProviders();
	}

	private void checkActiveProviders() {
		checkMap.clear();
		Iterator<IProviderController> iter = accessor.getIterator();
		while (iter.hasNext()) {
			IProvider nextElem = iter.next();
			Future<Boolean> checkFuture = callCheck(nextElem);
			checkMap.put(nextElem, checkFuture);
		}
		for (Entry<IProvider, Future<Boolean>> entry : checkMap.entrySet()) {
			if (!checkResult(entry.getValue())) {
				inActivate(entry.getKey());
			}
		}
	}

	private void checkInactiveProviders() {
		checkMap.clear();
		for (Entry<IProvider, Integer> entry : inactiveProviders.entrySet()) {
			Future<Boolean> checkFuture = callCheck(entry.getKey());
			checkMap.put(entry.getKey(), checkFuture);
		}
		for (Entry<IProvider, Future<Boolean>> entry : checkMap.entrySet()) {
			if (checkResult(entry.getValue())) {
				int sucessCheckCount = inactiveProviders.get(entry.getKey());
				sucessCheckCount++;
				if (sucessCheckCount > params.getHeartbeatCheckerSuccessfulCheckTreshold()) {
					tryToActivate(entry.getKey());
				} else {
					inactiveProviders.put(entry.getKey(), sucessCheckCount);
				}
			}
		}
	}

	public boolean removeInactiveProvider(IProvider provider) {
		return inactiveProviders.remove(provider) != null;
	}

	private Future<Boolean> callCheck(IProvider provider) {
		Callable<Boolean> task = new Callable<Boolean>() {
			public Boolean call() {
				return provider.check();
			}
		};
		Future<Boolean> future = executor.submit(task);
		return future;
	}

	private boolean checkResult(Future<Boolean> future) {
		boolean result = false;
		try {
			result = future.get(params.getProviderTimeoutMilisec(), TimeUnit.MILLISECONDS);
		} catch (TimeoutException ex) {
			System.out.println(ex.getStackTrace());
		} catch (ExecutionException ex) {
			System.out.println(ex.getStackTrace());
		} catch (InterruptedException ex) {
			System.out.println(ex.getStackTrace());
		} finally {
			future.cancel(true);
		}
		return result;
	}

	private void inActivate(IProvider provider) {
		accessor.removeProvider(provider);
		inactiveProviders.put(provider, 0);
	}

	private void tryToActivate(IProvider provider) {
		// Try to add provider again, if success remove it from inactive list
		if (accessor.addProvider(provider)) {
			inactiveProviders.remove(provider);
		}
	}
}
