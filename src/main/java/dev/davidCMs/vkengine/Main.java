package dev.davidCMs.vkengine;

import dev.davidCMs.vkengine.common.*;
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
import dev.davidCMs.vkengine.util.FiniteLog;
import org.joml.*;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;
import dev.davidCMs.vkengine.util.IOUtils;
import dev.davidCMs.vkengine.util.LogUtils;
import dev.davidCMs.vkengine.window.GLFWUtils;
import dev.davidCMs.vkengine.window.GLFWWindow;
import dev.davidCMs.vkengine.window.GlfwEnums;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

	public static final boolean debug = true;

	private static final TaggedLogger log = Logger.tag("Main");

	static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

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
					1 * Float.BYTES	+		//mouse total scroll
			        2 * Float.BYTES +       //start Z
			        2 * Float.BYTES         //Last start Z
            ;

    public static final Vector2f startz = new Vector2f(0);
    public static final Vector2f startzLast = new Vector2f(0);

	static VkBuffer vbo;
    static VkBuffer image;

    static VkBuffer[] bufs = new VkBuffer[5];

    static VkDescriptorSetLayout descriptorSetLayout;
    static VkDescriptorPool pool;
    static VkDescriptorSet set;

	static VkBuffer uniformBuffer;
	static VkDescriptorSetLayout uniformDescriptorSetLayout;
    static VkDescriptorPool uniformPool;
    static VkDescriptorSet uniformSet;

	static Matrix4f model = new Matrix4f();
	static Matrix4f view = new Matrix4f();
	static Matrix4f projection = new Matrix4f();

	public static void main(String[] args) throws IOException {

		ObjectPool<IFence> ints = new ObjectPool<>(Fence::new);
		ObjectPool<IFence>.Lease lease = ints.get();
		lease.destroy();

		view.lookAt(new Vector3f(1, 0,-1), new Vector3f(0, 1, 0), new Vector3f(0, 0, 0));

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
		int size = 1000;
		int maxBuffSize = 1024*1024;
		Random random = new Random(System.nanoTime());
		NativeByteBuffer[] first = new NativeByteBuffer[size];
		NativeByteBuffer[] second = new NativeByteBuffer[size];
		for (int i = 0; i < size; i++) {
			int rand = random.nextInt(1, maxBuffSize);
			first[i] = NativeByteBuffer.malloc(rand);
			second[i] = NativeByteBuffer.malloc(rand);
			for (int j = 0; j < rand; j++) {
				first[i].put(j, (byte) random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE));
			}
		}

		Runnable cancer = () -> {
			boolean back = true;
			while (true) {
				for (int i = 0; i < size; i++) {
					if (back)
						second[i].copyTo(first[i]);
					else
						first[i].copyTo(second[i]);
				}
				back = !back;
			}
		};

		for (int i = 0; i < 16*2*2; i++) {
			Thread cancerSpreder = new Thread(cancer, "cancer no. " + i+1);
			cancerSpreder.start();
		}
