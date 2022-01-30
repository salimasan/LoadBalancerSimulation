package com.iptiq.loadbalancer.invocationstrategy;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import com.iptiq.loadbalancer.IProviderController;

class RandomInvocation extends InvocationBase {

	Random random = new Random();

	RandomInvocation() {
		super.providers = new CopyOnWriteArrayList<IProviderController>();
		super.iterator = providers.iterator();
	}

	@Override
	public IProviderController getNext() {
		if (providers.isEmpty()) {
			throw new RuntimeException("No active providers!");
		}

		int bias = random.nextInt(super.providers.size());
		for (int i = 0; i < providers.size(); i++) {
			IProviderController cont = (IProviderController) ((CopyOnWriteArrayList) providers)
					.get((i + bias) % providers.size());
			if (cont.hasAvailableCapacity()) {
				return cont;
			}
		}
		throw new RuntimeException("All providers are busy!");
	}
}
