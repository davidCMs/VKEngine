package dev.davidCMs.vkengine;

import dev.davidCMs.vkengine.common.IFence;
import dev.davidCMs.vkengine.graphics.RenderDevice;
import dev.davidCMs.vkengine.graphics.shader.*;
import dev.davidCMs.vkengine.graphics.vk.*;
import dev.davidCMs.vkengine.graphics.vk.VkCommandBuffer;
import dev.davidCMs.vkengine.graphics.vk.VkPhysicalDevice;
import dev.davidCMs.vkengine.graphics.vk.VkQueue;
import dev.davidCMs.vkengine.graphics.vk.VkRect2D;
import dev.davidCMs.vkengine.graphics.vk.VkVertexInputAttributeDescription;
import dev.davidCMs.vkengine.graphics.vk.VkVertexInputBindingDescription;
import dev.davidCMs.vkengine.graphics.vk.VkViewport;
import dev.davidCMs.vkengine.graphics.vma.VmaAllocationBuilder;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;
import dev.davidCMs.vkengine.common.AutoCloseableByteBuffer;
import dev.davidCMs.vkengine.common.ColorRGBA;
import dev.davidCMs.vkengine.util.FiniteLog;
import dev.davidCMs.vkengine.util.IOUtils;
import dev.davidCMs.vkengine.util.LogUtils;
import dev.davidCMs.vkengine.window.GLFWUtils;
import dev.davidCMs.vkengine.window.GLFWWindow;
import dev.davidCMs.vkengine.window.GlfwEnums;
import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

	public static final boolean debug = false;

	private static final TaggedLogger log = Logger.tag("Main");
	static GLFWErrorCallback errorCallback;
	static GLFWWindow window;

	static VkInstanceContext instance;

	static long surface;

	static RenderDevice renderDevice;

	static VkSwapchainContext swapchain;

	static ShaderCompiler shaderCompiler;

	static VkPipelineContext pipeline;

	static VkCommandPool commandPool;
	static VkCommandBuffer[] commandBuffers;

    static VkQueue presentQueue;

	static final int framesInFlight = 3;
	public static int pcSize =
					1 * Float.BYTES + 		//frameNum
					1 * Float.BYTES + 		//time
					2 * Integer.BYTES +		//swapchain extent
					2 * Float.BYTES + 		//mouse pos
					1 * Integer.BYTES +		//mouse button mask
					1 * Float.BYTES			//mouse total scroll
			;

	static Thread renderThread;

	static VkBinarySemaphore[] presentCompleteSemaphores = new VkBinarySemaphore[framesInFlight];
	static VkBinarySemaphore[] renderFinishedSemaphores;
	static VkFence[] drawFences = new VkFence[framesInFlight];

	static VkBuffer vbo;

    static VkBuffer[] bufs = new VkBuffer[1000];

	public static void main(String[] args) {

		//log.trace("TRACE level log");
		//log.debug("DEBUG level log");
		//log.info("INFO level log");
		//log.warn("WARN level log");
		//log.error("ERROR level log");
		//log.error("FATAL level log");

		Configuration.DEBUG.set(debug);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(debug);
		Configuration.DEBUG_STACK.set(debug);

		Configuration.STACK_SIZE.set(1024*1024);

		if (!GLFW.glfwInit())
			throw new RuntimeException("Failed to init glfw");

		errorCallback = new GLFWErrorCallback() {
			@Override
			public void invoke(int error, long description) {
				log.error(MemoryUtil.memUTF8Safe(description));
			}
		}.set();

		log.info("Environment: ");
		System.getenv().forEach((k, v) -> log.info("\t{} = {}", k, v));

/*
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();



 */

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
		window.addKeyCallback((window1, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_F11 && action == GLFW.GLFW_PRESS)
                window.toggleFullScreen();
        });
        window.setVisible(true);
	}

	public static void initVulkan() {
		//Set<VkExtension> availableExtensions = VkExtension.getAvailableExtension();
		//availableExtensions.forEach(obj -> log.info("{}", obj));
		Set<VkExtension> requiredExtensions = GLFWUtils.getRequiredVkExtensions();
		log.info("Required GLFW Vulkan extensions: ");
		if (debug) requiredExtensions.add(VkExtension.EXT_DEBUG_UTILS);
		requiredExtensions.forEach(obj -> log.info("{}", obj));

		//VkLayer.getAvailableLayers().forEach(obj -> log.info("{}", obj));

		Set<VkLayer> enabledLayers = new HashSet<>();
		if (debug) {
			enabledLayers.add(VkLayer.KHRONOS_VALIDATION);
			//enabledLayers.add(VkLayer.LUNARG_API_DUMP);
		}

		VkInstanceBuilder instanceBuilder = new VkInstanceBuilder()
				.setApplicationName("Game")
				.setApplicationVersion(new VkVersion(1,0, 0, 1))
				.setEngineName("VKEngine")
				.setEngineVersion(new VkVersion(1,0, 0, 1))
				.enabledExtensions().add(requiredExtensions).ret()
				.enabledLayers().add(enabledLayers).ret();

		log.error(instanceBuilder.enabledExtensions().toString());

		if (debug) instanceBuilder
				.debugMessageSeverities().add(
                    //VkDebugMessageSeverity.INFO,
					VkDebugMessageSeverity.VERBOSE,
					VkDebugMessageSeverity.WARNING,
					VkDebugMessageSeverity.ERROR
				).ret()
				.debugMessageTypes().add(
					VkDebugMessageType.GENERAL,
					VkDebugMessageType.PERFORMANCE,
					VkDebugMessageType.VALIDATION
				);

		instance = instanceBuilder.build();


		log.info("Created vulkan instance");

		surface = window.getVkSurface(instance);
		log.info("Created vulkan surface");

        /*
		log.info("Searching for a suitable GPU...");
		for (VkPhysicalDevice device : VkPhysicalDevice.getAvailablePhysicalDevices(instance)) {
			VkPhysicalDeviceInfo pdInfo = device.getInfo();
			log.debug("\tChecking if device: \"{}\" is suitable", pdInfo.properties().deviceName());
			if (!VkPhysicalDeviceExtensionUtils.checkAvailabilityOf(device, VkPhysicalDeviceExtensionUtils.VK_KHR_SWAPCHAIN))
				continue;
			for (VkQueueFamily family : pdInfo.queueFamilies()) {
				log.debug("\t\tChecking queue family: {}", family.getIndex());
				if (family.capableOfGraphics() && graphicsFamily == null) {
					log.debug("\t\t\tFamily: {} can do graphics", family.getIndex());
					graphicsFamily = family;
				}
				if (family.canRenderTo(surface) && presentFamily == null) {
					log.debug("\t\t\tFamily: {} can do presentation", family.getIndex());
					presentFamily = family;
				}
				if (family.capableOfTransfer()
						&& family != graphicsFamily
						&& family != presentFamily
						&& transferFamily == null) {
					log.debug("\t\t\tFamily: {} is capable of dedicated transfer operations", family.getIndex());
					transferFamily = family;
				}
				if (graphicsFamily != null && presentFamily != null && transferFamily != null) {
					log.debug("\t\tExiting loop as all the required queue families have ben found");
					log.debug("\t\tGraphics:       {}", graphicsFamily.getIndex());
					log.debug("\t\tPresentation:   {}", presentFamily.getIndex());
					log.debug("\t\tTransfer:       {}", transferFamily.getIndex());
					break;
				}
			}
			if (graphicsFamily == null || presentFamily == null) {
				log.info("Device: \"{}\" is not suitable", pdInfo.properties().deviceName());
				physicalDevice = null;
				graphicsFamily = null;
				presentFamily = null;
				transferFamily = null;
			} else {
				log.info("Device: \"{}\" is suitable", pdInfo.properties().deviceName());
				physicalDevice = device;
				if (transferFamily == null) {
					log.debug("Failed to find a dedicated transfer family, will use graphic family for transfer operations");
					transferFamily = graphicsFamily;
				}
			}
		}

         */

        VkPhysicalDevice physicalDevice = RenderDevice.pickBestDevice(instance, surface);

		if (physicalDevice == null) {
			log.error("Could not find a suitable device!");
            throw new RuntimeException("Could not find a suitable device");
		}

        HashSet<String> wantedExtensions = new HashSet<>(Set.of(
                KHRDedicatedAllocation.VK_KHR_DEDICATED_ALLOCATION_EXTENSION_NAME,
                KHRBindMemory2.VK_KHR_BIND_MEMORY_2_EXTENSION_NAME,
                KHRMaintenance2.VK_KHR_MAINTENANCE_2_EXTENSION_NAME,
                KHRMaintenance4.VK_KHR_MAINTENANCE_4_EXTENSION_NAME,
                KHRMaintenance5.VK_KHR_MAINTENANCE_5_EXTENSION_NAME,
                EXTMemoryBudget.VK_EXT_MEMORY_BUDGET_EXTENSION_NAME,
                KHRBufferDeviceAddress.VK_KHR_BUFFER_DEVICE_ADDRESS_EXTENSION_NAME,
                EXTMemoryPriority.VK_EXT_MEMORY_PRIORITY_EXTENSION_NAME,
                AMDDeviceCoherentMemory.VK_AMD_DEVICE_COHERENT_MEMORY_EXTENSION_NAME,
                KHRExternalMemoryWin32.VK_KHR_EXTERNAL_MEMORY_WIN32_EXTENSION_NAME
        ));
        wantedExtensions.removeIf((s) -> !VkPhysicalDeviceExtensionUtils.checkAvailabilityOf(physicalDevice, s));
        wantedExtensions.addAll(Set.of(
                VkPhysicalDeviceExtensionUtils.VK_KHR_SWAPCHAIN,
                KHRDynamicRendering.VK_KHR_DYNAMIC_RENDERING_EXTENSION_NAME,
                EXTMemoryPriority.VK_EXT_MEMORY_PRIORITY_EXTENSION_NAME
        ));

        for (String ext : wantedExtensions) {
            log.info(ext);
        }

        log.info("Created vulkan device and queues");
        renderDevice = new RenderDevice(physicalDevice,
                new VkPhysicalDeviceFeaturesBuilder()
                        .setDynamicRendering(true)
                        .setSynchronization2(true)
                        .setWideLines(true)
                        .setFillModeNonSolid(true)
                        .setBufferDeviceAddress(true)
                        .setMemoryPriority(true),
                wantedExtensions
        );

        VkQueueFamily graphicsFamily = renderDevice.getGraphicsQueue().getQueueFamily();
        presentQueue = renderDevice.getPresentQueue(surface);
        VkQueueFamily presentFamily = presentQueue.getQueueFamily();

        log.info("Found a suitable GPU:           {}", physicalDevice.getInfo().properties().deviceName());
        log.info("Graphics queue family index is: {}", graphicsFamily.getIndex());
        log.info("Present queue family index is:  {}", presentFamily.getIndex());
        int transFam = 1;
        for (VkQueue queue : renderDevice.getTransferQueues())
            log.info("transfer {} queue family index is: {}", transFam++, queue.getQueueFamily().getIndex());


        log.info("Selected device memory info:  \n{}", LogUtils.beautify(physicalDevice.getInfo().memoryProperties()));
        //log.info("Selected device properties: \n{}", LogUtils.beautify(physicalDevice.properties()));

		VkSwapchainBuilder swapchainBuilder = new VkSwapchainBuilder(window, renderDevice.getDevice())
				.setImageExtent(window.getFrameBufferSize())
				.setCompositeAlpha(VkCompositeAlpha.PRE_MULTIPLIED)
				.setImageArrayLayers(1)
				.setImageColorSpace(VkImageColorSpace.SRGB_NONLINEAR)
				.setImageExtent(window.getFrameBufferSize())
				.setImageFormat(VkFormat.R8G8B8A8_UNORM)
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

		viewport = new VkViewport(
				new Vector2f(),
				new Vector2f(
						(float) swapchain.getExtent().x,
						(float) swapchain.getExtent().y
				),
				new Vector2f(0, 1)
		);

		ShaderCompilerBuilder compilerBuilder = new ShaderCompilerBuilder()
				.setGenerateDebugInfo(true)
				.setOptimizationLevel(
						debug ? OptimizationLevel.ZERO : OptimizationLevel.PERFORMANCE
				)
				.setInvertY(true)
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
				renderDevice.getDevice(),
				vertResult.bin(),
				ShaderStage.VERTEX
		);
		log.info("Created shader module for vertex shader");

		VkShaderModule fragShaderModule = new VkShaderModule(
				renderDevice.getDevice(),
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
				.setVertexInputState(new VkPipelineVertexInputStateBuilder()
						.setVertexBindingDescriptions(
								Set.of(
										new VkVertexInputBindingDescription(
												0,
												Float.BYTES * 5,
												VkVertexInputRate.VERTEX
										)
								)
						)
						.setVertexAttributeDescriptions(
								Set.of(
										new VkVertexInputAttributeDescription(
												0,
												0,
												VkFormat.R32G32B32_SFLOAT,
												0
										),
                                        new VkVertexInputAttributeDescription(
                                                1,
                                                0,
                                                VkFormat.R32G32_SFLOAT,
                                                Float.BYTES * 3
                                        )
								)
						)
				)
				.setInputAssemblyState(new VkPipelineInputAssemblyStateBuilder()
						.setPrimitiveTopology(VkPrimitiveTopology.TRIANGLE_STRIP)
						.setPrimitiveRestartEnable(false)
				)
				.setRasterizationState(new VkPipelineRasterizationStateBuilder()
						.setDepthClampEnable(false)
						.setRasterizerDiscardEnable(false)
						.setPolygonMode(VkPolygonMode.FILL)
						.setLineWidth(30.0f)
						.setCullMode(VkCullMode.NONE)
						.setFrontFace(VkFrontFace.COUNTER_CLOCKWISE)
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
												.setSrcColorBlendFactor(VkBlendFactor.SRC_ALPHA)
												.setDstColorBlendFactor(VkBlendFactor.ONE)
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
										.setSize(pcSize)
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

		pipeline = pipelineBuilder.newContext(renderDevice.getDevice());
		vertShaderModule.destroy();
		fragShaderModule.destroy();

		log.info("Successfully created graphics pipeline");

		commandPool = graphicsFamily.createCommandPool(renderDevice.getDevice(), VkCommandPoolCreateFlags.RESET_COMMAND_BUFFER);
		log.info("Successfully created command pool");

		commandBuffers = commandPool.createCommandBuffer(framesInFlight);
		log.info("Successfully allocated command buffers");

		for (int i = 0; i < framesInFlight; i++) {
			presentCompleteSemaphores[i] = new VkBinarySemaphore(renderDevice.getDevice());
			drawFences[i] = new VkFence(renderDevice.getDevice(), true);
		}

		renderFinishedSemaphores = new VkBinarySemaphore[swapchain.getImageCount()];
		for (int i = 0; i < swapchain.getImageCount(); i++) {
			renderFinishedSemaphores[i] = new VkBinarySemaphore(renderDevice.getDevice());
		}
		log.info("Successfully created sync objects");

		VkBufferBuilder builder = new VkBufferBuilder()
				.setSize(4*5*Float.BYTES)
                .getUsage()
                .add(
                        VkBufferUsageFlags.VERTEX_BUFFER,
                        VkBufferUsageFlags.TRANSFER_DST
                ).ret()
                .setAllocationBuilder(VmaAllocationBuilder.DEVICE);

		vbo = builder.build(renderDevice.getDevice());

		try (AutoCloseableByteBuffer vertices = vbo.createPreConfiguredByteBuffer()) {
			vertices.putFloat(-1).putFloat(-1).putFloat(0)
                    .putFloat(0).putFloat(0);

			vertices.putFloat(1).putFloat(-1).putFloat(0)
                    .putFloat(1).putFloat(0);

			vertices.putFloat(-1).putFloat(1).putFloat(0)
                    .putFloat(0).putFloat(1);

			vertices.putFloat(1).putFloat(1).putFloat(0)
                    .putFloat(1).putFloat(1);

			vertices.flip();

            renderDevice.uploadAsync(vbo, vertices.unwrap());

            IFence last = null;
            for (int i = 0; i < bufs.length; i++) {

                vertices.clear();

                vertices.putFloat((float)(Math.random() * 2.0 - 1.0)).putFloat((float)(Math.random() * 2.0 - 1.0)).putFloat((float)(Math.random() * 2.0 - 1.0))
                        .putFloat(0).putFloat(0);

                vertices.putFloat((float)(Math.random() * 2.0 - 1.0)).putFloat((float)(Math.random() * 2.0 - 1.0)).putFloat((float)(Math.random() * 2.0 - 1.0))
                        .putFloat(1).putFloat(0);

                vertices.putFloat((float)(Math.random() * 2.0 - 1.0)).putFloat((float)(Math.random() * 2.0 - 1.0)).putFloat((float)(Math.random() * 2.0 - 1.0))
                        .putFloat(0).putFloat(1);

                vertices.putFloat((float)(Math.random() * 2.0 - 1.0)).putFloat((float)(Math.random() * 2.0 - 1.0)).putFloat((float)(Math.random() * 2.0 - 1.0))
                        .putFloat(1).putFloat(1);

                vertices.flip();

                log.info("generated rands for " + i);

                bufs[i] = builder.build(renderDevice.getDevice());
                last = renderDevice.uploadAsync(bufs[i], vertices.unwrap());
                log.info("uploaded " + i);
            }

            last.waitFor().destroy();

        }

		log.info("Successfully created vertex buffer");

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

	public static void recordPushConstants(VkCommandBuffer cb) {
		ShaderStage[] stages = new ShaderStage[]{ShaderStage.VERTEX, ShaderStage.FRAGMENT};

		ByteBuffer data = MemoryUtil.memAlloc(pcSize);

		data.putFloat(frameNum);
		data.putFloat((float) time);

		Vector2i swapchainExtent = swapchain.getExtent();
		data.putInt(swapchainExtent.x).putInt(swapchainExtent.y);

		Vector2d mousePosD = window.getCursorPosition();
		data.putFloat((float) mousePosD.x).putFloat((float) mousePosD.y);

		boolean leftMBPressed = window.getMouseButtonState(GlfwEnums.MouseButton.LEFT).isPressed();
		boolean rightMBPressed = window.getMouseButtonState(GlfwEnums.MouseButton.RIGHT).isPressed();
		int MBMask = 0;
		if (leftMBPressed)  MBMask |= 0b1;
		if (rightMBPressed) MBMask |= 0b10;
		data.putInt(MBMask);

		data.putFloat(window.getTotalScroll());

		data.flip();

		cb.pushConstants(
				pipeline,
				stages,
				0,
				data
		);

		MemoryUtil.memFree(data);
	}

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
        recordPushConstants(commandBuffer);
        commandBuffer.bindVertexBuffer(vbo);
        commandBuffer.draw(4, 1, 0, 0);
        for (int i = 0; i < bufs.length; i++) {
            commandBuffer.bindVertexBuffer(bufs[i]);
            commandBuffer.draw(4, 1, 0, 0);
        }
		commandBuffer.endRendering();
		commandBuffer.insertImageMemoryBarrier(bottom);
		commandBuffer.end();

	}

	public static void drawFrame() {
		drawFences[currentFrame].waitFor();

		int imageIndex = swapchain.acquireNextImage(presentCompleteSemaphores[currentFrame]);
		while(imageIndex == -1) {
			swapchain.rebuild();
			renderingInfo.setRenderArea(new VkRect2D(new Vector2i(0,0), swapchain.getExtent()));
			viewport.setWidth(swapchain.getExtent().x);
			viewport.setHeight(swapchain.getExtent().y);
			scissor.setWidth(swapchain.getExtent().x);
			scissor.setHeight(swapchain.getExtent().y);
			imageIndex = swapchain.acquireNextImage(presentCompleteSemaphores[currentFrame]);
		}


		VkImageView imageView = swapchain.getImageView(imageIndex);

		drawFences[currentFrame].reset();

		recordCmdBuffer(commandBuffers[currentFrame], imageView);

		renderDevice.getGraphicsQueue().submit(drawFences[currentFrame], new VkQueue.VkSubmitInfoBuilder()
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

	static final FiniteLog frameTimeLog = new FiniteLog(1000);

	static double time = 0;

	public static void mainLoop() {

		renderThread = getRenderThread();
		log.info("Starting render thread");
		renderThread.start();

		while (!window.shouldClose()) {
			GLFW.glfwPollEvents();
			String s = String.format("VKEngine, Frame Time: %.3fms, Total Scroll: %.1f",
					(frameTimeLog.getAverage()/1_000_000),
					window.getTotalScroll()
			);
			window.setTitle(s);
		}

		log.info("Stopping render thread");
		renderThread.interrupt();
        try {
            renderThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        renderDevice.getDevice().waitIdle();
	}

	static final float fps = 120;

	public static Thread getRenderThread() {
		final long targetFrameTimeNs = (long) (1f/fps*1000000000);
		final long rendererStart = System.nanoTime();
		return new Thread(() -> {
			long start;
			while (!Thread.currentThread().isInterrupted()) {
				start = System.nanoTime();
				time = (start - rendererStart) / 1_000_000_000.0;
				drawFrame();
				long ns = System.nanoTime()-start;
				long sleepTime = targetFrameTimeNs - ns;
				if (sleepTime > 0) {
					//try {
					//	long sleepMillis = sleepTime / 1_000_000;
					//	int sleepNanos = (int)(sleepTime % 1_000_000);
					//	Thread.sleep(sleepMillis, sleepNanos);
					//} catch (InterruptedException e) {
					//	break;
					//}
				}
				frameTimeLog.put(System.nanoTime() - start);
			}
		}, "RenderThread");
	}

	public static void clean() {

		try {
			vbo.destroy();
		} catch (Exception e) {
			log.warn("Failed to destroy VBO");
		}

		try {
			for (int i = 0; i < framesInFlight; i++) {
				drawFences[i].destroy();
				presentCompleteSemaphores[i].destroy();
			}
            for (VkBinarySemaphore renderFinishedSemaphore : renderFinishedSemaphores) {
                renderFinishedSemaphore.destroy();
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
			renderDevice.destroy();
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