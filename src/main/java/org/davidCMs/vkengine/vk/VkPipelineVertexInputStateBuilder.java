package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

public class VkPipelineVertexInputStateBuilder implements Copyable {

	//todo implement

	public VkPipelineVertexInputStateCreateInfo build(MemoryStack stack) {
		VkPipelineVertexInputStateCreateInfo info = VkPipelineVertexInputStateCreateInfo.calloc(stack);
		info.sType$Default();
		info.pVertexAttributeDescriptions(VkVertexInputAttributeDescription.calloc(0, stack));
		info.pVertexBindingDescriptions(VkVertexInputBindingDescription.calloc(0, stack));
		return info;
	}

	public VkPipelineVertexInputStateBuilder copy() {
		return this; //todo implement
	}
}
