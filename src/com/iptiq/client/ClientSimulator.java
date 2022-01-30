package com.iptiq.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.iptiq.loadbalancer.common.ILoadBalancer;

public class ClientSimulator {

	private final int CLIENT_COUNT = 100;
	private final int CLIENT_TIMEOUT_MILISEC = 5000;
	private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(CLIENT_COUNT);
	private ILoadBalancer lb;
	private Future<String>[] futureArr = new Future[CLIENT_COUNT];
	private final Callable<String> task = new Callable<String>() {
		public String call() {
			return lb.get();
		}
	};

	public ClientSimulator(ILoadBalancer lb) {
		this.lb = lb;
	}

	public void simulate() {
		for (int i = 0; i < CLIENT_COUNT; i++) {
			futureArr[i] = executor.submit(task);
		}
		for (int i = 0; i < CLIENT_COUNT; i++) {
			try {
				String result = futureArr[i].get(CLIENT_TIMEOUT_MILISEC, TimeUnit.MILLISECONDS);
				System.out.println("The result of client " + i + " is " + result);
			} catch (TimeoutException ex) {
				System.out.println("The client " + i + " got a timeout !");
			} catch (Throwable ex) {
				System.out.println("The client " + i + " got an exception !" + ex.getStackTrace());
			} finally {
				futureArr[i].cancel(true);
			}
		}
	}
}
