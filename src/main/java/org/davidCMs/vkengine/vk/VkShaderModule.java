package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.shader.ShaderStage;
import org.davidCMs.vkengine.util.BufUtils;
import org.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class VkShaderModule {

	private final VkDeviceContext device;
	private final ShaderStage stage;

	private final long shaderModule;

	public VkShaderModule(VkDeviceContext device, ByteBuffer binBuf, ShaderStage stage) {
		this.device = device;
		this.stage = stage;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkShaderModuleCreateInfo info = VkShaderModuleCreateInfo.calloc(stack);
			info.sType$Default();
			info.pCode(binBuf);

			LongBuffer lb = stack.callocLong(1);
			int err = VK14.vkCreateShaderModule(device.device(), info, null, lb);
			if (err != VK14.VK_SUCCESS)
				throw new RuntimeException("Failed to create the ShaderModule" + VkUtils.translateErrorCode(err));

			shaderModule = lb.get(0);
		}
	}

	public void destroy() {
		VK14.vkDestroyShaderModule(device.device(), shaderModule, null);
	}

}
