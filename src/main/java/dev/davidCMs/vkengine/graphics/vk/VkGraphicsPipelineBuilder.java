package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.util.Copyable;
import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import java.nio.LongBuffer;
import java.util.List;

public class VkGraphicsPipelineBuilder implements Copyable {

	private VkPipelineCreateFlags flags;

	private List<VkPipelineShaderStageBuilder> stages;

	private VkPipelineVertexInputStateBuilder vertexInputState;
	private VkPipelineInputAssemblyStateBuilder inputAssemblyState;
	private VkPipelineTessellationStateBuilder tessellationState;
	private VkPipelineViewportStateBuilder viewportState;
	private VkPipelineRasterizationStateBuilder rasterizationState;
	private VkPipelineMultisampleStateBuilder multisampleState;
	private VkPipelineDepthStencilStateBuilder depthStencilState;
	private VkPipelineColorBlendStateBuilder colorBlendState;
	private VkPipelineDynamicStateBuilder dynamicState;

	private VkPipelineLayoutCreateInfoBuilder pipelineLayout;
	private PNextChainable pNext;

	public VkPipelineContext newContext(VkDeviceContext device) {
		return new VkPipelineContext(copy(), device);
	}

	private VkPipelineShaderStageCreateInfo.Buffer getStagesBuffer(MemoryStack stack) {
		VkPipelineShaderStageCreateInfo.Buffer buf = VkPipelineShaderStageCreateInfo.calloc(stages.size(), stack);
		for (int i = 0; i < stages.size(); i++) {
			buf.put(i, stages.get(i).build(stack));
		}
		return buf;
	}

	public VkGraphicsPipelineCreateInfo fillInfo(VkDeviceContext device, MemoryStack stack) {

		VkGraphicsPipelineCreateInfo info = VkGraphicsPipelineCreateInfo.calloc(stack);
		info.sType$Default();

		if (pNext != null)
			info.pNext(pNext.getpNext(stack));
		if (flags != null)
			info.flags((int) VkPipelineCreateFlags.getMaskOf(flags));
		if (stages != null)
			info.pStages(getStagesBuffer(stack));
		if (vertexInputState != null)
			info.pVertexInputState(vertexInputState.build(stack));
		if (inputAssemblyState != null)
			info.pInputAssemblyState(inputAssemblyState.build(stack));
		if (tessellationState != null)
			info.pTessellationState(tessellationState.build(stack));
		if (viewportState != null)
			info.pViewportState(viewportState.build(stack));
		if (rasterizationState != null)
			info.pRasterizationState(rasterizationState.build(stack));
		if (multisampleState != null)
			info.pMultisampleState(multisampleState.build(stack));
		if (depthStencilState != null)
			info.pDepthStencilState(depthStencilState.build(stack));
		if (colorBlendState != null)
			info.pColorBlendState(colorBlendState.build(stack));
		if (dynamicState != null)
			info.pDynamicState(dynamicState.build(stack));
		if (pipelineLayout != null)
			info.layout(pipelineLayout.build(device, stack));
		return info;
	}

	public record PipelineBuildInfo(long pipeline, long layout){}

	public static PipelineBuildInfo[] build(VkDeviceContext device, VkGraphicsPipelineBuilder... pipelines) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			LongBuffer lb = stack.mallocLong(pipelines.length);

			long[] layouts = new long[pipelines.length];

			VkGraphicsPipelineCreateInfo.Buffer buf = VkGraphicsPipelineCreateInfo.calloc(pipelines.length, stack);
			for (int i = 0; i < pipelines.length; i++) {
				VkGraphicsPipelineCreateInfo info = pipelines[i].fillInfo(device, stack);
				buf.put(i, info);
				layouts[i] = info.layout();
			}

			int err;
			err = VK14.vkCreateGraphicsPipelines(device.device(),0, buf, null, lb);
			if (err != VK14.VK_SUCCESS)
				throw new RuntimeException("Failed to create graphics pipeline: " + VkUtils.translateErrorCode(err));

			PipelineBuildInfo[] arr = new PipelineBuildInfo[pipelines.length];
			for (int i = 0; i < pipelines.length; i++) {
				arr[i] = new PipelineBuildInfo(lb.get(i), layouts[i]);
			}

