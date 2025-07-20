package org.davidCMs.vkengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidCMs.vkengine.common.ColorRGBA;
import org.davidCMs.vkengine.shader.*;
import org.davidCMs.vkengine.util.IOUtils;
import org.davidCMs.vkengine.vk.*;
import org.davidCMs.vkengine.vk.VkCommandBuffer;
import org.davidCMs.vkengine.vk.VkPhysicalDeviceInfo;
import org.davidCMs.vkengine.vk.VkQueue;
import org.davidCMs.vkengine.vk.VkRect2D;
import org.davidCMs.vkengine.vk.VkViewport;
import org.davidCMs.vkengine.window.GLFWWindow;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	static ShaderCompiler shaderCompiler;

	static VkPipelineContext pipeline;

	static VkCommandPool commandPool;
	static VkCommandBuffer[] commandBuffers;

	static final int framesInFlight = 2;

	static VkBinarySemaphore[] presentCompleteSemaphores = new VkBinarySemaphore[framesInFlight];
	static VkBinarySemaphore[] renderFinishedSemaphores;
	static VkFence[] drawFences = new VkFence[framesInFlight];


	public static void main(String[] args) throws Exception {

		System.setProperty("log4j2.configurationFile", "src/main/resources/log4j2.json");

		//log.trace("TRACE level log");
		//log.debug("DEBUG level log");
		//log.info("INFO level log");
		//log.warn("WARN level log");
		//log.error("ERROR level log");
		//log.fatal("FATAL level log");

		Configuration.DEBUG.set(true);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
		Configuration.DEBUG_STACK.set(true);
		Configuration.STACK_SIZE.set(1024*1024);

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
		Set<String> requiredExtensions = VkExtensionUtils.getRequiredVkExtensions();
		log.info("Required GLFW Vulkan extensions: ");
		requiredExtensions.add(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME);
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

		Set<VkDeviceBuilderQueueInfo> queueInfos = new HashSet<>();

		queueInfos.add(graphicsFamily.makeCreateInfo());
		if (presentFamily != graphicsFamily) {
			queueInfos.add(presentFamily.makeCreateInfo());
		}

		VkDeviceBuilder deviceBuilder = new VkDeviceBuilder()
				.setPhysicalDevice(physicalDevice)
				.setExtensions(
						VkPhysicalDeviceExtensionUtils.VK_KHR_SWAPCHAIN,
						KHRDynamicRendering.VK_KHR_DYNAMIC_RENDERING_EXTENSION_NAME
				)
				.setQueueInfos(queueInfos)
				.setpNext(new VkPhysicalDeviceFeaturesBuilder()
						.setDynamicRendering(true)
						.setSynchronization2(true)
						.setWideLines(true)
						.setFillModeNonSolid(true));

		VkPhysicalDeviceFeaturesBuilder b = new VkPhysicalDeviceFeaturesBuilder()
				.setDynamicRendering(true)
				.setSynchronization2(true)
				.setWideLines(true)
				.setFillModeNonSolid(true);

		try (MemoryStack stack = MemoryStack.stackPush()) {

			StringBuilder sb = new StringBuilder();
			VkBaseOutStructure structure = VkBaseOutStructure.createSafe(b.build(stack).address());
			while (structure != null) {
				sb.append(structure.sType()).append(">");
				structure = structure.pNext();
			}
			log.info(sb);

		}


		device = deviceBuilder.build();


		log.info("Created vulkan device and queues");
		graphicsQueue = device.getQueue(graphicsFamily, 0);
		presentQueue = device.getQueue(presentFamily, 0);

		VkSwapchainBuilder swapchainBuilder = new VkSwapchainBuilder(surface, device)
				.setImageExtent(window.getFrameBufferSize())
				.setCompositeAlpha(VkCompositeAlpha.OPAQUE)
				.setImageArrayLayers(1)
				.setImageColorSpace(VkImageColorSpace.SRGB_NONLINEAR)
				.setImageExtent(window.getFrameBufferSize())
				.setImageFormat(VkImageFormat.R8G8B8A8_SRGB)
				.setQueueFamilies(graphicsFamily == presentFamily ? Set.of(graphicsFamily) : Set.of(graphicsFamily, presentFamily))
				.setImageUsage(Set.of(VkImageUsage.COLOR_ATTACHMENT))
				.setMinImageCount(3)
				.setSurfaceTransform(VkSurfaceTransform.IDENTITY)
				.setPresentMode(VkPresentMode.MAILBOX);

		log.info("Created vulkan swapchain");
		swapchain = swapchainBuilder.newContext();
		swapchain.rebuild();

		renderingInfo.setRenderArea(new VkRect2D(new Vector2i(0,0), swapchain.getExtent()));

		scissor = new VkRect2D(
				new Vector2i(),
				swapchain.getExtent()
		);

		scissor = new VkRect2D(
				new Vector2i(),
				swapchain.getExtent()
		);

		ShaderCompilerBuilder compilerBuilder = new ShaderCompilerBuilder()
				.setGenerateDebugInfo(true)
				.setOptimizationLevel(OptimizationLevel.ZERO)
				.setInvertY(false)
				.setSetNaNClap(false)
				.setWarningsAsErrors(true)
				.setSuppressWarnings(false);

		shaderCompiler = compilerBuilder.build();

		String vertResource = "/shaders/src/main.vert";
		String fragResource = "/shaders/src/main.frag";

		String vertSrc;
		String fragSrc;

		try {
			vertSrc = IOUtils.loadResource(vertResource);
			fragSrc = IOUtils.loadResource(fragResource);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		CompilationResult vertResult = shaderCompiler.compile(vertSrc, ShaderStage.VERTEX, vertResource);
		CompilationResult fragResult = shaderCompiler.compile(fragSrc, ShaderStage.FRAGMENT, fragResource);

		if (vertResult.status() != CompilationStatus.SUCCESS) {
			log.error("Vertex shader errors: \n{}", vertResult.errors());
			log.info("Vertex shader src: \n{}", vertSrc);
			throw new RuntimeException("Vertex shader compilation failed");
		} else log.info("Successfully compiled vertex shader");
		log.info("Vertex shader compilation errors: \n{}", vertResult.errors());

		if (fragResult.status() != CompilationStatus.SUCCESS) {
			log.error("Fragment shader errors: \n{}", vertResult.errors());
			log.info("Fragment shader src: \n{}", fragSrc);
			log.error("\n{}", fragResult.errors());
			throw new RuntimeException("Fragment shader compilation failed");
		} else log.info("Successfully compiled fragment shader");
		log.info("Fragment shader compilation errors: \n{}", fragResult.errors());

		VkShaderModule vertShaderModule = new VkShaderModule(
				device,
				vertResult.bin(),
				ShaderStage.VERTEX
		);
		log.info("Created shader module for vertex shader");

		VkShaderModule fragShaderModule = new VkShaderModule(
				device,
				fragResult.bin(),
				ShaderStage.FRAGMENT
		);
		log.info("Created shader module for fragment shader");

		VkSpecializationInfoMapper mapper = new VkSpecializationInfoMapper();
		mapper.mapInt(10, 100);

		VkPipelineShaderStageBuilder vertStage = new VkPipelineShaderStageBuilder()
				.setModule(vertShaderModule);

		VkPipelineShaderStageBuilder fragStage = new VkPipelineShaderStageBuilder()
				.setModule(fragShaderModule);

		viewport = new VkViewport(
				new Vector2f(),
				new Vector2f(
						(float) swapchain.getExtent().x,
						(float) swapchain.getExtent().y
				),
				new Vector2f(0, 1)
		);

		scissor = new VkRect2D(
				new Vector2i(),
				swapchain.getExtent()
		);

		VkGraphicsPipelineBuilder pipelineBuilder = new VkGraphicsPipelineBuilder()
				.setDynamicState(new VkPipelineDynamicStateBuilder()
						.setDynamicStates(
								Set.of(
									VkDynamicState.VIEWPORT,
									VkDynamicState.SCISSOR
							)
						)
				)
				.setStages(
						List.of(
								new VkPipelineShaderStageBuilder()
										.setModule(vertShaderModule)
										.setEntryPoint("main"),
								new VkPipelineShaderStageBuilder()
										.setModule(fragShaderModule)
										.setEntryPoint("main")
						)
				)
				.setViewportState(new VkPipelineViewportStateBuilder()
						.setViewportCount(1)
						.setScissorsCount(1)
				)
				.setVertexInputState(new VkPipelineVertexInputStateBuilder())
				.setInputAssemblyState(new VkPipelineInputAssemblyStateBuilder()
						.setPrimitiveTopology(VkPrimitiveTopology.TRIANGLE_LIST)
						.setPrimitiveRestartEnable(false)
				)
				.setRasterizationState(new VkPipelineRasterizationStateBuilder()
						.setDepthClampEnable(false)
						.setRasterizerDiscardEnable(false)
						.setPolygonMode(VkPolygonMode.FILL)
						.setLineWidth(30.0f)
						.setCullMode(VkCullMode.BACK)
						.setFrontFace(VkFrontFace.CLOCKWISE)
						.setDepthBiasEnable(false)
						.setDepthBiasConstantFactor(0)
						.setDepthBiasClamp(0)
						.setDepthBiasSlopeFactor(0)
				)
				.setMultisampleState(new VkPipelineMultisampleStateBuilder()
						.setSampleShadingEnable(false)
						.setRasterizationSamples(VkSampleCount.SAMPLE_1)
						.setMinSampleShading(1.0f)
						.setAlphaToCoverageEnable(false)
						.setAlphaToOneEnable(false)
				)
				.setColorBlendState(new VkPipelineColorBlendStateBuilder()
						.setLogicOpEnable(false)
						.setLogicOp(VkLogicOp.COPY)
						.setBlendAttachments(
								List.of(
										new VkPipelineColorBlendAttachmentStateBuilder()
												.setColorWriteMask(
														Set.of(
															VkColorComponent.R,
															VkColorComponent.G,
															VkColorComponent.B,
															VkColorComponent.A
														)
												)
												.setBlendEnable(true)
												.setSrcColorBlendFactor(VkBlendFactor.ONE)
												.setDstColorBlendFactor(VkBlendFactor.ZERO)
												.setColorBlendOp(VkBlendOp.ADD)
												.setSrcAlphaBlendFactor(VkBlendFactor.ONE)
												.setDstAlphaBlendFactor(VkBlendFactor.ZERO)
												.setAlphaBlendOp(VkBlendOp.ADD)
								)
						)
						.setBlendConstants(
								new ColorRGBA(
										0.0f,
										0.0f,
										0.0f,
										0.0f
								)
						)
				)
				.setPipelineLayout(new VkPipelineLayoutCreateInfoBuilder()
						.setPushConstantRanges(List.of(
								new VkPushConstantRangeBuilder()
										.setSize(Integer.BYTES)
										.setOffset(0)
										.setStageFlags(Set.of(
												ShaderStage.FRAGMENT,
												ShaderStage.VERTEX
										))
						)))
				.setpNext(new VkPipelineRenderingBuilder()
						.setColorAttachmentCount(1)
						.setColorAttachmentFormats(
								List.of(
										swapchain.getBuilder().getImageFormat()
								)
						)
				)
				;

		pipeline = pipelineBuilder.newContext(device);
		vertShaderModule.destroy();
		fragShaderModule.destroy();

		log.info("Successfully created graphics pipeline");

		commandPool = graphicsFamily.createCommandPool(device, VkCommandPoolCreateFlags.RESET_COMMAND_BUFFER);
		log.info("Successfully created command pool");

		commandBuffers = commandPool.createCommandBuffer(framesInFlight);
		log.info("Successfully allocated command buffers");

		for (int i = 0; i < framesInFlight; i++) {
			presentCompleteSemaphores[i] = new VkBinarySemaphore(device);
			drawFences[i] = new VkFence(device, true);
		}

		renderFinishedSemaphores = new VkBinarySemaphore[swapchain.getImages().get().size()];
		for (int i = 0; i < swapchain.getImages().get().size(); i++) {
			renderFinishedSemaphores[i] = new VkBinarySemaphore(device);
		}
		log.info("Successfully created sync objects");

	}

	private static final VkImageMemoryBarrierBuilder top = new VkImageMemoryBarrierBuilder()
			.setOldLayout(VkImageLayout.UNDEFINED)
			.setNewLayout(VkImageLayout.COLOR_ATTACHMENT_OPTIMAL)
			.setDstAccessMask(
					Set.of(VkAccess.COLOR_ATTACHMENT_WRITE)
			)
			.setSrcStageMask(
					Set.of(VkPipelineStage.TOP_OF_PIPE)
			)
			.setDstStageMask(
					Set.of(VkPipelineStage.COLOR_ATTACHMENT_OUTPUT)
			)
			.setSubresourceRange(new VkImageSubresourceRangeBuilder()
					.setAspectMask(VkAspectMask.COLOR));

	private static final VkRenderingAttachmentInfoBuilder renderingAttachment = new VkRenderingAttachmentInfoBuilder()
			.setImageLayout(VkImageLayout.COLOR_ATTACHMENT_OPTIMAL)
			.setLoadOp(VkAttachmentLoadOp.CLEAR)
			.setStoreOp(VkAttachmentStoreOp.STORE)
			.setClearValue(new ColorRGBA(0,0,0,1));

	private static final VkRenderingInfoBuilder renderingInfo = new VkRenderingInfoBuilder()
			.setLayerCount(1)
			.setColorAttachments(
					List.of(
						renderingAttachment
					)
			);

	private static VkViewport viewport;

	public static VkRect2D scissor;

	public static final VkImageMemoryBarrierBuilder bottom = new VkImageMemoryBarrierBuilder()
			.setOldLayout(VkImageLayout.COLOR_ATTACHMENT_OPTIMAL)
			.setNewLayout(VkImageLayout.PRESENT_SRC)
			.setSrcAccessMask(
					Set.of(
							VkAccess.COLOR_ATTACHMENT_WRITE
					)
			)
			.setSrcStageMask(
					Set.of(
							VkPipelineStage.COLOR_ATTACHMENT_OUTPUT
					)
			)
			.setDstStageMask(
					Set.of(
							VkPipelineStage.BOTTOM_OF_PIPE
					)
			)
			.setSubresourceRange(new VkImageSubresourceRangeBuilder()
					.setAspectMask(VkAspectMask.COLOR));

	public static float frameNum = 0;
	public static int currentFrame = 0;
	public static void recordCmdBuffer(VkCommandBuffer commandBuffer, VkImageView imageView) {
		top.setImage(imageView.image());
		renderingAttachment.setImageView(imageView);
		bottom.setImage(imageView.image());

		commandBuffer.begin();
		commandBuffer.insertImageMemoryBarrier(top);
		commandBuffer.beginRendering(renderingInfo);
		commandBuffer.bindPipeline(VkPipelineBindPoint.GRAPHICS, pipeline);
		commandBuffer.setViewport(viewport);
		commandBuffer.setScissor(scissor);
		commandBuffer.pushConstants(
				pipeline,
				new ShaderStage[]{ShaderStage.VERTEX, ShaderStage.FRAGMENT},
				0,
				frameNum
				);
		commandBuffer.draw(3, 1, 0, 0);
		commandBuffer.endRendering();
		commandBuffer.insertImageMemoryBarrier(bottom);
		commandBuffer.end();

	}

	public static void drawFrame() {
		drawFences[currentFrame].waitFor();

		int imageIndex = swapchain.acquireNextImage(presentCompleteSemaphores[currentFrame]);
		VkImageView imageView = swapchain.getImageView(imageIndex);

		drawFences[currentFrame].reset();

		recordCmdBuffer(commandBuffers[currentFrame], imageView);

		graphicsQueue.submit(drawFences[currentFrame], new VkQueue.VkSubmitInfoBuilder()
				.setWaitSemaphores(
						new VkQueue.VkSubmitInfoBuilder.VkSemaphoreSubmitInfo(
								presentCompleteSemaphores[currentFrame], VkPipelineStage.COLOR_ATTACHMENT_OUTPUT
						)
				)
				.setCommandBuffers(commandBuffers[currentFrame])
				.setSignalSemaphores(
						new VkQueue.VkSubmitInfoBuilder.VkSemaphoreSubmitInfo(
								renderFinishedSemaphores[imageIndex], VkPipelineStage.COLOR_ATTACHMENT_OUTPUT
						)
				));

		presentQueue.present(renderFinishedSemaphores[imageIndex], swapchain, imageIndex);
		frameNum++;
		currentFrame = (int) (frameNum % framesInFlight);
	}

	public static void mainLoop() {
		while (!window.shouldClose()) {
			GLFW.glfwPollEvents();
			drawFrame();
		}
		device.waitIdle();
	}

	public static void clean() {

		try {
			for (int i = 0; i < framesInFlight; i++) {
				drawFences[i].destroy();
				renderFinishedSemaphores[i].destroy();
				presentCompleteSemaphores[i].destroy();
			}
		} catch (Exception e) {
			log.warn("Failed to destroy sync objects");
		}

		try {
			commandPool.destroy();
		} catch (Exception e) {
			log.warn("Failed to destroy command pool");
		}

		try {
			pipeline.destroy();
		} catch (Exception e) {
			log.warn("Failed to destroy pipeline");
		}

		try {
			shaderCompiler.close();
		} catch (Exception e) {
			log.warn("Failed to close shaderCompiler");
		}

		try {
			swapchain.destroy();
		} catch (Exception e) {
			log.warn("Failed to destroy swapchain");
		}

		try {
			device.destroy();
		} catch (Exception e) {
			log.warn("Failed to destroy device");
		}

		try {
			KHRSurface.vkDestroySurfaceKHR(instance.instance(), surface, null);
		} catch (Exception e) {
			log.warn("Failed to destroy surface");
		}

		try {
			instance.destroy();
		} catch (Exception e) {
			log.warn("Failed to destroy instance");
		}

		try {
			window.close();
		} catch (Exception e) {
			log.warn("Failed to close window");
		}

		try {
			errorCallback.close();
		} catch (Exception e) {
			log.warn("Failed to close errorCallback");
		}

		try {
			GLFW.glfwTerminate();
		} catch (Exception e) {
			log.warn("Failed to terminate glfw");
		}
	}



}