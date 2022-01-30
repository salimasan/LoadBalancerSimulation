package com.iptiq.loadbalancer.invocationstrategy;

import com.iptiq.loadbalancer.LoadBalancerParams;

public enum InvocationType {

	RANDOM(new RandomInvocation()), ROUNDROBIN(new RoundRobinInvocation());

	private InvocationBase invoker;

	private InvocationType(InvocationBase invoker) {
		this.invoker = invoker;
	}

	public IProviderInvocation getInvoker(LoadBalancerParams params) {
		 this.invoker.setParams(params);
		 return this.invoker;
	}
}
