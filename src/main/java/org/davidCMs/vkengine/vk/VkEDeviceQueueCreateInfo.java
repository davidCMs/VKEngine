package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;

import java.nio.FloatBuffer;

public class VkEDeviceQueueCreateInfo implements AutoCloseable {

	private final MemoryStack stack;
	private final VkDeviceQueueCreateInfo info;

	VkEDeviceQueueCreateInfo(int qfIndex, float priority) {
		stack = MemoryStack.stackPush();
		info = VkDeviceQueueCreateInfo.calloc(stack);
		info.sType$Default();

		FloatBuffer fb = stack.callocFloat(1);
		fb.put(priority);
		fb.flip();

		info.queueFamilyIndex(qfIndex);
		info.pQueuePriorities(fb);
	}

	public int getQueueFamilyIndex() {
		return info.queueFamilyIndex();
	}

	public float getPriority() {
		FloatBuffer buffer = info.pQueuePriorities();
		return buffer.get(0);
	}

	VkDeviceQueueCreateInfo getInfo() {return info;}

	@Override
	public void close() throws Exception {
		stack.close();
	}
}
