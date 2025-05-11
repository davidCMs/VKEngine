package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;

import java.nio.FloatBuffer;

public class VkEDeviceQueueCreateInfo extends AutoCloseableResource {

	private final VkDeviceQueueCreateInfo info;

	VkEDeviceQueueCreateInfo(int qfIndex, float priority) {
		info = VkDeviceQueueCreateInfo.calloc();
		info.sType$Default();
		info.pQueuePriorities(MemoryUtil.memAllocFloat(1));

		setPriority(priority);
		info.queueFamilyIndex(qfIndex);
	}

	public int getQueueFamilyIndex() {
		check();
		return info.queueFamilyIndex();
	}

	public float getPriority() {
		check();
		return info.pQueuePriorities().get(0);
	}

	public void setPriority(float priority) {
		check();
		if (priority > 1 || priority < 0) throw new IllegalArgumentException("Priority must be in the range of 1-0, but it was:" +priority);
		info.pQueuePriorities().put(0, priority);
	}

	VkDeviceQueueCreateInfo getInfo() {
		check();
		return info;
	}

	@Override
	public void close(){
		super.close();
		MemoryUtil.memFree(info.pQueuePriorities());
		info.close();
	}
}
