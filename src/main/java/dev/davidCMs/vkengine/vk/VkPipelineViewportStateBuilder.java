package dev.davidCMs.vkengine.vk;

import dev.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo;

import java.util.List;

public class VkPipelineViewportStateBuilder implements Copyable {

	private List<VkViewport> viewports;
	private int viewportCount;
	private List<VkRect2D> scissors;
	private int scissorsCount;

	private org.lwjgl.vulkan.VkViewport.Buffer getViewportBuffer(MemoryStack stack) {
		org.lwjgl.vulkan.VkViewport.Buffer buf = org.lwjgl.vulkan.VkViewport.calloc(viewports.size(), stack);
		for (int i = 0; i < viewports.size(); i++) {
			buf.put(i, viewports.get(i).toNative(stack));
		}
		return buf;
	}

	private org.lwjgl.vulkan.VkRect2D.Buffer getScissorBuffer(MemoryStack stack) {
		org.lwjgl.vulkan.VkRect2D.Buffer buf = org.lwjgl.vulkan.VkRect2D.calloc(scissors.size(), stack);
		for (int i = 0; i < scissors.size(); i++) {
			buf.put(i, scissors.get(i).toNative(stack));
		}
		return buf;
	}

	public VkPipelineViewportStateCreateInfo build(MemoryStack stack) {
		VkPipelineViewportStateCreateInfo info = VkPipelineViewportStateCreateInfo.calloc(stack);
		info.sType$Default();

		if (viewports != null && !viewports.isEmpty()) {
			if (viewportCount != 0 && viewportCount != viewports.size())
				throw new IllegalStateException("viewports.size() does not match viewportCount");
			info.viewportCount(viewports.size());
			info.pViewports(getViewportBuffer(stack));
		} else
			info.viewportCount(viewportCount);

		if (scissors != null && !scissors.isEmpty()) {
			if (scissorsCount != 0 && scissorsCount != scissors.size())
				throw new IllegalStateException("scissors.size() does not match scissorsCount");
			info.scissorCount(scissors.size());
			info.pScissors(getScissorBuffer(stack));
		} else
			info.scissorCount(scissorsCount);

		return info;
	}

	public List<VkViewport> getViewports() {
		return viewports;
	}

	public VkPipelineViewportStateBuilder setViewports(List<VkViewport> viewports) {
		this.viewports = viewports;
		return this;
	}

	public int getViewportCount() {
		return viewportCount;
	}

	public VkPipelineViewportStateBuilder setViewportCount(int viewportCount) {
		this.viewportCount = viewportCount;
		return this;
	}

	public List<VkRect2D> getScissors() {
		return scissors;
	}

	public VkPipelineViewportStateBuilder setScissors(List<VkRect2D> scissors) {
		this.scissors = scissors;
		return this;
	}

	public int getScissorsCount() {
		return scissorsCount;
	}

	public VkPipelineViewportStateBuilder setScissorsCount(int scissorsCount) {
		this.scissorsCount = scissorsCount;
		return this;
	}

	public VkPipelineViewportStateBuilder copy() {
		return new VkPipelineViewportStateBuilder()
				.setViewports(Copyable.copyList(viewports))
				.setViewportCount(viewportCount)
				.setScissors(Copyable.copyList(scissors))
				.setScissorsCount(scissorsCount);
	}
}
