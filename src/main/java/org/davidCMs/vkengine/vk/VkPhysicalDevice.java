package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkInstance;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

public class VkPhysicalDevice {

	private final org.lwjgl.vulkan.VkPhysicalDevice physicalDevice;
	private final VkInstanceContext instance;
	private final VkPhysicalDeviceInfo info;

	public VkPhysicalDevice(org.lwjgl.vulkan.VkPhysicalDevice physicalDevice, VkInstanceContext instance) {
		this.physicalDevice = physicalDevice;
		this.instance = instance;
		this.info = VkPhysicalDeviceInfo.getFrom(this);
	}

	public static Set<VkPhysicalDevice> getAvailablePhysicalDevices(VkInstanceContext instance) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			int[] devCount = new int[1];
			VK14.vkEnumeratePhysicalDevices(instance.instance(), devCount, null);
			PointerBuffer devBuf = stack.callocPointer(devCount[0]);
			VK14.vkEnumeratePhysicalDevices(instance.instance(), devCount, devBuf);

			if (devCount[0] < 1)
				throw new IllegalStateException("Unable to find any graphical device.");

			Set<VkPhysicalDevice> devices = new HashSet<>();

			for (int i = 0; i < devBuf.remaining(); i++) {
				devices.add(new VkPhysicalDevice(
						new org.lwjgl.vulkan.VkPhysicalDevice(devBuf.get(i), instance.instance()),
						instance
				));
			}
			return devices;
		}
	}

	public org.lwjgl.vulkan.VkPhysicalDevice getPhysicalDevice() {
		return physicalDevice;
	}

	public VkInstanceContext getInstance() {
		return instance;
	}

	public VkPhysicalDeviceInfo getInfo() {
		return info;
	}

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof VkPhysicalDevice that)) return false;

        return getPhysicalDevice().equals(that.getPhysicalDevice()) && getInstance().equals(that.getInstance()) && getInfo().equals(that.getInfo());
	}

	@Override
	public int hashCode() {
		int result = getPhysicalDevice().hashCode();
		result = 31 * result + getInstance().hashCode();
		return result;
	}
}
