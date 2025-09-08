package dev.davidCMs.vkengine.vk;

import dev.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

public class VkPipelineShaderStageBuilder implements Copyable {

	private VkShaderModule module;
	private VkSpecializationInfoMapper mapper;
	private String entryPoint;

	public VkPipelineShaderStageCreateInfo build(MemoryStack stack) {

		if (module == null)
			throw new NullPointerException("module must be set");

		entryPoint = entryPoint == null ? "main" : entryPoint;

		VkPipelineShaderStageCreateInfo info = VkPipelineShaderStageCreateInfo.calloc(stack);
		info.sType$Default();
		info.stage(module.getStage().getVkBit());
		info.pName(stack.UTF8(entryPoint));
		info.module(module.getShaderModule());

		if (mapper != null)
			info.pSpecializationInfo(mapper.build(stack));

		return info;
	}

	public VkShaderModule getModule() {
		return module;
	}

	public VkPipelineShaderStageBuilder setModule(VkShaderModule module) {
		this.module = module;
		return this;
	}

	public VkSpecializationInfoMapper getMapper() {
		return mapper;
	}

	public VkPipelineShaderStageBuilder setMapper(VkSpecializationInfoMapper mapper) {
		this.mapper = mapper;
		return this;
	}

	public String getEntryPoint() {
		return entryPoint;
	}

	public VkPipelineShaderStageBuilder setEntryPoint(String entryPoint) {
		this.entryPoint = entryPoint;
		return this;
	}

	@Override
	public VkPipelineShaderStageBuilder copy() {
		return new VkPipelineShaderStageBuilder()
				.setEntryPoint(entryPoint)
				.setMapper(Copyable.safeCopy(mapper))
				.setModule(module);
	}
}
