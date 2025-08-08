package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.shader.ShaderStage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

	public VkCommandBuffer pushConstants(VkPipelineContext pipeline, ShaderStage[] stages, int offset, ByteBuffer data) {
		if (data.order() != ByteOrder.LITTLE_ENDIAN)
			throw new RuntimeException("data byte order must be little endian");
		if (data.remaining() == 0)
			throw new RuntimeException("data has 0 remaining, forgot .flip() perhaps?");

		VK14.vkCmdPushConstants(
				commandBuffer,
				pipeline.getPipelineLayout(),
				ShaderStage.getVkMaskOf(stages),
				offset,
				data
		);

		return this;
	}

	public VkCommandBuffer bindVertexBuffer(VkBuffer buffer) {
		return bindVertexBuffers(new VkBuffer[]{buffer}, 0, new long[]{0});
	}

	public VkCommandBuffer bindVertexBuffers(@NotNull VkBuffer[] buffers, int firstBinding, @NotNull long[] offsets) {
		return bindVertexBuffers(buffers, firstBinding, offsets, null, null);
	}

	public VkCommandBuffer bindVertexBuffers(@NotNull VkBuffer[] buffers, int firstBinding, @NotNull long[] offsets, @Nullable long[] sizes, @Nullable long[] strides) {

		if (buffers.length != offsets.length)
			throw new IllegalArgumentException("buffer.length and offsets.length do not match.");
		if (sizes != null)
			if (buffers.length != sizes.length)
				throw new IllegalArgumentException("buffer.length and sizes.length do not match");
		if (strides != null)
			if (buffers.length != strides.length)
				throw new IllegalArgumentException("buffer.length and strides.length do not match");

		long[] longBuffers = new long[buffers.length];
		for (int i = 0; i < buffers.length; i++) {
			longBuffers[i] = buffers[i].getBuffer();
		}

		VK14.vkCmdBindVertexBuffers2(
				commandBuffer,
				firstBinding,
				longBuffers,
				offsets,
				sizes,
				strides
		);
		return this;
	}

	public record VkBufferCopyRegion(long srcOffset, long dstOffset, long size) {

		public VkBufferCopy2 toNative(MemoryStack stack) {
			VkBufferCopy2 info = VkBufferCopy2.calloc(stack);
			info.sType$Default()
					.srcOffset(srcOffset)
					.dstOffset(dstOffset)
					.size(size);
			return info;
		}

		public static VkBufferCopy2.Buffer toNative(MemoryStack stack, Set<VkBufferCopyRegion> regions) {
			VkBufferCopy2.Buffer buf = VkBufferCopy2.calloc(regions.size(), stack);
			int i = 0;
			for (VkBufferCopyRegion region : regions) {
				buf.put(i, region.toNative(stack));
				i++;
			}
			return buf;
		}

		public static VkBufferCopy2.Buffer toNative(MemoryStack stack, VkBufferCopyRegion... regions) {
			VkBufferCopy2.Buffer buf = VkBufferCopy2.calloc(regions.length, stack);
			for (int i = 0; i < regions.length; i++) {
				buf.put(i, regions[i].toNative(stack));
			}
			return buf;
		}
	}

	public VkCommandBuffer copyBuffer(VkBuffer src, VkBuffer dst) {
		if (dst.getSize() != src.getSize())
			throw new IllegalArgumentException("src size and dst size do not match use the overload of this method that allows you to provide regions instead");
		return copyBuffer(src, dst, Set.of(new VkBufferCopyRegion(0,0, src.getSize())));
	}

	public VkCommandBuffer copyBuffer(VkBuffer src, VkBuffer dst, Set<VkBufferCopyRegion> regions) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkCopyBufferInfo2 info = VkCopyBufferInfo2.calloc(stack);
			info.sType$Default();
			info.srcBuffer(src.getBuffer());
			info.dstBuffer(dst.getBuffer());
			info.pRegions(VkBufferCopyRegion.toNative(stack, regions));

			VK14.vkCmdCopyBuffer2(commandBuffer, info);
		}
		return this;
	}

	org.lwjgl.vulkan.VkCommandBuffer getCommandBuffer() {
		return commandBuffer;
	}
}
