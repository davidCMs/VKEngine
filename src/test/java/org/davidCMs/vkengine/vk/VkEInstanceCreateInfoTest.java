package org.davidCMs.vkengine.vk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ExceptionUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VkEInstanceCreateInfoTest {

	@Test
	void testLayerGetSet() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();

		Set<String> layers = Set.of(VkELayerUtils.KHRONOS_VALIDATION_NAME);

		info.setEnabledLayerNames(layers);

		Assertions.assertEquals(layers, info.getEnabledLayerNames());
	}

	@Test
	void testExtensionGetSet() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();

		Set<String> extensions = Set.of(VkEExtensionUtils.EXT_DEBUG_UTILS_NAME);

		info.setEnabledExtensionNames(extensions);

		Assertions.assertEquals(extensions, info.getEnabledExtensionNames());
	}

	@Test
	void setApplicationCreateInfoCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.setApplicationCreateInfo(new VkEApplicationInfo());
		});
	}

	@Test
	void setEnabledLayerNamesCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.setEnabledLayerNames(VkELayerUtils.KHRONOS_VALIDATION_NAME);
		});
	}

	@Test
	void getEnabledLayerNamesCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.getEnabledLayerNames();
		});
	}

	@Test
	void setEnabledExtensionNamesCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.setEnabledExtensionNames(VkEExtensionUtils.EXT_DEBUG_UTILS_NAME);
		});
	}

	@Test
	void getEnabledExtensionNamesCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.getEnabledExtensionNames();
		});
	}

	@Test
	void setDebugMessageTypesCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.setDebugMessageTypes(VkEDebugMessageType.GENERAL);
		});
	}

	@Test
	void setDebugMessageSeverityCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.setDebugMessageSeverity(VkEDebugMessageSeverity.INFO);
		});
	}

	@Test
	void setMessengerCallbackCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.setMessengerCallback(null);
		});
	}

	@Test
	void getInfoCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.getInfo();
		});
	}

	@Test
	void getMessengerInfoCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.getMessengerInfo();
		});
	}

	@Test
	void getInternalMessengerCallbackCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.getInternalMessengerCallback();
		});
	}

	@Test
	void setInternalMessengerCallbackCloseCheck() {
		VkEInstanceCreateInfo info = new VkEInstanceCreateInfo();
		info.close();

		Assertions.assertThrows(ClosedResourceException.class, () -> {
			info.setInternalMessengerCallback(null);
		});
	}


}