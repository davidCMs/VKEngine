package org.davidCMs.vkengine;

import org.davidCMs.vkengine.vk.*;
import org.davidCMs.vkengine.vk.deviceinfo.VkEPhysicalDeviceInfo;
import org.davidCMs.vkengine.window.GLFWWindow;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Configuration;
import org.lwjgl.vulkan.*;

import java.util.HashSet;
import java.util.Set;

public class Main {

	static GLFWErrorCallback errorCallback;
	static GLFWWindow window;

	static VkInstance instance;
	static VkDebugUtilsMessengerCallbackEXT messengerCallback;

	static long surface;

	static VkPhysicalDevice physicalDevice = null;
	static VkEPhysicalDeviceInfo physicalDeviceInfo = null;

	static VkEQueueFamily graphicsFamily = null;
	static VkEQueueFamily presentFamily = null;

	static VkDevice device;
	static VkQueue graphicsQueue;
	static VkQueue presentQueue;

	public static void main(String[] args) throws Exception {

		Configuration.DEBUG.set(true);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
		Configuration.DEBUG_STACK.set(true);

		GLFW.glfwInit();

		errorCallback = GLFWErrorCallback.createPrint(System.err).set();

		init();
	}

	public static void init() {
		//try {
			initWindow();
			initVulkan();

			mainLoop();
		//} finally {
			clean();
		//}

	}

	public static void initWindow() {
		window = new GLFWWindow(800, 600, "VK Window");
		window.setVisible(true);
	}

	public static void initVulkan() {
		Set<String> reqExt = VkEExtensionUtils.getRequiredVkExtensions();
		reqExt.add(VkEExtensionUtils.EXT_DEBUG_UTILS_NAME);

		Set<String> requiredExtensions = VkEExtensionUtils.getRequiredVkExtensions();
		System.out.println("Required GLFW Vulkan extensions: ");
		requiredExtensions.add(VkEExtensionUtils.EXT_DEBUG_UTILS_NAME);
		requiredExtensions.forEach(System.out::println);
		System.out.println();

		instance = new VkInstanceBuilder()
				.setApplicationName("Game")
				.setApplicationVersion(new VkEVersion(1,0, 0, 1))
				.setEngineName("VKEngine")
				.setEngineVersion(new VkEVersion(1,0, 0, 1))
				.setDebugMessageSeverities(
						//VkEDebugMessageSeverity.INFO,
						VkEDebugMessageSeverity.VERBOSE,
						VkEDebugMessageSeverity.WARNING,
						VkEDebugMessageSeverity.ERROR
				)
				.setDebugMessageTypes(
						VkEDebugMessageType.GENERAL,
						VkEDebugMessageType.PERFORMANCE,
						VkEDebugMessageType.VALIDATION
				)
				.setEnabledExtensions(requiredExtensions)
				.setEnabledLayers(VkELayerUtils.KHRONOS_VALIDATION_NAME)
				.build();


		System.out.println("Created instance");

		surface = window.makeVkSurface(instance);
		System.out.println("Created surface");

		for (VkPhysicalDevice device : VkEPhysicalDeviceUtils.getAvailablePhysicalDevices(instance)) {
			VkEPhysicalDeviceInfo pdInfo = VkEPhysicalDeviceInfo.getFrom(device);
			System.out.println("Checking if device: \"" + pdInfo.properties().deviceName() + "\" is suitable");
			if (!VkEPhysicalDeviceExtensionUtils.checkAvailabilityOf(device, VkEPhysicalDeviceExtensionUtils.VK_KHR_SWAPCHAIN))
				continue;
			for (VkEQueueFamily family : pdInfo.queueFamilies()) {
				System.out.println("Checking queue family: " + family.getIndex());
				if (family.capableOfGraphics() && graphicsFamily == null) {
					System.out.println("Family: " + family.getIndex() + " can do graphics");
					graphicsFamily = family;
				}
				if (VkEPhysicalDeviceUtils.canRenderTo(device, family, surface) && presentFamily == null) {
					System.out.println("Family: " + family.getIndex() + " can do presentation");
					presentFamily = family;
				}
				if (graphicsFamily != null || presentFamily != null) {
					System.out.println("Exiting loop as family: " + family.getIndex() + " is capable of graphics and presentation");
					break;
				}
			}
			if (graphicsFamily == null || presentFamily == null) {
				System.out.println("Device: \""  + pdInfo.properties().deviceName() + "\" is not suitable");
				physicalDevice = null;
				physicalDeviceInfo = null;
				graphicsFamily = null;
				presentFamily = null;
			} else {
				System.out.println("Device: \""  + pdInfo.properties().deviceName() + "\" is suitable");
				physicalDevice = device;
				physicalDeviceInfo = pdInfo;
			}
		}
		if (physicalDevice == null)
			throw new RuntimeException("Could not find a suitable device");

		System.out.println("Found a suitable GPU: " + physicalDeviceInfo.properties().deviceName());
		System.out.println("Graphics queue family index is: " + graphicsFamily.getIndex());
		System.out.println("Present queue family index is: " + presentFamily.getIndex());

		Set<VkDeviceBuilderQueueInfo> queueInfos = new HashSet<>();

		queueInfos.add(graphicsFamily.makeCreateInfo());
		if (presentFamily != graphicsFamily) {
			queueInfos.add(presentFamily.makeCreateInfo());
		}

		VkDeviceBuilder deviceBuilder = new VkDeviceBuilder()
				.setPhysicalDevice(physicalDevice)
				.setExtensions(VkEPhysicalDeviceExtensionUtils.VK_KHR_SWAPCHAIN)
				.setQueueInfos(queueInfos);

		device = deviceBuilder.build();

		System.out.println("Created device and queues");
		graphicsQueue = deviceBuilder.getQueue(graphicsFamily, 0);
		presentQueue = deviceBuilder.getQueue(presentFamily, 0);

		if (device == null) throw new RuntimeException();

	}

	public static void mainLoop() {
		while (!window.shouldClose()) {
			GLFW.glfwPollEvents();
		}
	}

	public static void clean() {

		VK14.vkDestroyDevice(device, null);
		KHRSurface.vkDestroySurfaceKHR(instance, surface, null);

		VK14.vkDestroyInstance(instance, null);

		window.close();
		errorCallback.close();
		GLFW.glfwTerminate();
	}

}