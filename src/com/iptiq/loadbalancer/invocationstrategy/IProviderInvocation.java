package com.iptiq.loadbalancer.invocationstrategy;

import java.util.Iterator;

import com.iptiq.loadbalancer.IProviderController;
import com.iptiq.loadbalancer.common.IProviderManager;

public interface IProviderInvocation extends IProviderManager {

	public IProviderController getNext();

	public Iterator<IProviderController> getIterator();
}
