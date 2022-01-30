package com.iptiq.loadbalancer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.iptiq.loadbalancer.common.IProvider;

public class ProviderController implements IProviderController {

	private final IProvider provider;
	private LoadBalancerParams params;
	private final ThreadPoolExecutor executor;
	private final Callable<String> task = new Callable<String>() {
		public String call() {
			return provider.get();
		}
	};

	public ProviderController(LoadBalancerParams params, IProvider provider) {
		this.params = params;
		this.provider = provider;
		this.executor = (ThreadPoolExecutor) Executors
				.newFixedThreadPool(params.getProviderParallelRequestHandlingCapacity());
	}

	@Override
	public boolean hasAvailableCapacity() {
		return executor.getActiveCount() < params.getProviderParallelRequestHandlingCapacity();
	}

	@Override
	public String get() {
		Future<String> future = executor.submit(task);
		String result;
		try {
			result = future.get(params.getProviderTimeoutMilisec(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public boolean check() {
		return provider.check();
	}
}
