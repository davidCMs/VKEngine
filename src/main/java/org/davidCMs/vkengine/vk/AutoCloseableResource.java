package org.davidCMs.vkengine.vk;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AutoCloseableResource implements AutoCloseable {

	private final AtomicBoolean closed = new AtomicBoolean(false);

	public boolean isClosed() {
		return closed.get();
	}

	protected void check() {
		if (isClosed()) throw new ClosedResourceException();
	}

	@Override
	public void close() {
		if (!closed.compareAndSet(false, true))
			throw new IllegalStateException("Resource has already ben closed");
	}
}
