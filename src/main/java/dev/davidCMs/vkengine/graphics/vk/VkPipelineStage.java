package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK14.*;

public enum VkPipelineStage {

	ALL_COMMANDS(VK_PIPELINE_STAGE_2_ALL_COMMANDS_BIT),
	ALL_GRAPHICS(VK_PIPELINE_STAGE_2_ALL_GRAPHICS_BIT),
	ALL_TRANSFER(VK_PIPELINE_STAGE_2_ALL_TRANSFER_BIT),
	BLIT(VK_PIPELINE_STAGE_2_BLIT_BIT),
	CLEAR(VK_PIPELINE_STAGE_2_CLEAR_BIT),
	BOTTOM_OF_PIPE(VK_PIPELINE_STAGE_2_BOTTOM_OF_PIPE_BIT),
	COLOR_ATTACHMENT_OUTPUT(VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT),
	COMPUTE_SHADER(VK_PIPELINE_STAGE_2_COMPUTE_SHADER_BIT),
	COPY(VK_PIPELINE_STAGE_2_COPY_BIT),
	DRAW_INDIRECT(VK_PIPELINE_STAGE_2_DRAW_INDIRECT_BIT),
	EARLY_FRAGMENT_TESTS(VK_PIPELINE_STAGE_2_EARLY_FRAGMENT_TESTS_BIT),
	FRAGMENT_SHADER(VK_PIPELINE_STAGE_2_FRAGMENT_SHADER_BIT),
	GEOMETRY_SHADER(VK_PIPELINE_STAGE_2_GEOMETRY_SHADER_BIT),
	HOST(VK_PIPELINE_STAGE_2_HOST_BIT),
	INDEX_INPUT(VK_PIPELINE_STAGE_2_INDEX_INPUT_BIT),
	LATE_FRAGMENT_TESTS(VK_PIPELINE_STAGE_2_LATE_FRAGMENT_TESTS_BIT),
	NONE(VK_PIPELINE_STAGE_2_NONE),
	PRE_RASTERIZATION_SHADERS(VK_PIPELINE_STAGE_2_PRE_RASTERIZATION_SHADERS_BIT),
	RESOLVE(VK_PIPELINE_STAGE_2_RESOLVE_BIT),
	TESSELLATION_CONTROL_SHADER(VK_PIPELINE_STAGE_2_TESSELLATION_CONTROL_SHADER_BIT),
	TESSELLATION_EVALUATION_SHADER(VK_PIPELINE_STAGE_2_TESSELLATION_EVALUATION_SHADER_BIT),
	TOP_OF_PIPE(VK_PIPELINE_STAGE_2_TOP_OF_PIPE_BIT),
	VERTEX_ATTRIBUTE_INPUT(VK_PIPELINE_STAGE_2_VERTEX_ATTRIBUTE_INPUT_BIT),
	VERTEX_INPUT(VK_PIPELINE_STAGE_2_VERTEX_INPUT_BIT),
	VERTEX_SHADER(VK_PIPELINE_STAGE_2_VERTEX_SHADER_BIT),

	;

	final long bit;

	VkPipelineStage(long bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkPipelineStage... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkPipelineStage bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkPipelineStage> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkPipelineStage bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkPipelineStage bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkPipelineStage> maskAsSet(long mask) {
	    Set<VkPipelineStage> set = new HashSet<>();
	    for (VkPipelineStage bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkPipelineStage valueOf(long bitVal) {
	    for (VkPipelineStage bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}
}
