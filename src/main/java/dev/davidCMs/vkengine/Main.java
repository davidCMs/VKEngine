package dev.davidCMs.vkengine;

import dev.davidCMs.vkengine.common.IFence;
import dev.davidCMs.vkengine.graphics.RenderDevice;
import dev.davidCMs.vkengine.graphics.RenderableWindow;
import dev.davidCMs.vkengine.graphics.SimpleRenderer;
import dev.davidCMs.vkengine.graphics.shader.*;
import dev.davidCMs.vkengine.graphics.vk.*;
import dev.davidCMs.vkengine.graphics.vk.VkCommandBuffer;
import dev.davidCMs.vkengine.graphics.vk.VkPhysicalDevice;
import dev.davidCMs.vkengine.graphics.vk.VkQueue;
import dev.davidCMs.vkengine.graphics.vk.VkVertexInputAttributeDescription;
import dev.davidCMs.vkengine.graphics.vk.VkVertexInputBindingDescription;
import dev.davidCMs.vkengine.graphics.vma.VmaAllocationBuilder;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;
import dev.davidCMs.vkengine.common.NativeByteBuffer;
import dev.davidCMs.vkengine.common.ColorRGBA;
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
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

	public static final boolean debug = true;

	private static final TaggedLogger log = Logger.tag("Main");
	static GLFWErrorCallback errorCallback;
    static GLFWWindow glfwWindow;
    static RenderableWindow window;

    static SimpleRenderer renderer;

	static VkInstanceContext instance;

	static RenderDevice renderDevice;
	static ShaderCompiler shaderCompiler;
	static VkPipelineContext pipeline;

	public static int pcSize =
					1 * Float.BYTES + 		//frameNum
					1 * Float.BYTES + 		//time
					2 * Integer.BYTES +		//swapchain extent
					2 * Float.BYTES + 		//mouse pos
					1 * Integer.BYTES +		//mouse button mask
					1 * Float.BYTES			//mouse total scroll
			;

	static VkBuffer vbo;
    static VkBuffer image;

    static VkBuffer[] bufs = new VkBuffer[100];

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

    static boolean rendererSet = true;

	public static void initWindow() {
        glfwWindow = new GLFWWindow(800, 600, "VK Window");
        glfwWindow.addKeyCallback((window1, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_F11 && action == GLFW.GLFW_PRESS)
                glfwWindow.toggleFullScreen();
            if (key == GLFW.GLFW_KEY_F1 && action == GLFW.GLFW_PRESS) {
                window.setRenderer(rendererSet ? null : renderer);
                rendererSet = !rendererSet;
            }
        });
        //window.addFramebufferSizeCallback((window, width, height) -> {
        //    log.info("Window changed size! (" + width + ", " + height + ")");
        //});
        glfwWindow.setVisible(true);
        //window.toggleFullScreen();
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

        long sTemp = glfwWindow.getVkSurface(instance);
        VkPhysicalDevice physicalDevice = RenderDevice.pickBestDevice(instance, sTemp);
        KHRSurface.vkDestroySurfaceKHR(instance.instance(), sTemp, null);

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
                EXTMemoryPriority.VK_EXT_MEMORY_PRIORITY_EXTENSION_NAME,
                EXTSwapchainMaintenance1.VK_EXT_SWAPCHAIN_MAINTENANCE_1_EXTENSION_NAME
                //EXTSurfaceMaintenance1.VK_EXT_SURFACE_MAINTENANCE_1_EXTENSION_NAME
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
                        .setSwapchainMaintenance1(true)
                        .setMemoryPriority(true),
                wantedExtensions
        );

        VkQueueFamily graphicsFamily = renderDevice.getGraphicsQueue().getQueueFamily();

        log.info("Found a suitable GPU:           {}", physicalDevice.getInfo().properties().deviceName());
        log.info("Graphics queue family index is: {}", graphicsFamily.getIndex());
        int transFam = 1;
        for (VkQueue queue : renderDevice.getTransferQueues())
            log.info("transfer {} queue family index is: {}", transFam++, queue.getQueueFamily().getIndex());


        log.info("Selected device memory info:  \n{}", LogUtils.beautify(physicalDevice.getInfo().memoryProperties()));
        //log.info("Selected device properties: \n{}", LogUtils.beautify(physicalDevice.properties()));

		window = new RenderableWindow(renderDevice, glfwWindow);

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
						.setLineWidth(5.0f)
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
										window.getFormat()
								)
						)
				)
				;

		pipeline = pipelineBuilder.newContext(renderDevice.getDevice());
		vertShaderModule.destroy();
		fragShaderModule.destroy();

		log.info("Successfully created graphics pipeline");

        renderer = new SimpleRenderer(renderDevice, pipeline);

		VkBufferBuilder builder = new VkBufferBuilder()
				.setSize(4*5*Float.BYTES)
                .usage()
                .add(
                        VkBufferUsageFlags.VERTEX_BUFFER,
                        VkBufferUsageFlags.TRANSFER_DST
                ).ret()
                .setAllocationBuilder(VmaAllocationBuilder.DEVICE);

		vbo = builder.build(renderDevice.getDevice());

		try (NativeByteBuffer vertices = vbo.createPreConfiguredByteBuffer()) {
            int n = 0;
			vertices.putFloat(n++ * Float.BYTES, -1).putFloat(n++ * Float.BYTES, -1).putFloat(n++ * Float.BYTES, 0)
                    .putFloat(n++ * Float.BYTES, 0).putFloat(n++ * Float.BYTES, 0);

            vertices.putFloat(n++ * Float.BYTES, 1).putFloat(n++ * Float.BYTES, -1).putFloat(n++ * Float.BYTES, 0)
                    .putFloat(n++ * Float.BYTES, 1).putFloat(n++ * Float.BYTES, 0);

            vertices.putFloat(n++ * Float.BYTES, -1).putFloat(n++ * Float.BYTES, 1).putFloat(n++ * Float.BYTES, 0)
                    .putFloat(n++ * Float.BYTES, 0).putFloat(n++ * Float.BYTES, 1);

            vertices.putFloat(n++ * Float.BYTES, 1).putFloat(n++ * Float.BYTES, 1).putFloat(n++ * Float.BYTES, 0)
                    .putFloat(n++ * Float.BYTES, 1).putFloat(n++ * Float.BYTES, 1);

            renderDevice.uploadAsync(vbo, vertices);
            renderer.getVbos().add(vbo);

            float[][] quadUVs = {
                    {0f, 1f},
                    {1f, 1f},
                    {0f, 0f},
                    {1f, 0f}
            };

            IFence last = null;
            for (int i = 0; i < bufs.length; i++) {

                vertices.clear();

                float fact = 1.f;

                for (int j = 0; j < 4; j++) {
                    float x,y,z,u,v;
                    x = (float) (Math.random() * 2.0 - 1.0) * fact;
                    y = (float) (Math.random() * 2.0 - 1.0) * fact;
                    z = (float) (Math.random() * 2.0 - 1.0) * fact;

                    u = quadUVs[j][0];
                    v = quadUVs[j][1];

                    vertices.putFloat(0, x).putFloat(1, y).putFloat(2, z)
                            .putFloat(3, u).putFloat(4, v);
                }

                bufs[i] = builder.build(renderDevice.getDevice());
                last = renderDevice.uploadAsync(bufs[i], vertices);
                //renderer.getVbos().add(bufs[i]);
            }

            last.waitFor().destroy();

        }

        Path path = Path.of("/home/davidcms/Pictures/VRChat/2025-07/test.rgb");
        FileChannel fileChannel;
        long fileSize;
        try {
            fileChannel = FileChannel.open(path, StandardOpenOption.READ);
            fileSize = Files.size(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VkBuffer imageBuffer = new VkBufferBuilder()
                .setSize(fileSize)
                .usage().add(VkBufferUsageFlags.UNIFORM_BUFFER, VkBufferUsageFlags.TRANSFER_DST).ret()
                .setAllocationBuilder(VmaAllocationBuilder.DEVICE)
                .build(renderDevice.getDevice());

        try (NativeByteBuffer buf = imageBuffer.createPreConfiguredByteBuffer()) {
            while (buf.readFrom(fileChannel) > 1);
            fileChannel.close();
            log.info("Uploading");
            renderDevice.uploadAsync(imageBuffer, buf).waitFor();
            log.info("Uploaded");
            Thread.sleep(100);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        renderer.setPushConstantsCallBack(Main::recordPushConstants);

		log.info("Successfully created vertex buffer");

        window.setRenderer(renderer);

	}

	public static void recordPushConstants(VkCommandBuffer cb) {
		ShaderStage[] stages = new ShaderStage[]{ShaderStage.VERTEX, ShaderStage.FRAGMENT};

		ByteBuffer data = MemoryUtil.memAlloc(pcSize);

		data.putFloat(window.getFrame());
		data.putFloat((float) window.getTime());

		Vector2i swapchainExtent = window.getExtent();
		data.putInt(swapchainExtent.x).putInt(swapchainExtent.y);

		Vector2d mousePosD = glfwWindow.getCursorPosition();
		data.putFloat((float) mousePosD.x).putFloat((float) mousePosD.y);

		boolean leftMBPressed = glfwWindow.getMouseButtonState(GlfwEnums.MouseButton.LEFT).isPressed();
		boolean rightMBPressed = glfwWindow.getMouseButtonState(GlfwEnums.MouseButton.RIGHT).isPressed();
		int MBMask = 0;
		if (leftMBPressed)  MBMask |= 0b1;
		if (rightMBPressed) MBMask |= 0b10;
		data.putInt(MBMask);

		data.putFloat(glfwWindow.getTotalScroll());

		data.flip();

		cb.pushConstants(
				pipeline,
				stages,
				0,
				data
		);

		MemoryUtil.memFree(data);
	}

	public static void mainLoop() {
		while (!glfwWindow.shouldClose()) {
			GLFW.glfwPollEvents();
			String s = String.format("VKEngine, Frame Time: %.3fms, Total Scroll: %.1f",
					(window.getFrameTimeLog().getAverage()/1_000_000),
                    glfwWindow.getTotalScroll()
			);
            glfwWindow.setTitle(s + "Frame: " + window.getFrame());
		}
        renderDevice.getDevice().waitIdle();
	}

	public static void clean() {
        renderDevice.getDevice().waitIdle();
        try {
            window.destroy();
        } catch (Exception e) {
            log.warn("Failed to destroy renderWindow");
        }

		try {
			vbo.destroy();
            for (VkBuffer buf : bufs) {
                buf.destroy();
            }
		} catch (Exception e) {
			log.warn("Failed to destroy VBO");
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
			renderDevice.destroy();
		} catch (Exception e) {
			log.warn("Failed to destroy device");
		}

		try {
			instance.destroy();
		} catch (Exception e) {
			log.warn("Failed to destroy instance");
		}

		try {
			glfwWindow.close();
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