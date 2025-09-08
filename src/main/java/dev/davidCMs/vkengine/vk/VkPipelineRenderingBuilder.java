package dev.davidCMs.vkengine.vk;

import dev.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineRenderingCreateInfo;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class VkPipelineRenderingBuilder extends PNextChainable {
	private int colorAttachmentCount;
	private List<VkImageFormat> colorAttachmentFormats;
	private VkImageFormat depthAttachmentFormat = VkImageFormat.UNDEFINED;
	private VkImageFormat stencilAttachmentFormat = VkImageFormat.UNDEFINED;

	private IntBuffer getColorAttachmentFormatsBuffer(MemoryStack stack) {
		IntBuffer ib = stack.callocInt(colorAttachmentFormats.size());
		for (int i = 0; i < colorAttachmentFormats.size(); i++) {
			ib.put(i, colorAttachmentFormats.get(i).bit);
		}
		return ib;
	}

	public VkPipelineRenderingCreateInfo build(MemoryStack stack) {
		VkPipelineRenderingCreateInfo info = VkPipelineRenderingCreateInfo.calloc(stack);
		info.colorAttachmentCount(colorAttachmentCount);
		info.pColorAttachmentFormats(getColorAttachmentFormatsBuffer(stack));
		info.depthAttachmentFormat(depthAttachmentFormat.bit);
		info.stencilAttachmentFormat(stencilAttachmentFormat.bit);
		info.sType$Default();
		info.pNext(getNextpNext(stack));
		return info;
	}

	@Override
	public PNextChainable copy() {
		return new VkPipelineRenderingBuilder()
				.setColorAttachmentCount(colorAttachmentCount)
				.setColorAttachmentFormats(
						colorAttachmentFormats != null ?
								new ArrayList<>(colorAttachmentFormats) : null)
				.setDepthAttachmentFormat(depthAttachmentFormat)
				.setStencilAttachmentFormat(stencilAttachmentFormat)
				.setpNext(Copyable.safeCopy(pNext));
	}

	@Override
	public long getpNext(MemoryStack stack) {
		return build(stack).address();
	}

	public int getColorAttachmentCount() {
		return colorAttachmentCount;
	}

	public VkPipelineRenderingBuilder setColorAttachmentCount(int colorAttachmentCount) {
		this.colorAttachmentCount = colorAttachmentCount;
		return this;
	}

	public List<VkImageFormat> getColorAttachmentFormats() {
		return colorAttachmentFormats;
	}

	public VkPipelineRenderingBuilder setColorAttachmentFormats(List<VkImageFormat> colorAttachmentFormats) {
		this.colorAttachmentFormats = colorAttachmentFormats;
		return this;
	}

	public VkImageFormat getDepthAttachmentFormat() {
		return depthAttachmentFormat;
	}

	public VkPipelineRenderingBuilder setDepthAttachmentFormat(VkImageFormat depthAttachmentFormat) {
		this.depthAttachmentFormat = depthAttachmentFormat;
		return this;
	}

	public VkImageFormat getStencilAttachmentFormat() {
		return stencilAttachmentFormat;
	}

	public VkPipelineRenderingBuilder setStencilAttachmentFormat(VkImageFormat stencilAttachmentFormat) {
		this.stencilAttachmentFormat = stencilAttachmentFormat;
		return this;
	}
}
