package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;

public abstract class PNextChainable implements Copyable {

	protected PNextChainable pNext;

	public PNextChainable setpNext(PNextChainable pNext) {
		this.pNext = pNext;
		return this;
	}

	public abstract long getpNext(MemoryStack stack);

	protected long getNextpNext(MemoryStack stack) {
		return pNext != null ?
				pNext.getpNext(stack) :
				0;
	}

	public abstract PNextChainable copy();

}
