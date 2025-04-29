package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.EXTDebugUtils;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK14;

public class VkEInstance implements AutoCloseable {

	private final MemoryStack stack;
	private final org.lwjgl.vulkan.VkInstance instance;
	private final VkEInternalDebugMessengerCallback internalMessengerCallback;
	private VkEDebugMessengerCallback messengerCallback;
	private final long messenger;

	public VkEInstance(VkEInstanceCreateInfo info) {
		stack = MemoryStack.stackPush();

		PointerBuffer buffer = stack.callocPointer(1);

		int err = VK14.vkCreateInstance(info.getInfo(), null, buffer);

		if (err != VK10.VK_SUCCESS)
			throw new VkEFailedToCreateInstanceException("Failed to create instance error code: " + err);

		instance = new org.lwjgl.vulkan.VkInstance(buffer.get(0), info.getInfo());

		long[] pb = new long[1];

		if (EXTDebugUtils.vkCreateDebugUtilsMessengerEXT(instance, info.getMessengerInfo(), null, pb) != VK10.VK_SUCCESS) {
			throw new IllegalStateException("Failed to create a messenger");
		}

		internalMessengerCallback = info.getInternalMessengerCallback();
		info.setInternalMessengerCallback(null);
		messenger = pb[0];
	}

	public org.lwjgl.vulkan.VkInstance getInstance() {
		return instance;
	}

	public void setMessengerCallback(VkEDebugMessengerCallback messengerCallback) {
		this.messengerCallback = messengerCallback;
		this.internalMessengerCallback.setCallback(messengerCallback);
	}

	@Override
	public void close() throws Exception {
		internalMessengerCallback.close();
		EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT(instance, messenger, null);
		VK14.vkDestroyInstance(instance, null);
		stack.close();
	}
}
