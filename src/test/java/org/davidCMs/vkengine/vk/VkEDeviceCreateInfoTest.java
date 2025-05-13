package org.davidCMs.vkengine.vk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class VkEDeviceCreateInfoTest {

	@Test
	void getQueueCreateInfosCloseCheck() {
		VkEDeviceCreateInfo info = new VkEDeviceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, info::getQueueCreateInfos);
	}

	@Test
	void getEnabledExtensionsCloseCheck() {
		VkEDeviceCreateInfo info = new VkEDeviceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, info::getEnabledExtensions);
	}

	@Test
	void getInfoCloseCheck() {
		VkEDeviceCreateInfo info = new VkEDeviceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, info::getInfo);
	}

	@Test
	void setQueueCreateInfosCloseCheck() {
		VkEDeviceCreateInfo info = new VkEDeviceCreateInfo();
		info.close();

		VkEQueueFamily family = VkEDeviceQueueCreateInfoTest.getFamily();

		Assertions.assertThrows(ClosedResourceException.class,
				() -> info.setQueueCreateInfos(Set.of(family.makeCreateInfo(1))));
	}

	@Test
	void setEnabledExtensionsCloseCheck() {
		VkEDeviceCreateInfo info = new VkEDeviceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class,
				() -> info.setEnabledExtensions(Set.of("A")));
	}

	@Test
	void queueCreateInfosSetGetTest() {
		VkEQueueFamily family = VkEDeviceQueueCreateInfoTest.getFamily();

		Set<VkEDeviceQueueCreateInfo> set = Set.of(family.makeCreateInfo(1));

		VkEDeviceCreateInfo info = new VkEDeviceCreateInfo()
				.setQueueCreateInfos(set);

		Set<VkEDeviceQueueCreateInfo> gottenSet = info.getQueueCreateInfos();

		Assertions.assertEquals(set, gottenSet);
	}

	@Test
	void extensionsSetGetTest() {
		Set<String> set = Set.of("TEST");

		VkEDeviceCreateInfo info = new VkEDeviceCreateInfo()
				.setEnabledExtensions(set);

		Set<String> gottenSet = info.getEnabledExtensions();

		Assertions.assertEquals(set, gottenSet);
	}

}