package com.iptiq.client;


import com.iptiq.loadbalancer.LoadBalancer;
import com.iptiq.loadbalancer.LoadBalancerParams;
import com.iptiq.loadbalancer.common.ILoadBalancer;
import com.iptiq.loadbalancer.invocationstrategy.InvocationType;

public class Main {

	private static final InvocationType INVOCATION_TYPE = InvocationType.ROUNDROBIN;
	private static final int MAX_PROVIDER_COUNT = 10;
	private static final int PROVIDER_TIMEOUT_MILISEC = 1000;
	private static final int PROVIDER_PARALLEL_REQUEST_HANDLING_CAPACITY = 10;
	private static final int HEARTBEATCHECKER_SUCCESSFULL_CHECK_TRESHOLD = 2;
	private static final int HEARTBEATCHECKER_FREQUENCY_MILISEC = 2000;

	public static void main(String[] args) {

		System.out.println("Load Balancer is starting");
		LoadBalancerParams params = new LoadBalancerParams(INVOCATION_TYPE, MAX_PROVIDER_COUNT,
				PROVIDER_TIMEOUT_MILISEC, PROVIDER_PARALLEL_REQUEST_HANDLING_CAPACITY,
				HEARTBEATCHECKER_SUCCESSFULL_CHECK_TRESHOLD, HEARTBEATCHECKER_FREQUENCY_MILISEC);
		ILoadBalancer lb = LoadBalancer.getInstance(params);
		ClientSimulator client = new ClientSimulator(lb);
		client.simulate();
		System.out.println("Load Balance ended");
	}

}
