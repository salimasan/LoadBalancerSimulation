package com.iptiq.loadbalancer.common;

public interface IProviderManager {

	public boolean addProvider(IProvider provider);

	public boolean removeProvider(IProvider provider);
}
