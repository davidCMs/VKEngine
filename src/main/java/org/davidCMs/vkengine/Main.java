package org.davidCMs.vkengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

public class Main {


	private static final Logger log = LogManager.getLogger(Main.class);
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

		System.setProperty("log4j2.configurationFile", "src/main/resources/log4j2.json");

		log.trace("TRACE level log");
		log.debug("DEBUG level log");
		log.info("INFO level log");
		log.warn("WARN level log");
		log.error("ERROR level log");
		log.fatal("FATAL level log");

		Configuration.DEBUG.set(true);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
		Configuration.DEBUG_STACK.set(true);

		GLFW.glfwInit();

		errorCallback = GLFWErrorCallback.createPrint(System.err).set();

		init();
	}

	public static void init() {
		try {
			log.info("Initialising window.");
			initWindow();
			log.info("Initialising vulkan.");
			initVulkan();

			log.info("Entering main loop.");
			mainLoop();
		} catch (Throwable t) {
			t.printStackTrace();

		} finally {
			log.info("Cleaning up.");
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
		log.info("Required GLFW Vulkan extensions: ");
		requiredExtensions.add(VkExtensionUtils.EXT_DEBUG_UTILS_NAME);
		requiredExtensions.forEach(log::info);

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


		log.info("Created vulkan instance");

		surface = window.makeVkSurface(instance.instance());
		log.info("Created vulkan surface");

		log.info("Searching for a suitable GPU...");
		for (VkPhysicalDevice device : VkPhysicalDeviceUtils.getAvailablePhysicalDevices(instance.instance())) {
			VkPhysicalDeviceInfo pdInfo = VkPhysicalDeviceInfo.getFrom(device);
			log.debug("\tChecking if device: \"{}\" is suitable", pdInfo.properties().deviceName());
			if (!VkPhysicalDeviceExtensionUtils.checkAvailabilityOf(device, VkPhysicalDeviceExtensionUtils.VK_KHR_SWAPCHAIN))
				continue;
			for (VkQueueFamily family : pdInfo.queueFamilies()) {
				log.debug("\t\tChecking queue family: {}", family.getIndex());
				if (family.capableOfGraphics() && graphicsFamily == null) {
					log.debug("\t\t\tFamily: {} can do graphics", family.getIndex());
					graphicsFamily = family;
				}
				if (VkPhysicalDeviceUtils.canRenderTo(device, family, surface) && presentFamily == null) {
					log.debug("\t\t\tFamily: {} can do presentation", family.getIndex());
					presentFamily = family;
				}
				if (graphicsFamily != null || presentFamily != null) {
					log.debug("\t\t\tExiting loop as family: {} is capable of graphics and presentation", family.getIndex());
					break;
				}
			}
			if (graphicsFamily == null || presentFamily == null) {
				log.info("Device: \"{}\" is not suitable", pdInfo.properties().deviceName());
				physicalDevice = null;
				physicalDeviceInfo = null;
				graphicsFamily = null;
				presentFamily = null;
			} else {
				log.info("Device: \"{}\" is suitable", pdInfo.properties().deviceName());
				physicalDevice = device;
				physicalDeviceInfo = pdInfo;
			}
		}
		if (physicalDevice == null) {
			log.fatal("Could not find a suitable GPU!", new RuntimeException("Could not find a suitable device"));
		}

		log.info("Found a suitable GPU: {}", physicalDeviceInfo.properties().deviceName());
		log.info("Graphics queue family index is: {}", graphicsFamily.getIndex());
		log.info("Present queue family index is: {}", presentFamily.getIndex());

		log.debug(LogUtil.beautify(physicalDeviceInfo));

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

		log.info("Created vulkan device and queues");
		graphicsQueue = device.getQueue(graphicsFamily, 0);
		presentQueue = device.getQueue(presentFamily, 0);

		VkPhysicalDeviceSwapChainInfo swapChainInfo = VkPhysicalDeviceSwapChainInfo.getFrom(physicalDevice, surface);

		log.debug(LogUtil.beautify(swapChainInfo));

		VkSwapchainBuilder vkSwapchainBuilder = new VkSwapchainBuilder(surface, device)
				.setImageExtent(window.getFrameBufferSize())
				.setCompositeAlpha(VkCompositeAlpha.OPAQUE)
				.setImageArrayLayers(1)
				.setImageColorSpace(VkImageColorSpace.SRGB_NONLINEAR)
				.setImageExtent(window.getFrameBufferSize())
				.setImageFormat(VkImageFormat.R8G8B8A8_SRGB)
				.setQueueFamilies(graphicsFamily == presentFamily ? Set.of(graphicsFamily) : Set.of(graphicsFamily, presentFamily))
				.setImageUsage(VkImageUsage.COLOR_ATTACHMENT)
				.setMinImageCount(3)
				.setSurfaceTransform(VkSurfaceTransform.IDENTITY)
				.setPresentMode(VkPresentMode.MAILBOX);

		log.info("Created vulkan swapchain");
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