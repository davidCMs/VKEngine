package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.vk.deviceinfo.VkEPhysicalDeviceInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.Configuration;
import org.lwjgl.vulkan.VkPhysicalDevice;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VkEDeviceQueueCreateInfoTest {

	@Test
	void getInfoCloseCheck() {
		VkEQueueFamily family = getFamily();
		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo();
		deviceQueueCreateInfo.close();

		Assertions.assertThrows(ClosedResourceException.class, deviceQueueCreateInfo::getInfo);
	}

	@Test
	void getFamilyCloseCheck() {
		VkEQueueFamily family = getFamily();
		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo();
		deviceQueueCreateInfo.close();

		Assertions.assertThrows(ClosedResourceException.class, deviceQueueCreateInfo::getFamily);
	}

	@Test
	void getPriorityCloseCheck() {
		VkEQueueFamily family = getFamily();
		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo();
		deviceQueueCreateInfo.close();

		Assertions.assertThrows(ClosedResourceException.class, deviceQueueCreateInfo::getPriorities);
	}

	@Test
	void setPriorityCloseCheck() {
		VkEQueueFamily family = getFamily();
		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo();
		deviceQueueCreateInfo.close();

		Assertions.assertThrows(ClosedResourceException.class,
				() -> deviceQueueCreateInfo.setPriorities(1));
	}

	@Test
	void setterThrowsForPriorityAboveOne() {
		VkEQueueFamily family = getFamily();

		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo();

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> deviceQueueCreateInfo.setPriorities(1.01f));
	}

	@Test
	void setterThrowsForPriorityBelowZero() {
		VkEQueueFamily family = getFamily();

		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo();

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> deviceQueueCreateInfo.setPriorities(-.01f));
	}

	@BeforeEach
	void setUp() {
		Configuration.DEBUG.set(true);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
		Configuration.DEBUG_STACK.set(true);
	}

	static VkEQueueFamily getFamily() {
		VkEInstance instance = new VkEInstance(VkEInstanceTest.getCreationInfo());

		VkPhysicalDevice device = VkEPhysicalDeviceUtils.getDevice(instance);

		VkEPhysicalDeviceInfo info = VkEPhysicalDeviceInfo.getFrom(device);

		Set<VkEQueueFamily> queueFamilySet = info.queueFamilies();

		VkEQueueFamily selectedFamily = null;
		for (VkEQueueFamily family : queueFamilySet) {
			if (selectedFamily == null) {
				selectedFamily = family;
				continue;
			}
			if (selectedFamily.getMaxQueues() < family.getMaxQueues())
				selectedFamily = family;
		}

		return selectedFamily;
	}

}