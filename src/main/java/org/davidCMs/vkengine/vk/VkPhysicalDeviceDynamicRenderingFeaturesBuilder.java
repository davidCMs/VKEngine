package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPhysicalDeviceDynamicRenderingFeatures;

public class VkPhysicalDeviceDynamicRenderingFeaturesBuilder extends PNextChainable {

	@Override
	public long getpNext(MemoryStack stack) {
		VkPhysicalDeviceDynamicRenderingFeatures features = VkPhysicalDeviceDynamicRenderingFeatures.calloc(stack);
		features.sType$Default();
		features.pNext(getNextpNext(stack));
		features.dynamicRendering(true);
		return features.address();
	}

	@Override
	public PNextChainable copy() {
		return this;
	}
}
