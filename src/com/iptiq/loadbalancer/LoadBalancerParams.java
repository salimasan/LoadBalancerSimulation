package com.iptiq.loadbalancer;

import com.iptiq.loadbalancer.invocationstrategy.InvocationType;

public class LoadBalancerParams {

	private final InvocationType invocationType;
	private final int maxProviderCount;
	private final int providerTimeoutMilisec;
	private final int providerParallelRequestHandlingCapacity;
	private final int heartbeatCheckerSuccessfulCheckTreshold;
	private final int heartbeatCheckerFrequencyMilisec;

	public LoadBalancerParams(InvocationType invocationType, int maxProviderCount, int providerTimeoutMilisec,
			int providerParallelRequestHandlingCapacity, int heartbeatCheckerSuccessfulCheckTreshold,
			int heartbeatCheckerFrequencyMilisec) {
		this.invocationType = invocationType;
		this.maxProviderCount = maxProviderCount;
		this.providerTimeoutMilisec = providerTimeoutMilisec;
		this.providerParallelRequestHandlingCapacity = providerParallelRequestHandlingCapacity;
		this.heartbeatCheckerSuccessfulCheckTreshold = heartbeatCheckerSuccessfulCheckTreshold;
		this.heartbeatCheckerFrequencyMilisec = heartbeatCheckerFrequencyMilisec;
	}

	public InvocationType getInvocationType() {
		return invocationType;
	}

	public int getMaxProviderCount() {
		return maxProviderCount;
	}

	public int getProviderTimeoutMilisec() {
		return providerTimeoutMilisec;
	}

	public int getProviderParallelRequestHandlingCapacity() {
		return providerParallelRequestHandlingCapacity;
	}

	public int getHeartbeatCheckerSuccessfulCheckTreshold() {
		return heartbeatCheckerSuccessfulCheckTreshold;
	}

	public int getHeartbeatCheckerFrequencyMilisec() {
		return heartbeatCheckerFrequencyMilisec;
	}
}
