package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.shader.ShaderStage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;

public class VkCommandBuffer {

	private final VkCommandPool pool;
	private final org.lwjgl.vulkan.VkCommandBuffer commandBuffer;

	public VkCommandBuffer(VkCommandPool pool, long commandBuffer) {
		this.pool = pool;
		this.commandBuffer = new org.lwjgl.vulkan.VkCommandBuffer(commandBuffer, pool.getDevice().device());
	}

	public VkCommandBuffer begin(VkCommandBufferUsageFlags... flags) {
		return begin(VkCommandBufferUsageFlags.getMaskOf(flags));
	}

	public VkCommandBuffer begin(Set<VkCommandBufferUsageFlags> flags) {
		return begin(VkCommandBufferUsageFlags.getMaskOf(flags));
	}

	private VkCommandBuffer begin(int flags) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkCommandBufferBeginInfo info = VkCommandBufferBeginInfo.calloc(stack);
			info.sType$Default();
			info.flags(flags);

			VK14.vkBeginCommandBuffer(commandBuffer, info);
		}
		return this;
	}

	public VkCommandBuffer end() {
		VK14.vkEndCommandBuffer(commandBuffer);
		return this;
	}

	public VkCommandBuffer insertImageMemoryBarrier(VkImageMemoryBarrierBuilder barrier) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			VkImageMemoryBarrier2.Buffer buffer = VkImageMemoryBarrier2.calloc(1, stack);
			buffer.put(0, barrier.build(stack));

			VkDependencyInfo info = VkDependencyInfo.calloc(stack);
			info.sType$Default();
			info.pImageMemoryBarriers(buffer);

			VK14.vkCmdPipelineBarrier2(commandBuffer, info);
		}
		return this;
	}

	public VkCommandBuffer beginRendering(VkRenderingInfoBuilder renderingInfo) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VK14.vkCmdBeginRendering(commandBuffer, renderingInfo.build(stack));
		}
		return this;
	}

	public VkCommandBuffer bindPipeline(VkPipelineBindPoint bindPoint, VkPipelineContext pipeline) {
		VK14.vkCmdBindPipeline(commandBuffer, bindPoint.bit, pipeline.getPipeline());
		return this;
	}

	public VkCommandBuffer setViewport(VkViewport viewport) {
		return setViewport(0, viewport);
	}

	public VkCommandBuffer setViewport(int first, List<VkViewport> viewports) {
		return setViewport(first, viewports.toArray(new VkViewport[0]));
	}

	public VkCommandBuffer setViewport(int first, VkViewport... viewport) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			org.lwjgl.vulkan.VkViewport.Buffer buf = org.lwjgl.vulkan.VkViewport.calloc(viewport.length, stack);
			for (int i = 0; i < viewport.length; i++) {
				buf.put(i, viewport[i].toNative(stack));
			}

			VK14.vkCmdSetViewport(commandBuffer, first, buf);
		}
		return this;
	}

	public VkCommandBuffer setScissor(VkRect2D scissor) {
		return setScissor(0, scissor);
	}

	public VkCommandBuffer setScissor(int first, List<VkRect2D> scissor) {
		return setScissor(first, scissor.toArray(new VkRect2D[0]));
	}

	public VkCommandBuffer setScissor(int first, VkRect2D... scissor) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			org.lwjgl.vulkan.VkRect2D.Buffer buf = org.lwjgl.vulkan.VkRect2D.calloc(scissor.length, stack);
			for (int i = 0; i < scissor.length; i++) {
				buf.put(i, scissor[i].toNative(stack));
			}

			VK14.vkCmdSetScissor(commandBuffer, first, buf);
		}
		return this;
	}

	public VkCommandBuffer draw(int vertexCount, int instanceCount, int firstVertex, int firstInstance) {
		VK14.vkCmdDraw(commandBuffer, vertexCount, instanceCount, firstVertex, firstInstance);
		return this;
	}

	public VkCommandBuffer endRendering() {
		VK14.vkCmdEndRendering(commandBuffer);
		return this;
	}

	public VkCommandBuffer pushConstants(VkPipelineContext pipeline, ShaderStage[] stages, int offset, int data) {
		return pushConstants(pipeline, stages, offset, new int[]{data});
	}

	public VkCommandBuffer pushConstants(VkPipelineContext pipeline, ShaderStage[] stages, int offset, float data) {
		return pushConstants(pipeline, stages, offset, new float[]{data});
	}

	public VkCommandBuffer pushConstants(VkPipelineContext pipeline, ShaderStage[] stages, int offset, double data) {
		return pushConstants(pipeline, stages, offset, new double[]{data});
	}

	public VkCommandBuffer pushConstants(VkPipelineContext pipeline, ShaderStage[] stages, int offset, int[] data) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VK14.vkCmdPushConstants(
					commandBuffer,
					pipeline.getPipelineLayout(),
					ShaderStage.getVkMaskOf(stages),
					offset,
					stack.ints(data)
					);

		}
		return this;
	}

	public VkCommandBuffer pushConstants(VkPipelineContext pipeline, ShaderStage[] stages, int offset, float[] data) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VK14.vkCmdPushConstants(
					commandBuffer,
					pipeline.getPipelineLayout(),
					ShaderStage.getVkMaskOf(stages),
					offset,
					stack.floats(data)
			);

		}
		return this;
	}

	public VkCommandBuffer pushConstants(VkPipelineContext pipeline, ShaderStage[] stages, int offset, double[] data) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VK14.vkCmdPushConstants(
					commandBuffer,
					pipeline.getPipelineLayout(),
					ShaderStage.getVkMaskOf(stages),
					offset,
					stack.doubles(data)
			);

		}
		return this;
	}

	org.lwjgl.vulkan.VkCommandBuffer getCommandBuffer() {
		return commandBuffer;
	}
}
