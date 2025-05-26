package org.davidCMs.vkengine;

import org.davidCMs.vkengine.util.LogUtil;
import org.davidCMs.vkengine.vk.*;
import org.davidCMs.vkengine.vk.VkPhysicalDeviceInfo;
import org.davidCMs.vkengine.window.GLFWWindow;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Configuration;
import org.lwjgl.vulkan.*;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_SRGB;
import static org.lwjgl.vulkan.VK10.vkDestroyInstance;

public class Main {

	static GLFWErrorCallback errorCallback;
	static GLFWWindow window;

	static VkInstanceContext instance;
	static VkDebugUtilsMessengerCallbackEXT messengerCallback;

	static long surface;

	static VkPhysicalDevice physicalDevice = null;
	static VkPhysicalDeviceInfo physicalDeviceInfo = null;

	static VkQueueFamily graphicsFamily = null;
	static VkQueueFamily presentFamily = null;

	static VkDeviceContext device;
	static VkQueue graphicsQueue;
	static VkQueue presentQueue;

	static VkSwapchainContext swapchain;

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
		} catch (Throwable t) {
			t.printStackTrace();

		} finally {
			clean();
		}

	}

	public static void initWindow() {
		window = new GLFWWindow(800, 600, "VK Window");
		window.setVisible(true);
	}

	public static void initVulkan() {
		Set<String> reqExt = VkExtensionUtils.getRequiredVkExtensions();
		reqExt.add(VkExtensionUtils.EXT_DEBUG_UTILS_NAME);

		Set<String> requiredExtensions = VkExtensionUtils.getRequiredVkExtensions();
		System.out.println("Required GLFW Vulkan extensions: ");
		requiredExtensions.add(VkExtensionUtils.EXT_DEBUG_UTILS_NAME);
		requiredExtensions.forEach(System.out::println);
		System.out.println();

		instance = new VkInstanceBuilder()
				.setApplicationName("Game")
				.setApplicationVersion(new VkVersion(1,0, 0, 1))
				.setEngineName("VKEngine")
				.setEngineVersion(new VkVersion(1,0, 0, 1))
				.setDebugMessageSeverities(
						//VkEDebugMessageSeverity.INFO,
						VkDebugMessageSeverity.VERBOSE,
						VkDebugMessageSeverity.WARNING,
						VkDebugMessageSeverity.ERROR
				)
				.setDebugMessageTypes(
						VkDebugMessageType.GENERAL,
						VkDebugMessageType.PERFORMANCE,
						VkDebugMessageType.VALIDATION
				)
				.setEnabledExtensions(requiredExtensions)
				.setEnabledLayers(VkLayerUtils.KHRONOS_VALIDATION_NAME)
				.build();


		System.out.println("Created instance");

		surface = window.makeVkSurface(instance.instance());
		System.out.println("Created surface");

		for (VkPhysicalDevice device : VkPhysicalDeviceUtils.getAvailablePhysicalDevices(instance.instance())) {
			VkPhysicalDeviceInfo pdInfo = VkPhysicalDeviceInfo.getFrom(device);
			System.out.println("Checking if device: \"" + pdInfo.properties().deviceName() + "\" is suitable");
			if (!VkPhysicalDeviceExtensionUtils.checkAvailabilityOf(device, VkPhysicalDeviceExtensionUtils.VK_KHR_SWAPCHAIN))
				continue;
			for (VkQueueFamily family : pdInfo.queueFamilies()) {
				System.out.println("Checking queue family: " + family.getIndex());
				if (family.capableOfGraphics() && graphicsFamily == null) {
					System.out.println("Family: " + family.getIndex() + " can do graphics");
					graphicsFamily = family;
				}
				if (VkPhysicalDeviceUtils.canRenderTo(device, family, surface) && presentFamily == null) {
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

		LogUtil.printObj(physicalDeviceInfo);

		Set<VkDeviceBuilderQueueInfo> queueInfos = new HashSet<>();

		queueInfos.add(graphicsFamily.makeCreateInfo());
		if (presentFamily != graphicsFamily) {
			queueInfos.add(presentFamily.makeCreateInfo());
		}

		VkDeviceBuilder deviceBuilder = new VkDeviceBuilder()
				.setPhysicalDevice(physicalDevice)
				.setExtensions(VkPhysicalDeviceExtensionUtils.VK_KHR_SWAPCHAIN)
				.setQueueInfos(queueInfos);

		device = deviceBuilder.build();

		System.out.println("Created device and queues");
		graphicsQueue = device.getQueue(graphicsFamily, 0);
		presentQueue = device.getQueue(presentFamily, 0);

		VkPhysicalDeviceSwapChainInfo swapChainInfo = VkPhysicalDeviceSwapChainInfo.getFrom(physicalDevice, surface);

		LogUtil.printObj(swapChainInfo);

		VkSwapchainBuilder vkSwapchainBuilder = new VkSwapchainBuilder(surface, device)
				.setCompositeAlpha(CompositeAlpha.OPAQUE)
				.setImageArrayLayers(1)
				.setImageColorSpace(KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)
				.setImageExtent(window.getFrameBufferSize())
				.setImageFormat(VK_FORMAT_B8G8R8A8_SRGB)
				.setQueueFamilies(graphicsFamily == presentFamily ? Set.of(graphicsFamily) : Set.of(graphicsFamily, presentFamily))
				.setImageUsage(VkImageUsage.COLOR_ATTACHMENT);

		swapchain = new VkSwapchainContext(vkSwapchainBuilder);

	}

	public static void mainLoop() {
		while (!window.shouldClose()) {
			GLFW.glfwPollEvents();
		}
	}

	public static void clean() {

		swapchain.destroy();

		device.destroy();
		KHRSurface.vkDestroySurfaceKHR(instance.instance(), surface, null);

		instance.destroy();

		window.close();
		errorCallback.close();
		GLFW.glfwTerminate();
	}



}