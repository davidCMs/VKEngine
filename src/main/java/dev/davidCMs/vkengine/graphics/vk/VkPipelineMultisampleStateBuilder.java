package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineMultisampleStateCreateInfo;

public class VkPipelineMultisampleStateBuilder implements Copyable {

	private boolean sampleShadingEnable = false;
	private VkSampleCount rasterizationSamples = VkSampleCount.SAMPLE_1;
	private float minSampleShading = 1.0f;
	//private int sampleMask = 0;
	private boolean alphaToCoverageEnable = false;
	private boolean alphaToOneEnable = false;

	public VkPipelineMultisampleStateCreateInfo build(MemoryStack stack) {
		VkPipelineMultisampleStateCreateInfo info = VkPipelineMultisampleStateCreateInfo.calloc(stack);
		info.sType$Default();
		info.sampleShadingEnable(sampleShadingEnable);
		info.rasterizationSamples(rasterizationSamples.bit);
		info.minSampleShading(minSampleShading);
		//info.pSampleMask(sampleMask);
		info.alphaToCoverageEnable(alphaToCoverageEnable);
		info.alphaToOneEnable(alphaToOneEnable);
		return info;
	}

	public boolean isAlphaToOneEnable() {
		return alphaToOneEnable;
	}

	public VkPipelineMultisampleStateBuilder setAlphaToOneEnable(boolean alphaToOneEnable) {
		this.alphaToOneEnable = alphaToOneEnable;
		return this;
	}

	public boolean isAlphaToCoverageEnable() {
		return alphaToCoverageEnable;
	}

	public VkPipelineMultisampleStateBuilder setAlphaToCoverageEnable(boolean alphaToCoverageEnable) {
		this.alphaToCoverageEnable = alphaToCoverageEnable;
		return this;
	}

	public float getMinSampleShading() {
		return minSampleShading;
	}

	public VkPipelineMultisampleStateBuilder setMinSampleShading(float minSampleShading) {
		this.minSampleShading = minSampleShading;
		return this;
	}

	public VkSampleCount getRasterizationSamples() {
		return rasterizationSamples;
	}

	public VkPipelineMultisampleStateBuilder setRasterizationSamples(VkSampleCount rasterizationSamples) {
		this.rasterizationSamples = rasterizationSamples;
		return this;
	}

	public boolean isSampleShadingEnable() {
		return sampleShadingEnable;
	}

	public VkPipelineMultisampleStateBuilder setSampleShadingEnable(boolean sampleShadingEnable) {
		this.sampleShadingEnable = sampleShadingEnable;
		return this;
	}


	public VkPipelineMultisampleStateBuilder copy() {
		return new VkPipelineMultisampleStateBuilder()
				.setSampleShadingEnable(sampleShadingEnable)
				.setRasterizationSamples(rasterizationSamples)
				.setMinSampleShading(minSampleShading)
				.setAlphaToCoverageEnable(alphaToCoverageEnable)
				.setAlphaToOneEnable(alphaToOneEnable);
	}
}
