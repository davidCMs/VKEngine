package org.davidCMs.vkengine;

import org.davidCMs.vkengine.vk.*;
import org.davidCMs.vkengine.vk.VkEApplicationInfo;
import org.davidCMs.vkengine.vk.VkEInstance;
import org.davidCMs.vkengine.vk.VkEInstanceCreateInfo;
import org.davidCMs.vkengine.vk.deviceinfo.VkEPhysicalDeviceInfo;
import org.davidCMs.vkengine.window.GLFWWindow;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Configuration;
import org.lwjgl.vulkan.*;

import java.util.Optional;
import java.util.Set;

public class Main {

	static GLFWErrorCallback errorCallback;
	static GLFWWindow window;

	static VkEInstance instance;
	static VkDebugUtilsMessengerCallbackEXT messengerCallback;

	static VkEPhysicalDeviceInfo physicalDeviceInfo;

	public static void main(String[] args) throws Exception {

		Configuration.DEBUG.set(true);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
		Configuration.DEBUG_STACK.set(true);

		GLFW.glfwInit();

		errorCallback = GLFWErrorCallback.createPrint(System.err).set();

		init();
	}

	public static void init() {
		try {
			initWindow();
			initVulkan();

			mainLoop();
		} finally {
			clean();
		}

	}

	public static void initWindow() {
		window = new GLFWWindow(800, 600, "VK Window");
		window.setVisible(true);
	}

	public static void initVulkan() {

		VkEApplicationInfo applicationInfo = new VkEApplicationInfo()
				.setApplicationName("Game")
				.setApplicationVersion(new VkEVersion(0,0, 0, 1))
				.setEngineName("VKEngine")
				.setEngineVersion(new VkEVersion(0,0, 0, 1));

		VkEInstanceCreateInfo instanceInfo = new VkEInstanceCreateInfo();
		instanceInfo.setApplicationCreateInfo(applicationInfo);
		instanceInfo.setEnabledLayerNames(Set.of(VkELayerUtils.KHRONOS_VALIDATION_NAME));

		Set<String> layers = instanceInfo.getEnabledLayerNames();
		layers.forEach(System.out::println);

		instanceInfo.setDebugMessageSeverity(
				VkEDebugMessageSeverity.ERROR,
				//VkEDebugMessageSeverity.INFO,
				VkEDebugMessageSeverity.WARNING,
				VkEDebugMessageSeverity.VERBOSE
		);
		instanceInfo.setDebugMessageTypes(
				VkEDebugMessageType.GENERAL,
				VkEDebugMessageType.PERFORMANCE,
				VkEDebugMessageType.VALIDATION
		);


		Set<String> reqExt = VkEExtensionUtils.getRequiredVkExtensions();
		reqExt.add(VkEExtensionUtils.EXT_DEBUG_UTILS_NAME);

		instanceInfo.setEnabledExtensionNames(reqExt);

		Set<String> requiredExtensions = VkEExtensionUtils.getRequiredVkExtensions();
		System.out.println("Required GLFW Vulkan extensions: ");
		requiredExtensions.forEach(System.out::println);
		System.out.println();

		Set<String> enabledExtensions = instanceInfo.getEnabledExtensionNames();
		System.out.println("Enabled Extensions: ");
		enabledExtensions.forEach(System.out::println);
		System.out.println();

		instance = new VkEInstance(instanceInfo);

		VkPhysicalDevice physicalDevice = VkEPhysicalDeviceUtils.getDevice(instance);

		if (!VkEPhysicalDeviceExtensionUtils.checkAvailabilityOf(physicalDevice, VkEPhysicalDeviceExtensionUtils.VK_KHR_SWAPCHAIN))
			throw new RuntimeException("Extensions not found");

		physicalDeviceInfo = VkEPhysicalDeviceInfo.getFrom(physicalDevice);

		Optional<VkEQueueFamily> queueFamilyOpt =
				physicalDeviceInfo.queueFamilies().stream()
						.filter(VkEQueueFamily::capableOfGraphics)
						.findAny();

		if (queueFamilyOpt.isEmpty()) {
			throw new RuntimeException("Failed to find a queue family capable of graphics");
		}

		VkEQueueFamily family = queueFamilyOpt.get();

		VkEDeviceQueueCreateInfo createInfo = family.makeCreateInfo();
		createInfo.setPriorities(1.0f);

		VkEDeviceCreateInfo deviceCreateInfo = new VkEDeviceCreateInfo();
		deviceCreateInfo.setQueueCreateInfos(Set.of(createInfo));
		deviceCreateInfo.setEnabledExtensions(Set.of(VkEPhysicalDeviceExtensionUtils.VK_KHR_SWAPCHAIN));

		VkEDevice device = new VkEDevice(physicalDevice, deviceCreateInfo);

		VkQueue graphicsQueue = device.getQueue(family, 0);

		try {
			applicationInfo.close();
			instanceInfo.close();
		} catch (Throwable ignored) {}
	}

	public static void mainLoop() {
		while (!window.shouldClose()) {
			GLFW.glfwPollEvents();
		}
	}

	public static void clean() {

		try {
			instance.close();
		} catch (Throwable ignored) {}

		window.close();
		errorCallback.close();
		GLFW.glfwTerminate();
	}

}