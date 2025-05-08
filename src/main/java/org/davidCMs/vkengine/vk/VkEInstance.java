package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.EXTDebugUtils;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkInstance;

import java.nio.LongBuffer;

public class VkEInstance extends AutoCloseableResource {

	private final org.lwjgl.vulkan.VkInstance instance;
	private  VkEInternalDebugMessengerCallback internalMessengerCallback;
	private  long messenger;

	public VkEInstance(VkEInstanceCreateInfo info) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			PointerBuffer buf = stack.callocPointer(1);
			int err = VK14.vkCreateInstance(
					info.getInfo(), null, buf);
			if (err != VK10.VK_SUCCESS)
				throw new VkEFailedToCreateInstanceException(
						"Failed to create instance error code: " + err);


			instance = new VkInstance(
					buf.get(0), info.getInfo());


			LongBuffer lbuf = stack.callocLong(1);
			err = EXTDebugUtils.vkCreateDebugUtilsMessengerEXT(
					instance, info.getMessengerInfo(), null, lbuf);
			if (err != VK10.VK_SUCCESS) {
				throw new IllegalStateException(
						"Failed to create a messenger");
			}


			internalMessengerCallback = info.getInternalMessengerCallback();
			info.setInternalMessengerCallback(null);
			messenger = lbuf.get(0);

			try {
				info.close();
			} catch (Exception e) {
				throw new RuntimeException("Could not close info");
			}
		}
	}
	public VkInstance getInstance() {
		check();
		return instance;
	}

	public void setMessengerCallback(
			VkEDebugMessengerCallback messengerCallback) {
		check();
		this.internalMessengerCallback.setCallback(messengerCallback);
	}

	VkEInternalDebugMessengerCallback getInternalMessengerCallback() {
		check();
		return internalMessengerCallback;
	}

	@Override
	public void close(){
		super.close();

		EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT(
				instance, messenger, null);
		VK14.vkDestroyInstance(instance, null);

		internalMessengerCallback.close();
	}
}
