package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.BuilderList;
import dev.davidCMs.vkengine.common.BuilderSet;
import org.tinylog.TaggedLogger;
import dev.davidCMs.vkengine.util.Copyable;
import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

public class VkDescriptorSetLayoutBuilder implements Copyable {
    private final BuilderList<VkDescriptorSetLayoutBuilder, VkDescriptorSetLayoutBindingBuilder> bindings = new BuilderList<>(this);
    private final BuilderSet<VkDescriptorSetLayoutBuilder, VkDescriptorSetLayoutCreateFlags> flags = new BuilderSet<>(this);

	private VkDescriptorSetLayoutBinding.Buffer getBindingsBuffer(MemoryStack stack) {
		VkDescriptorSetLayoutBinding.Buffer buf = VkDescriptorSetLayoutBinding.calloc(bindings.size(), stack);
		for (int i = 0; i < bindings.size(); i++) {
			buf.put(i, bindings.get(i).build(stack));
		}
		return buf;
	}

	public VkDescriptorSetLayout build(VkDeviceContext device, MemoryStack stack) {
		VkDescriptorSetLayoutCreateInfo info = VkDescriptorSetLayoutCreateInfo.calloc(stack);
		info.sType$Default();
		info.flags((int) VkDescriptorSetLayoutCreateFlags.getMaskOf(flags.getSet()));
		info.pBindings(getBindingsBuffer(stack));
		LongBuffer lb = stack.mallocLong(1);
		int err;
		err = VK14.vkCreateDescriptorSetLayout(device.device(), info, null, lb);

		if (err != VK14.VK_SUCCESS)
			throw new IllegalStateException("Failed to create a DescriptorSetLayout: " + VkUtils.translateErrorCode(err));

		return new VkDescriptorSetLayout(lb.get(0), device);
	}

    public VkDescriptorSetLayout build(VkDeviceContext device) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            return build(device, stack);
        }
    }

    public BuilderList<VkDescriptorSetLayoutBuilder, VkDescriptorSetLayoutBindingBuilder> bindings() {
        return bindings;
    }

    public BuilderSet<VkDescriptorSetLayoutBuilder, VkDescriptorSetLayoutCreateFlags> flags() {
        return flags;
    }

    @Override
	public Copyable copy() {
		return new VkDescriptorSetLayoutBuilder()
				.flags.add(flags.getSet()).ret()
				.bindings.add(bindings.getList()).ret();
	}
}
