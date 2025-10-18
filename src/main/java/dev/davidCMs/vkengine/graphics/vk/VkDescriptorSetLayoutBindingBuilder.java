package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.graphics.shader.ShaderStage;
import dev.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VkDescriptorSetLayoutBindingBuilder implements Copyable {
	private int binding;
	private VkDescriptorType descriptorType;
	private Set<ShaderStage> stageFlags;
	private List<Long> samplers;

	public VkDescriptorSetLayoutBinding build(MemoryStack stack) {
		VkDescriptorSetLayoutBinding info = VkDescriptorSetLayoutBinding.calloc(stack);
		info.binding(binding);
		info.descriptorType(descriptorType.bit);
		info.stageFlags(ShaderStage.getVkMaskOf(stageFlags));

		LongBuffer samplersLB = stack.mallocLong(samplers.size());
		for (int i = 0; i < samplers.size(); i++) {
			samplersLB.put(i, samplers.get(i));
		}

		info.pImmutableSamplers(samplersLB);

		return info;
	}

	public int getBinding() {
		return binding;
	}

	public VkDescriptorSetLayoutBindingBuilder setBinding(int binding) {
		this.binding = binding;
		return this;
	}

	public VkDescriptorType getDescriptorType() {
		return descriptorType;
	}

	public VkDescriptorSetLayoutBindingBuilder setDescriptorType(VkDescriptorType descriptorType) {
		this.descriptorType = descriptorType;
		return this;
	}

	public Set<ShaderStage> getStageFlags() {
		return stageFlags;
	}

	public VkDescriptorSetLayoutBindingBuilder setStageFlags(Set<ShaderStage> stageFlags) {
		this.stageFlags = stageFlags;
		return this;
	}

	public List<Long> getSamplers() {
		return samplers;
	}

	public VkDescriptorSetLayoutBindingBuilder setSamplers(List<Long> samplers) {
		this.samplers = samplers;
		return this;
	}

	@Override
	public Copyable copy() {
		return new VkDescriptorSetLayoutBindingBuilder()
				.setBinding(binding)
				.setDescriptorType(descriptorType)
				.setStageFlags(stageFlags != null ? new HashSet<>(stageFlags) : null)
				.setSamplers(samplers != null ? new ArrayList<>(samplers) : null);
	}
}
