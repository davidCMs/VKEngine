package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.BuilderList;
import dev.davidCMs.vkengine.util.Copyable;
import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPushConstantRange;

import java.nio.LongBuffer;
import java.util.List;

public class VkPipelineLayoutCreateInfoBuilder implements Copyable {

    private final BuilderList<VkPipelineLayoutCreateInfoBuilder, VkDescriptorSetLayout> setLayouts = new BuilderList<>(this);
    private final BuilderList<VkPipelineLayoutCreateInfoBuilder, VkPushConstantRangeBuilder> pushConstantRanges = new BuilderList<>(this);

	private LongBuffer getSetLayoutsBuffer(VkDeviceContext device, MemoryStack stack) {
		LongBuffer buf = stack.mallocLong(setLayouts.size());
		for (int i = 0; i < setLayouts.size(); i++) {
			buf.put(i, setLayouts.get(i).layout());
		}
		return buf;
	}

	private VkPushConstantRange.Buffer getPushConstantRangesBuffer(MemoryStack stack) {
		VkPushConstantRange.Buffer buf = VkPushConstantRange.calloc(pushConstantRanges.size(), stack);
		for (int i = 0; i < pushConstantRanges.size(); i++) {
			buf.put(i, pushConstantRanges.get(i).build(stack));
		}
		return buf;
	}

	public long build(VkDeviceContext device, MemoryStack stack) {
		VkPipelineLayoutCreateInfo info = VkPipelineLayoutCreateInfo.calloc(stack);
		info.sType$Default();
		if (!setLayouts.isEmpty())
			info.pSetLayouts(getSetLayoutsBuffer(device, stack));
		if (!pushConstantRanges.isEmpty())
			info.pPushConstantRanges(getPushConstantRangesBuffer(stack));

		LongBuffer lb = stack.mallocLong(1);

		int err;
		err = VK14.vkCreatePipelineLayout(device.device(), info, null , lb);
		if (err != VK14.VK_SUCCESS)
			throw new IllegalStateException("Failed to create VkPipelineLayout: " + VkUtils.translateErrorCode(err));

		return lb.get(0);
	}

    public BuilderList<VkPipelineLayoutCreateInfoBuilder, VkDescriptorSetLayout> setLayouts() {
        return setLayouts;
    }

    public BuilderList<VkPipelineLayoutCreateInfoBuilder, VkPushConstantRangeBuilder> pushConstantRanges() {
        return pushConstantRanges;
    }

    @Override
	public VkPipelineLayoutCreateInfoBuilder copy() {
		return new VkPipelineLayoutCreateInfoBuilder()
				.setLayouts().add(setLayouts.getList()).ret()
				.pushConstantRanges().add(pushConstantRanges.getList()).ret();
	}
}
