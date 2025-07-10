package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineTessellationStateCreateInfo;

public class VkPipelineTessellationStateBuilder implements Copyable {
	private int patchControlPoints;

	public VkPipelineTessellationStateCreateInfo build(MemoryStack stack) {
		VkPipelineTessellationStateCreateInfo info = VkPipelineTessellationStateCreateInfo.calloc(stack);
		info.sType$Default();
		info.patchControlPoints(patchControlPoints);
		return info;
	}

	public int getPatchControlPoints() {
		return patchControlPoints;
	}

	public VkPipelineTessellationStateBuilder setPatchControlPoints(int patchControlPoints) {
		this.patchControlPoints = patchControlPoints;
		return this;
	}

	public VkPipelineTessellationStateBuilder copy() {
		return new VkPipelineTessellationStateBuilder()
				.setPatchControlPoints(patchControlPoints);
	}
}
