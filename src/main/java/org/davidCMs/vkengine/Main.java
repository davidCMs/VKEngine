package org.davidCMs.vkengine;

import org.davidCMs.vkengine.vk.*;
import org.davidCMs.vkengine.vk.VkEApplicationInfo;
import org.davidCMs.vkengine.vk.VkEInstance;
import org.davidCMs.vkengine.vk.VkEInstanceCreateInfo;
import org.davidCMs.vkengine.window.GLFWWindow;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.util.Arrays;
import java.util.Set;
import java.util.Stack;

public class Main {

	static GLFWErrorCallback errorCallback;
	static GLFWWindow window;

	static VkEInstance instance;
	static VkDebugUtilsMessengerCallbackEXT messengerCallback;

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
				.setApplicationVersion(new VkEVersion(0, 0, 1))
				.setEngineName("VKEngine")
				.setEngineVersion(new VkEVersion(0, 0, 1));

		VkEInstanceCreateInfo instanceInfo = new VkEInstanceCreateInfo();
		instanceInfo.setApplicationCreateInfo(applicationInfo);
		instanceInfo.setEnabledLayerNames(VkELayerUtils.KHRONOS_VALIDATION_NAME);

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

		String[] extToEnableSel = new String[] {
				VkEExtensionUtils.EXT_DEBUG_UTILS_NAME
		};
		Set<String> reqExt = VkEExtensionUtils.getRequiredVkExtensions();
		reqExt.addAll(Arrays.asList(extToEnableSel));

		instanceInfo.setEnabledExtensionsNames(reqExt.toArray(new String[0]));

		Set<String> availableLayers = VkELayerUtils.getAvailableLayers();
		System.out.println("Available layers: ");
		availableLayers.forEach(System.out::println);
		System.out.println();

		Set<String> enabledLayers = instanceInfo.getEnabledLayerNames();
		System.out.println("Enabled Layers: ");
		enabledLayers.forEach(System.out::println);
		System.out.println();

		Set<String> availableExtensions = VkEExtensionUtils.getAvailableExtension();
		System.out.println("Available Extensions: ");
		availableExtensions.forEach(System.out::println);
		System.out.println();

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