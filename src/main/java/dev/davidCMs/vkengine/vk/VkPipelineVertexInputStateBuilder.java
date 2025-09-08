package dev.davidCMs.vkengine.vk;

import dev.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;

import java.util.HashSet;
import java.util.Set;

public class VkPipelineVertexInputStateBuilder implements Copyable {

	private Set<VkVertexInputBindingDescription> vertexBindingDescriptions;
	private Set<VkVertexInputAttributeDescription> vertexAttributeDescriptions;

	private org.lwjgl.vulkan.VkVertexInputBindingDescription.Buffer getVertexBindingDescriptionsBuffer(MemoryStack stack) {
		org.lwjgl.vulkan.VkVertexInputBindingDescription.Buffer buf = org.lwjgl.vulkan.VkVertexInputBindingDescription.calloc(vertexBindingDescriptions.size(), stack);
		int i = 0;
		for (VkVertexInputBindingDescription vertexBindingDescription : vertexBindingDescriptions) {
			buf.put(i, vertexBindingDescription.toNative(stack));
			i++;
		}
		return buf;
	}

	private org.lwjgl.vulkan.VkVertexInputAttributeDescription.Buffer getVertexAttributeDescriptionsBuffer(MemoryStack stack) {
		org.lwjgl.vulkan.VkVertexInputAttributeDescription.Buffer buf = org.lwjgl.vulkan.VkVertexInputAttributeDescription.calloc(vertexAttributeDescriptions.size(), stack);
		int i = 0;
		for (VkVertexInputAttributeDescription vertexAttributeDescription : vertexAttributeDescriptions) {
			buf.put(i, vertexAttributeDescription.toNative(stack));
			i++;
		}
		return buf;
	}

	public VkPipelineVertexInputStateCreateInfo build(MemoryStack stack) {
		VkPipelineVertexInputStateCreateInfo info = VkPipelineVertexInputStateCreateInfo.calloc(stack);
		info.sType$Default();
		info.pVertexAttributeDescriptions(getVertexAttributeDescriptionsBuffer(stack));
		info.pVertexBindingDescriptions(getVertexBindingDescriptionsBuffer(stack));
		return info;
	}

	public Set<VkVertexInputBindingDescription> getVertexBindingDescriptions() {
		return vertexBindingDescriptions;
	}

	public VkPipelineVertexInputStateBuilder setVertexBindingDescriptions(Set<VkVertexInputBindingDescription> vertexBindingDescriptions) {
		this.vertexBindingDescriptions = vertexBindingDescriptions;
		return this;
	}

	public Set<VkVertexInputAttributeDescription> getVertexAttributeDescriptions() {
		return vertexAttributeDescriptions;
	}

	public VkPipelineVertexInputStateBuilder setVertexAttributeDescriptions(Set<VkVertexInputAttributeDescription> vertexAttributeDescriptions) {
		this.vertexAttributeDescriptions = vertexAttributeDescriptions;
		return this;
	}

	public VkPipelineVertexInputStateBuilder copy() {
		return new VkPipelineVertexInputStateBuilder()
				.setVertexBindingDescriptions(vertexBindingDescriptions == null ? null : new HashSet<>(vertexBindingDescriptions))
				.setVertexAttributeDescriptions(vertexAttributeDescriptions == null ? null : new HashSet<>(vertexAttributeDescriptions));
	}
}
