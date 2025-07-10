package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineDepthStencilStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDynamicStateCreateInfo;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

public class VkPipelineDynamicStateBuilder implements Copyable {

	private Set<VkDynamicState> dynamicStates;

	private IntBuffer getDynamicStatesBuffer(MemoryStack stack) {
		IntBuffer buf = stack.callocInt(dynamicStates.size());
		int i = 0;
		for(VkDynamicState state : dynamicStates) {
			buf.put(i, state.bit);
			i++;
		}
		return buf;
	}

	public VkPipelineDynamicStateCreateInfo build(MemoryStack stack) {
		VkPipelineDynamicStateCreateInfo info = VkPipelineDynamicStateCreateInfo.calloc(stack);
		info.sType$Default();
		info.pDynamicStates(getDynamicStatesBuffer(stack));
		return info;
	}

	public Set<VkDynamicState> getDynamicStates() {
		return dynamicStates;
	}

	public VkPipelineDynamicStateBuilder setDynamicStates(Set<VkDynamicState> dynamicStates) {
		this.dynamicStates = dynamicStates;
		return this;
	}

	public VkPipelineDynamicStateBuilder copy() {
		return new VkPipelineDynamicStateBuilder()
				.setDynamicStates(dynamicStates != null ? new HashSet<>(dynamicStates) : null);
	}
}
