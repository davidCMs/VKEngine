package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VK14;

public class VkPipelineContext {

	private final VkGraphicsPipelineBuilder builder;
	private final VkDeviceContext device;

	private long pipeline;
	private long pipelineLayout;

	public VkPipelineContext(VkGraphicsPipelineBuilder builder, VkDeviceContext device) {
		this.builder = builder;
		this.device = device;
		rebuild();
	}

	public void rebuild() {
		VkGraphicsPipelineBuilder.PipelineBuildInfo info = builder.build(device);
		pipeline = info.pipeline();
		pipelineLayout = info.layout();
	}

	public VkGraphicsPipelineBuilder getBuilder() {
		return builder;
	}

	public VkDeviceContext getDevice() {
		return device;
	}

	public long getPipeline() {
		return pipeline;
	}

	public long getPipelineLayout() {
		return pipelineLayout;
	}

	public void destroy() {
		VK14.vkDestroyPipeline(device.device(), pipeline, null);
		VK14.vkDestroyPipelineLayout(device.device(), pipelineLayout, null);
	}

}
