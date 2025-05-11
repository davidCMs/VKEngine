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
		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo(1);
		deviceQueueCreateInfo.close();

		Assertions.assertThrows(ClosedResourceException.class, deviceQueueCreateInfo::getInfo);
	}

	@Test
	void getPriorityCloseCheck() {
		VkEQueueFamily family = getFamily();
		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo(1);
		deviceQueueCreateInfo.close();

		Assertions.assertThrows(ClosedResourceException.class, deviceQueueCreateInfo::getPriority);
	}

	@Test
	void getQueueFamilyIndexCloseCheck() {
		VkEQueueFamily family = getFamily();
		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo(1);
		deviceQueueCreateInfo.close();

		Assertions.assertThrows(ClosedResourceException.class, deviceQueueCreateInfo::getQueueFamilyIndex);
	}

	@Test
	void setPriorityCloseCheck() {
		VkEQueueFamily family = getFamily();
		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo(1);
		deviceQueueCreateInfo.close();

		Assertions.assertThrows(ClosedResourceException.class,
				() -> deviceQueueCreateInfo.setPriority(1));
	}

	@Test
	void constructorThrowsForPriorityAboveOne() {
		VkEQueueFamily family = getFamily();


		Assertions.assertThrows(IllegalArgumentException.class,
				() -> family.makeCreateInfo(1.01f));
	}

	@Test
	void constructorThrowsForPriorityBelowZero() {
		VkEQueueFamily family = getFamily();


		Assertions.assertThrows(IllegalArgumentException.class,
				() -> family.makeCreateInfo(-0.01f));
	}

	@Test
	void setterThrowsForPriorityAboveOne() {
		VkEQueueFamily family = getFamily();
		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo(1);

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> deviceQueueCreateInfo.setPriority(-.01f));
	}

	@Test
	void setterThrowsForPriorityBelowZero() {
		VkEQueueFamily family = getFamily();
		VkEDeviceQueueCreateInfo deviceQueueCreateInfo = family.makeCreateInfo(1);

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> deviceQueueCreateInfo.setPriority(-.01f));
	}

	@BeforeEach
	void setUp() {
		Configuration.DEBUG.set(true);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
		Configuration.DEBUG_STACK.set(true);
	}

	VkEQueueFamily getFamily() {
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