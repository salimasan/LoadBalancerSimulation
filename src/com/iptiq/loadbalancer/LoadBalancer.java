package com.iptiq.loadbalancer;

import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import com.iptiq.loadbalancer.common.ILoadBalancer;
import com.iptiq.loadbalancer.common.IProvider;
import com.iptiq.loadbalancer.invocationstrategy.IProviderInvocation;
import com.iptiq.loadbalancer.provider.Provider;

public class LoadBalancer implements ILoadBalancer {

	private static LoadBalancer theInstance = null;
	private IProviderInvocation invoker;
	private LoadBalancerParams params;
	private HeartBeatChecker checker;
	private final ThreadPoolExecutor workerExecutor;
	private final Callable<IProviderController> task = new Callable<IProviderController>() {
		public IProviderController call() {
			return invoker.getNext();
		}
	};

	public LoadBalancer(LoadBalancerParams params) {
		this.invoker = params.getInvocationType().getInvoker(params);
		this.params = params;
		this.workerExecutor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(params.getMaxProviderCount());
	}

	public static ILoadBalancer getInstance(LoadBalancerParams params) {
		if (theInstance == null) {
			synchronized (LoadBalancer.class) {
				theInstance = new LoadBalancer(params);
				for (int i = 0; i < params.getMaxProviderCount(); i++) {
					theInstance.addProvider(new Provider());
				}
				theInstance.checker = new HeartBeatChecker(theInstance.invoker, params);
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(theInstance.checker, 0, params.getHeartbeatCheckerFrequencyMilisec());
			}
		}
		return theInstance;
	}

	@Override
	public String get() {
		Future<IProviderController> future = workerExecutor.submit(task);
		try {
			IProviderController cont = future.get();
			return cont.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean addProvider(IProvider provider) {
		synchronized (this) {
			return invoker.addProvider(provider);
		}
	}

	@Override
	public boolean removeProvider(IProvider provider) {
		synchronized (this) {
			boolean result = invoker.removeProvider(provider);
			if (!result) {
				// If the provider could not be removed, try to find it among inactive providers
				result = theInstance.checker.removeInactiveProvider(provider);
			}
			return result;
		}
	}
}
