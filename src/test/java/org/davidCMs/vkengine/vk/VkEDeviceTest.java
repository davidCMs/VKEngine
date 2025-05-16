package org.davidCMs.vkengine.vk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lwjgl.vulkan.VkPhysicalDevice;

import java.util.HashSet;
import java.util.Set;

class VkEDeviceTest {

	@Test
	void acquiredQueueTest() {
		VkEInstance instance = new VkEInstance(VkEInstanceTest.getCreationInfo());
		VkPhysicalDevice physicalDevice = VkEPhysicalDeviceUtils.getDevice(instance);
		Set<VkEQueueFamily> familySet = VkEQueueFamily.getDeviceQueueFamilies(physicalDevice);

		int queuesCreated = 0;

		Set<VkEDeviceQueueCreateInfo> queueCreateInfos = new HashSet<>();
		for (VkEQueueFamily family : familySet) {
			VkEDeviceQueueCreateInfo createInfo = family.makeCreateInfo();
			float[] priorities = new float[family.getMaxQueues()];
			for (int i = 0; i < family.getMaxQueues(); i++) {
				priorities[i] = (float) i / family.getMaxQueues();
				queuesCreated++;
			}
			createInfo.setPriorities(priorities);
			queueCreateInfos.add(createInfo);
		}


		VkEDeviceCreateInfo deviceCreateInfo = new VkEDeviceCreateInfo()
				.setQueueCreateInfos(queueCreateInfos				);

		VkEDevice device = new VkEDevice(physicalDevice, deviceCreateInfo);

		int queuesGot = 0;

		for (VkEQueueFamily family : familySet) {
			for (int i = 0; i < family.getMaxQueues(); i++) {
				Assertions.assertNotNull(device.getQueue(family, i));
				queuesGot++;
			}
		}

		Assertions.assertEquals(queuesGot, queuesCreated);

	}

}