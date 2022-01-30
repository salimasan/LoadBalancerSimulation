package com.iptiq.loadbalancer.invocationstrategy;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.iptiq.loadbalancer.IProviderController;
import com.iptiq.loadbalancer.LoadBalancerParams;
import com.iptiq.loadbalancer.ProviderController;
import com.iptiq.loadbalancer.common.IProvider;
import com.iptiq.loadbalancer.common.IProviderManager;

public abstract class InvocationBase implements IProviderInvocation, IProviderManager {

	protected Collection<IProviderController> providers;

	protected Iterator<IProviderController> iterator;

	protected LoadBalancerParams params;

	public void setParams(LoadBalancerParams params) {
		this.params = params;
	}

	public boolean addProvider(IProvider provider) {
		if (providers.size() == params.getMaxProviderCount()) {
			return false;
		}
		return providers.add(new ProviderController(params, provider));
	}

	public boolean removeProvider(IProvider provider) {
		return providers.remove(new ProviderController(params, provider));
	}

	public Iterator<IProviderController> getIterator() {
		return providers.iterator();
	}
}
