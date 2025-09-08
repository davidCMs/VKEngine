package dev.davidCMs.vkengine.vk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	List<VkDescriptorSetLayoutCreateFlags> flags;
	List<VkDescriptorSetLayoutBindingBuilder> bindings;

	private VkDescriptorSetLayoutBinding.Buffer getBindingsBuffer(MemoryStack stack) {
		VkDescriptorSetLayoutBinding.Buffer buf = VkDescriptorSetLayoutBinding.calloc(bindings.size(), stack);
		for (int i = 0; i < bindings.size(); i++) {
			buf.put(i, bindings.get(i).build(stack));
		}
		return buf;
	}

	public long build(VkDeviceContext device, MemoryStack stack) {
		VkDescriptorSetLayoutCreateInfo info = VkDescriptorSetLayoutCreateInfo.calloc(stack);
		info.sType$Default();
		info.flags(VkDescriptorSetLayoutCreateFlags.getMaskOf(flags));
		info.pBindings(getBindingsBuffer(stack));
		LongBuffer lb = stack.mallocLong(1);
		int err;
		err = VK14.vkCreateDescriptorSetLayout(device.device(), info, null, lb);

		if (err != VK14.VK_SUCCESS)
			throw new IllegalStateException("Failed to create a DescriptorSetLayout: " + VkUtils.translateErrorCode(err));

		return lb.get(0);
	}

	public List<VkDescriptorSetLayoutCreateFlags> getFlags() {
		return flags;
	}

	public VkDescriptorSetLayoutBuilder setFlags(List<VkDescriptorSetLayoutCreateFlags> flags) {
		this.flags = flags;
		return this;
	}

	public List<VkDescriptorSetLayoutBindingBuilder> bindings() {
		return bindings;
	}

	public VkDescriptorSetLayoutBuilder setBindings(List<VkDescriptorSetLayoutBindingBuilder> bindings) {
		this.bindings = bindings;
		return this;
	}

	@Override
	public Copyable copy() {
		return new VkDescriptorSetLayoutBuilder()
				.setFlags(flags != null ? new ArrayList<>(flags) : null)
				.setBindings(Copyable.copyList(bindings));
	}
}
