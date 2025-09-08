package dev.davidCMs.vkengine.vk;

import dev.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineInputAssemblyStateCreateInfo;

public class VkPipelineInputAssemblyStateBuilder implements Copyable {

	private VkPrimitiveTopology primitiveTopology;
	private boolean primitiveRestartEnable;

	public VkPipelineInputAssemblyStateCreateInfo build(MemoryStack stack) {
		VkPipelineInputAssemblyStateCreateInfo info = VkPipelineInputAssemblyStateCreateInfo.calloc(stack);
		info.sType$Default();
		info.topology(primitiveTopology.bit);
		info.primitiveRestartEnable(primitiveRestartEnable);
		return info;
	}

	public VkPrimitiveTopology getPrimitiveTopology() {
		return primitiveTopology;
	}

	public VkPipelineInputAssemblyStateBuilder setPrimitiveTopology(VkPrimitiveTopology primitiveTopology) {
		this.primitiveTopology = primitiveTopology;
		return this;
	}

	public boolean isPrimitiveRestartEnable() {
		return primitiveRestartEnable;
	}

	public VkPipelineInputAssemblyStateBuilder setPrimitiveRestartEnable(boolean primitiveRestartEnable) {
		this.primitiveRestartEnable = primitiveRestartEnable;
		return this;
	}

	public VkPipelineInputAssemblyStateBuilder copy() {
		return new VkPipelineInputAssemblyStateBuilder()
				.setPrimitiveTopology(primitiveTopology)
				.setPrimitiveRestartEnable(primitiveRestartEnable);
	}
}
