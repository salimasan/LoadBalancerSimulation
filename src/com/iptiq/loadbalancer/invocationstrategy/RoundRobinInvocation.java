package com.iptiq.loadbalancer.invocationstrategy;

import java.util.concurrent.CopyOnWriteArrayList;
import com.iptiq.loadbalancer.IProviderController;

class RoundRobinInvocation extends InvocationBase {

	RoundRobinInvocation() {
		super.providers = new CopyOnWriteArrayList<IProviderController>();
		super.iterator = providers.iterator();
	}

	public IProviderController getNext() {
		if (providers.isEmpty()) {
			throw new RuntimeException("No active providers!");
		}
		IProviderController control, first;
		first = control = getNextCircular();
		do {
			if (control.hasAvailableCapacity()) {
				return control;
			}
			control = getNextCircular();
		} while (!first.equals(control));

		throw new RuntimeException("All providers are busy!");
	}

	private IProviderController getNextCircular() {
		if (!iterator.hasNext()) {
			// Turn to the first element
			iterator = providers.iterator();
		}
		return iterator.next();
	}
}