*/
		if (true)
			init();


	}

	public static void init() {
		try {

			service.scheduleAtFixedRate(() -> {
				angle.set(new Vector3f(
						random.nextFloat() * 2f - 1f,
						random.nextFloat() * 2f - 1f,
						random.nextFloat() * 2f - 1f
				).normalize());
				log.info("Direction: " + angle.get());
			}, 0, 1, TimeUnit.SECONDS);

			log.info("Initialising window.");
			initWindow();
			log.info("Initialising vulkan.");
			initVulkan();
            glfwWindow.setVisible(true);

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
    static boolean animating = false;

	public static void initWindow() {
        glfwWindow = new GLFWWindow(800, 600, "VK Window");
        glfwWindow.addKeyCallback((window1, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_F11 && action == GLFW.GLFW_PRESS)
                glfwWindow.toggleFullScreen();
            if (key == GLFW.GLFW_KEY_F1 && action == GLFW.GLFW_PRESS) {
                window.setRenderer(rendererSet ? null : renderer);
                rendererSet = !rendererSet;
            }
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
                glfwWindow.close();

            if (action == GLFW.GLFW_REPEAT || action == GLFW.GLFW_PRESS) {
                double modMulti = 1;
                if ((mods & GLFW.GLFW_MOD_SHIFT) != 0)
                    modMulti *= 10;
                if ((mods & GLFW.GLFW_MOD_CONTROL) != 0)
                    modMulti *= 100;

                if (key == GLFW.GLFW_KEY_LEFT)
                    startz.x -= (float) (0.00001d * modMulti);
                if (key == GLFW.GLFW_KEY_RIGHT)
                    startz.x += (float) (0.00001d * modMulti);
                if (key == GLFW.GLFW_KEY_DOWN)
                    startz.y -= (float) (0.00001d * modMulti);
                if (key == GLFW.GLFW_KEY_UP)
                    startz.y += (float) (0.00001d * modMulti);

                if (startz.y >  0.5f) startz.y =  0.5f;
                if (startz.y < -0.5f) startz.y = -0.5f;
                if (startz.x >  0.5f) startz.x =  0.5f;
                if (startz.x < -0.5f) startz.x = -0.5f;
            }
            if (key == GLFW.GLFW_KEY_BACKSPACE && action == GLFW.GLFW_PRESS)
                startz.set(0);
            if (key == GLFW.GLFW_KEY_F2 && action == GLFW.GLFW_PRESS)
                animating = !animating;
        });
        glfwWindow.addFramebufferSizeCallback((window, width, height) -> {
			projection.perspective(90, (float) width/height, 0.01f, 1000, true);
        });
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
				.requiredExtensions().add(requiredExtensions).ret()
				.requiredLayers().add(enabledLayers).ret();

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
				new VkDeviceExtensionInfo()
						.wantedExtensions().add(
								VkDeviceExtension.VK_KHR_DEDICATED_ALLOCATION,
								VkDeviceExtension.VK_KHR_BIND_MEMORY_2,
								VkDeviceExtension.VK_KHR_MAINTENANCE_2,
								VkDeviceExtension.VK_KHR_MAINTENANCE_4,
								VkDeviceExtension.VK_KHR_MAINTENANCE_5,
								VkDeviceExtension.VK_EXT_MEMORY_BUDGET,
								VkDeviceExtension.VK_KHR_BUFFER_DEVICE_ADDRESS,
								VkDeviceExtension.VK_EXT_MEMORY_PRIORITY,
								VkDeviceExtension.VK_AMD_DEVICE_COHERENT_MEMORY,
								VkDeviceExtension.VK_KHR_EXTERNAL_MEMORY_WIN32,
								VkDeviceExtension.VK_EXT_SWAPCHAIN_MAINTENANCE_1,
								VkDeviceExtension.VK_EXT_SURFACE_MAINTENANCE_1
						).ret()
						.requiredExtension().add(
								VkDeviceExtension.VK_KHR_SWAPCHAIN,
								VkDeviceExtension.VK_KHR_DYNAMIC_RENDERING
						).ret()
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

		Path vertResource = Path.of("/shaders/src/main.vert");
		Path fragResource = Path.of("/shaders/src/main.frag");

		String vertSrc;
		String fragSrc;

		try {
			vertSrc = IOUtils.load(vertResource).toStringAndFree();
			fragSrc = IOUtils.load(fragResource).toStringAndFree();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		CompilationResult vertResult = shaderCompiler.compile(vertSrc, ShaderStage.VERTEX, vertResource.toString());
		CompilationResult fragResult = shaderCompiler.compile(fragSrc, ShaderStage.FRAGMENT, fragResource.toString());

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

        descriptorSetLayout = new VkDescriptorSetLayoutBuilder()
                .bindings().add(
                        new VkDescriptorSetLayoutBindingBuilder()
                                .setBinding(0)
                                .setDescriptorType(VkDescriptorType.STORAGE_BUFFER)
                                .setDescriptorCount(1)
                                .setStageFlags(Set.of(ShaderStage.FRAGMENT)),
						new VkDescriptorSetLayoutBindingBuilder()
								.setBinding(1)
								.setDescriptorType(VkDescriptorType.UNIFORM_BUFFER)
								.setDescriptorCount(1)
								.setStageFlags(Set.of(ShaderStage.VERTEX))
                ).ret()
                .build(renderDevice.getDevice());

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
						.setPrimitiveTopology(VkPrimitiveTopology.TRIANGLE_LIST)
						.setPrimitiveRestartEnable(false)
				)
				.setRasterizationState(new VkPipelineRasterizationStateBuilder()
						.setDepthClampEnable(false)
						.setRasterizerDiscardEnable(false)
						.setPolygonMode(VkPolygonMode.FILL)
						.setLineWidth(40.0f)
						.setCullMode(VkCullMode.NONE)
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
                        .setLayouts().add(descriptorSetLayout).ret()
						.pushConstantRanges().add(
								new VkPushConstantRangeBuilder()
                                        .setSize(pcSize)
                                        .setOffset(0)
										.setStageFlags(
                                                Set.of(
												    ShaderStage.FRAGMENT,
												    ShaderStage.VERTEX
                                                )
                                        )
                        ).ret()
                )
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
				.setSize(2*6*4*5*Float.BYTES)
                .usage()
                .add(
                        VkBufferUsageFlags.VERTEX_BUFFER,
                        VkBufferUsageFlags.TRANSFER_DST
                ).ret()
                .setAllocationBuilder(VmaAllocationBuilder.AUTO);

		vbo = builder.build(renderDevice.getDevice());

		try (NativeByteBuffer vertices = vbo.createPreConfiguredByteBuffer()) {
			vertices.putFloat(-1).putFloat(-1).putFloat( 1).putFloat(0).putFloat(0);
			vertices.putFloat( 1).putFloat(-1).putFloat( 1).putFloat(1).putFloat(0);
			vertices.putFloat( 1).putFloat( 1).putFloat( 1).putFloat(1).putFloat(1);

			vertices.putFloat(-1).putFloat(-1).putFloat( 1).putFloat(0).putFloat(0);
			vertices.putFloat( 1).putFloat( 1).putFloat( 1).putFloat(1).putFloat(1);
			vertices.putFloat(-1).putFloat( 1).putFloat( 1).putFloat(0).putFloat(1);


			vertices.putFloat( 1).putFloat(-1).putFloat(-1).putFloat(0).putFloat(0);
			vertices.putFloat(-1).putFloat(-1).putFloat(-1).putFloat(1).putFloat(0);
			vertices.putFloat(-1).putFloat( 1).putFloat(-1).putFloat(1).putFloat(1);

			vertices.putFloat( 1).putFloat(-1).putFloat(-1).putFloat(0).putFloat(0);
			vertices.putFloat(-1).putFloat( 1).putFloat(-1).putFloat(1).putFloat(1);
			vertices.putFloat( 1).putFloat( 1).putFloat(-1).putFloat(0).putFloat(1);


			vertices.putFloat(-1).putFloat(-1).putFloat(-1).putFloat(0).putFloat(0);
			vertices.putFloat(-1).putFloat(-1).putFloat( 1).putFloat(1).putFloat(0);
			vertices.putFloat(-1).putFloat( 1).putFloat( 1).putFloat(1).putFloat(1);

			vertices.putFloat(-1).putFloat(-1).putFloat(-1).putFloat(0).putFloat(0);
			vertices.putFloat(-1).putFloat( 1).putFloat( 1).putFloat(1).putFloat(1);
			vertices.putFloat(-1).putFloat( 1).putFloat(-1).putFloat(0).putFloat(1);


			vertices.putFloat( 1).putFloat(-1).putFloat( 1).putFloat(0).putFloat(0);
			vertices.putFloat( 1).putFloat(-1).putFloat(-1).putFloat(1).putFloat(0);
			vertices.putFloat( 1).putFloat( 1).putFloat(-1).putFloat(1).putFloat(1);

			vertices.putFloat( 1).putFloat(-1).putFloat( 1).putFloat(0).putFloat(0);
			vertices.putFloat( 1).putFloat( 1).putFloat(-1).putFloat(1).putFloat(1);
			vertices.putFloat( 1).putFloat( 1).putFloat( 1).putFloat(0).putFloat(1);


			vertices.putFloat(-1).putFloat( 1).putFloat( 1).putFloat(0).putFloat(0);
			vertices.putFloat( 1).putFloat( 1).putFloat( 1).putFloat(1).putFloat(0);
			vertices.putFloat( 1).putFloat( 1).putFloat(-1).putFloat(1).putFloat(1);

			vertices.putFloat(-1).putFloat( 1).putFloat( 1).putFloat(0).putFloat(0);
			vertices.putFloat( 1).putFloat( 1).putFloat(-1).putFloat(1).putFloat(1);
			vertices.putFloat(-1).putFloat( 1).putFloat(-1).putFloat(0).putFloat(1);


			vertices.putFloat(-1).putFloat(-1).putFloat(-1).putFloat(0).putFloat(0);
			vertices.putFloat( 1).putFloat(-1).putFloat(-1).putFloat(1).putFloat(0);
			vertices.putFloat( 1).putFloat(-1).putFloat( 1).putFloat(1).putFloat(1);

			vertices.putFloat(-1).putFloat(-1).putFloat(-1).putFloat(0).putFloat(0);
			vertices.putFloat( 1).putFloat(-1).putFloat( 1).putFloat(1).putFloat(1);
			vertices.putFloat(-1).putFloat(-1).putFloat( 1).putFloat(0).putFloat(1);

            log.info("Uploading vertices");
            renderDevice.uploadAsync(vbo, vertices);
            log.info("Done uploading vertices");
            renderer.getVbos().add(vbo);

            float[][] quadUVs = {
                    {0f, 1f},
                    {1f, 1f},
                    {0f, 0f},
                    {1f, 0f}
            };

            IFence last = null;
            for (int i = 0; i < bufs.length; i++) {

                vertices.flip();

                float fact = 1.f;

                for (int j = 0; j < 4; j++) {
                    float x,y,z,u,v;
                    x = (float) (Math.random() * 2.0 - 1.0) * fact;
                    y = (float) (Math.random() * 2.0 - 1.0) * fact;
                    z = (float) (Math.random() * 2.0 - 1.0) * fact;

                    u = quadUVs[j][0];
                    v = quadUVs[j][1];

                    vertices.putFloat(x).putFloat(y).putFloat(z)
                            .putFloat(u).putFloat(v);
                }

                bufs[i] = builder.build(renderDevice.getDevice());
                last = renderDevice.uploadAsync(bufs[i], vertices);
                //renderer.getVbos().add(bufs[i]);
            }

            last.waitFor().destroy();

        }

		log.info("Uploaded all vbos");

        Path path = Path.of("test.png");

		Image img;
		try {
			img = IOUtils.loadImage(path, 3);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		image = new VkBufferBuilder()
				.setSize(img.data().getSize())
				.usage().add(VkBufferUsageFlags.STORAGE_BUFFER, VkBufferUsageFlags.TRANSFER_DST).ret()
				.setAllocationBuilder(VmaAllocationBuilder.AUTO)
				.build(renderDevice.getDevice());

		log.info("Uploading");
		renderDevice.uploadAsync(image, img.data()).waitFor();
		log.info("Uploaded");
		img.destroy();

		uniformBuffer = new VkBufferBuilder()
				.setSize(Float.BYTES * 16 * 3)
				.usage().add(VkBufferUsageFlags.UNIFORM_BUFFER, VkBufferUsageFlags.TRANSFER_DST).ret()
				.setAllocationBuilder(VmaAllocationBuilder.AUTO)
				.build(renderDevice.getDevice());

		pool = new VkDescriptorPoolBuilder()
				.setMaxSets(1)
				.poolSizes().add(
						new VkDescriptorPoolBuilder.VkPoolSize(VkDescriptorType.STORAGE_BUFFER, 1),
						new VkDescriptorPoolBuilder.VkPoolSize(VkDescriptorType.UNIFORM_BUFFER, 1)
				).ret()
				.build(renderDevice.getDevice());
		set = pool.allocate(descriptorSetLayout);
		set.uploadBuffer(
				0,
				0,
				VkDescriptorType.STORAGE_BUFFER,
				image
		);
		set.uploadBuffer(
				1,
				0,
				VkDescriptorType.UNIFORM_BUFFER,
				uniformBuffer,
				0,
				uniformBuffer.getSize()
		);

        renderer.setPushConstantsCallBack(Main::recordPushConstants);

		log.info("Successfully created vertex buffer");

        window.setRenderer(renderer);

	}

    static long lastSW = System.nanoTime();
    final static long SWDelta = 100_000L;

	private static final Random random = new Random();

	public static final AtomicReference<Vector3f> angle = new AtomicReference<>(new Vector3f());

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

        data.putFloat(startz.x);
        data.putFloat(startz.y);

        data.putFloat(startzLast.x);
        data.putFloat(startzLast.y);

        if (System.nanoTime() - lastSW > SWDelta) {
            startzLast.set(startz);
            lastSW = System.nanoTime();
        }

		data.flip();

		cb.pushConstants(
				pipeline,
				stages,
				0,
				data
		);

        if (set != null)
            cb.bindDescriptorSets(VkPipelineBindPoint.GRAPHICS, pipeline.getPipelineLayout(), 0, set);
        else throw new RuntimeException("Failed to bind descriptor set as it is null");

        MemoryUtil.memFree(data);


		float angularSpeed = 0.0008f;
		model.rotate(angularSpeed, angle.get());

		view.identity().lookAt(new Vector3f(2, 2, 2), new Vector3f(0, 0,  0), new Vector3f(0, 1, 0));
		Vector2i extent = window.getExtent();
		float aspect = ((float)extent.x)/((float)extent.y);
		projection.identity().perspective(
                (float) Math.toRadians(45),
				aspect,
				0.1f,
				100,
				true
		);

		try (NativeByteBuffer byteBuffer = uniformBuffer.createPreConfiguredByteBuffer()) {
			float[] floats = new float[16];
			for (float f : model.get(floats)) byteBuffer.putFloat(f);
			for (float f : view.get(floats)) byteBuffer.putFloat(f);
			for (float f : projection.get(floats)) byteBuffer.putFloat(f);

			renderDevice.uploadAsync(uniformBuffer, byteBuffer).waitFor();
		}
    }

    public static long mainLoopStart;
    public static final FiniteLog updateLog = new FiniteLog(100);

	public static void mainLoop() {
		while (!glfwWindow.shouldClose()) {
            long now = System.nanoTime();
            long timeTaken = now - mainLoopStart;
            float deltaTime = timeTaken / 1_000_000_000.0f;
            mainLoopStart = now;
			GLFW.glfwPollEvents();
			String s = String.format("VKEngine, Frame Time: %.3fms, UpdateTime: %.3fms, Total Scroll: %.1f StartZ=",
					(window.getFrameTimeLog().getAverage()/1_000_000),
                    updateLog.getAverage(),
                    glfwWindow.getTotalScroll()
			);
            s = s + startz;
            glfwWindow.setTitle(s + " Frame: " + window.getFrame());

            if (animating) {
                startz.set(Math.sin(window.getTime())*.75, Math.cos(window.getTime()*0.456456)*0.5);
            }
		}
        renderDevice.getDevice().waitIdle();
	}

	public static void clean() {

		service.shutdown();

        renderDevice.getDevice().waitIdle();
        try {
            window.destroy();
        } catch (Exception e) {
            log.warn("Failed to destroy renderWindow");
        }

        try {
            pool.destroy();
        } catch (Exception e) {
            log.warn("Failed to destroy descriptor set pool");
        }

        try {
            descriptorSetLayout.destroy();
        } catch (Exception e) {
            log.warn("Failed to destroy descriptor set layout");
        }

        try {
            image.destroy();
        } catch (Exception e) {
            log.warn("Failed to destroy 'image'");
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