package com.iptiq.loadbalancer;

import com.iptiq.loadbalancer.common.IProvider;

public interface IProviderController extends IProvider {
	
	public boolean hasAvailableCapacity();
}
