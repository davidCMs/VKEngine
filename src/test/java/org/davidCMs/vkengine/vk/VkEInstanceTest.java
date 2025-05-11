package org.davidCMs.vkengine.vk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.Configuration;

import static org.junit.jupiter.api.Assertions.*;

class VkEInstanceTest {

	@Test
	void getInstanceCloseCheck() {
		VkEInstanceCreateInfo info = getCreationInfo();
		VkEInstance instance = new VkEInstance(info);
		instance.close();

		Assertions.assertThrows(ClosedResourceException.class,
				instance::getInstance);
	}

	@Test
	void setMessengerCallbackCloseCheck() {
		VkEInstanceCreateInfo info = getCreationInfo();
		VkEInstance instance = new VkEInstance(info);
		instance.close();

		Assertions.assertThrows(ClosedResourceException.class,
				() -> {
					instance.setMessengerCallback(null);
				});
	}

	@Test
	void transferOfMessenger() {
		VkEInstanceCreateInfo info = getCreationInfo();

		VkEInternalDebugMessengerCallback callback = info.getInternalMessengerCallback();

		VkEInstance instance = new VkEInstance(info);

		assertSame(instance.getInternalMessengerCallback(), callback);
	}

	static VkEInstanceCreateInfo getCreationInfo() {
		return new VkEInstanceCreateInfo()
				.setApplicationCreateInfo(new VkEApplicationInfo()
						.setApplicationName("Test")
						.setEngineName("Test")
						.setApplicationVersion(
								new VkEVersion(
										1,0, 0, 1
								)
						)
						.setEngineVersion(
								new VkEVersion(
										1, 0, 0,1
								)
						)
				)
				.setDebugMessageSeverity(
						VkEDebugMessageSeverity.VERBOSE,
						VkEDebugMessageSeverity.WARNING,
						VkEDebugMessageSeverity.ERROR

						//VkEDebugMessageSeverity.INFO
				)
				.setDebugMessageTypes(
						VkEDebugMessageType.GENERAL,
						VkEDebugMessageType.PERFORMANCE,
						VkEDebugMessageType.VALIDATION
				)
				.setEnabledExtensionNames(
						VkEExtensionUtils.EXT_DEBUG_UTILS_NAME
				)
				.setEnabledLayerNames(
						VkELayerUtils.KHRONOS_VALIDATION_NAME
				);
	}


	@BeforeEach
	void setUp() {
		Configuration.DEBUG.set(true);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
		Configuration.DEBUG_STACK.set(true);
	}

}