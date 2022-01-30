package com.iptiq.loadbalancer.provider;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.iptiq.loadbalancer.common.IProvider;

public class Provider implements IProvider {

	private final UUID id = UUID.randomUUID();

	private final Random random = new Random();

	@Override
	public String get() {
		// Sleep up to 10 seconds to simulate real life request handling
		try {
			TimeUnit.SECONDS.sleep(random.nextInt(10));
		} catch (InterruptedException e) {
			System.out.println(e.getStackTrace());
		}
		return this.id.toString();
	}

	@Override
	public boolean check() {
		return this.random.nextBoolean();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}

		final Provider other = (Provider) obj;

		if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