			return arr;
		}
	}

	public PipelineBuildInfo build(VkDeviceContext device) {
		return build(device, this)[0];
	}

	public PNextChainable getpNext() {
		return pNext;
	}

	public VkGraphicsPipelineBuilder setpNext(PNextChainable pNext) {
		this.pNext = pNext;
		return this;
	}

	public VkPipelineCreateFlags getFlags() {
		return flags;
	}

	public VkGraphicsPipelineBuilder setFlags(VkPipelineCreateFlags flags) {
		this.flags = flags;
		return this;
	}

	public List<VkPipelineShaderStageBuilder> getStages() {
		return stages;
	}

	public VkGraphicsPipelineBuilder setStages(List<VkPipelineShaderStageBuilder> stages) {
		this.stages = stages;
		
		return this;
	}

	public VkPipelineVertexInputStateBuilder getVertexInputState() {
		return vertexInputState;
	}

	public VkGraphicsPipelineBuilder setVertexInputState(VkPipelineVertexInputStateBuilder vertexInputState) {
		this.vertexInputState = vertexInputState;
		return this;
	}

	public VkPipelineInputAssemblyStateBuilder getInputAssemblyState() {
		return inputAssemblyState;
	}

	public VkGraphicsPipelineBuilder setInputAssemblyState(VkPipelineInputAssemblyStateBuilder inputAssemblyState) {
		this.inputAssemblyState = inputAssemblyState;
		return this;
	}

	public VkPipelineTessellationStateBuilder getTessellationState() {
		return tessellationState;
	}

	public VkGraphicsPipelineBuilder setTessellationState(VkPipelineTessellationStateBuilder tessellationState) {
		this.tessellationState = tessellationState;
		return this;
	}

	public VkPipelineViewportStateBuilder getViewportState() {
		return viewportState;
	}

	public VkGraphicsPipelineBuilder setViewportState(VkPipelineViewportStateBuilder viewportState) {
		this.viewportState = viewportState;
		return this;
	}

	public VkPipelineRasterizationStateBuilder getRasterizationState() {
		return rasterizationState;
	}

	public VkGraphicsPipelineBuilder setRasterizationState(VkPipelineRasterizationStateBuilder rasterizationState) {
		this.rasterizationState = rasterizationState;
		return this;
	}

	public VkPipelineMultisampleStateBuilder getMultisampleState() {
		return multisampleState;
	}

	public VkGraphicsPipelineBuilder setMultisampleState(VkPipelineMultisampleStateBuilder multisampleState) {
		this.multisampleState = multisampleState;
		return this;
	}

	public VkPipelineDepthStencilStateBuilder getDepthStencilState() {
		return depthStencilState;
	}

	public VkGraphicsPipelineBuilder setDepthStencilState(VkPipelineDepthStencilStateBuilder depthStencilState) {
		this.depthStencilState = depthStencilState;
		return this;
	}

	public VkPipelineColorBlendStateBuilder getColorBlendState() {
		return colorBlendState;
	}

	public VkGraphicsPipelineBuilder setColorBlendState(VkPipelineColorBlendStateBuilder colorBlendState) {
		this.colorBlendState = colorBlendState;
		return this;
	}

	public VkPipelineDynamicStateBuilder getDynamicState() {
		return dynamicState;
	}

	public VkGraphicsPipelineBuilder setDynamicState(VkPipelineDynamicStateBuilder dynamicState) {
		this.dynamicState = dynamicState;
		return this;
	}

	public VkPipelineLayoutCreateInfoBuilder getPipelineLayout() {
		return pipelineLayout;
	}

	public VkGraphicsPipelineBuilder setPipelineLayout(VkPipelineLayoutCreateInfoBuilder pipelineLayout) {
		this.pipelineLayout = pipelineLayout;
		return this;
	}


	@Override
	public VkGraphicsPipelineBuilder copy() {
		return new VkGraphicsPipelineBuilder()
				.setpNext(Copyable.safeCopy(pNext))
				.setStages(Copyable.copyList(stages))
				.setVertexInputState(Copyable.safeCopy(vertexInputState))
				.setInputAssemblyState(Copyable.safeCopy(inputAssemblyState))
				.setTessellationState(Copyable.safeCopy(tessellationState))
				.setViewportState(Copyable.safeCopy(viewportState))
				.setRasterizationState(Copyable.safeCopy(rasterizationState))
				.setMultisampleState(Copyable.safeCopy(multisampleState))
				.setDepthStencilState(Copyable.safeCopy(depthStencilState))
				.setColorBlendState(Copyable.safeCopy(colorBlendState))
				.setDynamicState(Copyable.safeCopy(dynamicState))
				.setPipelineLayout(Copyable.safeCopy(pipelineLayout));
	}
}
