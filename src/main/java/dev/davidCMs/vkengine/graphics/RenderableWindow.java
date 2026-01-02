package dev.davidCMs.vkengine.graphics;

import dev.davidCMs.vkengine.common.Destroyable;
import dev.davidCMs.vkengine.graphics.vk.*;
import dev.davidCMs.vkengine.util.FiniteLog;
import dev.davidCMs.vkengine.window.GLFWWindow;
import org.joml.Vector2i;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RenderableWindow implements Destroyable {

    private static final long RESIZE_DELAY = 5_000_000L;
    private static final int RESIZE_DELAY_SKIP_DELTA = 60;
    private static final TaggedLogger log = Logger.tag("Graphics");

    private final GLFWWindow glfwWindow;
    private final VkSurface surface;
    private final RenderDevice device;
    private final VkQueue presentQueue;
    private final VkCommandPool pool;
    private final Thread renderThread;

    private final ReentrantReadWriteLock renderLock = new ReentrantReadWriteLock(); //ALWAYS LOCK LAST DO AVOID DEAD LOCK
    private final ReentrantReadWriteLock propertiesLock = new ReentrantReadWriteLock(); //ALWAYS LOCK FIRST DO AVOID DEAD LOCK
    private final Object rendererNullLock = new Object();

    private final VkSwapchainBuilder swapchainBuilder;
    private final AtomicReference<VkSwapchain> swapchain = new AtomicReference<>();

    private volatile int framesInFlight = 388;
    private final Vector2i recentGLFWExtent = new Vector2i();
    private final Vector2i currentExtent = new Vector2i();
    private volatile long lastResize = 0;

    private int frame;
    private int currentFrame;
    private volatile double time = 0;

    private final AtomicReference<VkBinarySemaphore[]> presentCompleteSemaphores = new AtomicReference<>();
    private final AtomicReference<VkBinarySemaphore[]> renderFinishedSemaphores = new AtomicReference<>();
    private final AtomicReference<VkFence[]> inFlightFences = new AtomicReference<>();
    private final AtomicReference<VkFence[]> renderFinishedFences = new AtomicReference<>();
    private final AtomicReference<VkFence[]> imagesInFlight = new AtomicReference<>();
    private final AtomicReference<VkCommandBuffer[]> commandBuffers = new AtomicReference<>();

    private final AtomicReference<Renderer> renderer = new AtomicReference<>();

    private final FiniteLog frameTimeLog = new FiniteLog(1000);

    public RenderableWindow(RenderDevice device, GLFWWindow glfwWindow) {
        this.glfwWindow = glfwWindow;
        glfwWindow.addFramebufferSizeCallback((_, newWidth, newHeight) -> {
            recentGLFWExtent.set(newWidth, newHeight);
            lastResize= System.nanoTime();
        });
        surface = new VkGLFWSurface(device.getDevice().physicalDevice(), glfwWindow);
        this.device = device;
        this.presentQueue = device.getPresentQueue(surface);
        this.pool = device.getGraphicsQueue().getQueueFamily().createCommandPool(device.getDevice(), VkCommandPoolCreateFlags.RESET_COMMAND_BUFFER);

        this.currentExtent.set(glfwWindow.getFrameBufferSize());
        this.recentGLFWExtent.set(this.currentExtent);

        swapchainBuilder = new VkSwapchainBuilder(device.getDevice());
        initSwapchainBuilder();
        rebuildSwapchain(true);
        setFramesInFlight(this.framesInFlight);

        this.renderThread = createRenderThread();
        renderThread.start();
    }

    //not threadsafe
    private void initSwapchainBuilder() {
        VkSurfaceInfo info = surface.getSurfaceInfo();
        swapchainBuilder.setSurface(surface);
        swapchainBuilder.setClipped(false);
        swapchainBuilder.setImageArrayLayers(1);
        swapchainBuilder.setSurfaceTransform(info.capabilities().currentTransform());

        int desiredImages = info.presentModes().contains(VkPresentMode.MAILBOX) ? 3 : 2;
        int imagesCount = Math.max(desiredImages, info.capabilities().minImageCount());
        swapchainBuilder.setMinImageCount(imagesCount);
        swapchainBuilder.setPresentMode(info.presentModes().contains(VkPresentMode.MAILBOX) ? VkPresentMode.MAILBOX : VkPresentMode.FIFO);

        if (info.capabilities().supportedCompositeAlpha().contains(VkCompositeAlpha.POST_MULTIPLIED))
            swapchainBuilder.setCompositeAlpha(VkCompositeAlpha.POST_MULTIPLIED);
        else if (info.capabilities().supportedCompositeAlpha().contains(VkCompositeAlpha.PRE_MULTIPLIED))
            swapchainBuilder.setCompositeAlpha(VkCompositeAlpha.PRE_MULTIPLIED);
        else if (info.capabilities().supportedCompositeAlpha().contains(VkCompositeAlpha.INHERIT))
            swapchainBuilder.setCompositeAlpha(VkCompositeAlpha.INHERIT);
        else if (info.capabilities().supportedCompositeAlpha().contains(VkCompositeAlpha.OPAQUE))
            swapchainBuilder.setCompositeAlpha(VkCompositeAlpha.OPAQUE);
        else throw new RuntimeException("WHAT THE FUCKY?!?!?!!?");

        boolean alphaSRGB = false;
        boolean opaqueSRGB = false;
        boolean ABGR = false;
        boolean OBGR = false;

        for (VkSurfaceInfo.SurfaceFormat format : info.formats()) {
            if (format.colorSpace() != VkImageColorSpace.SRGB_NONLINEAR) continue;
            if (format.format() == VkFormat.R8G8B8A8_SRGB) {
                alphaSRGB = true;
                break;
            } else if (format.format() == VkFormat.R8G8B8_SRGB) {
                opaqueSRGB = true;
                break;
            } else if (format.format() == VkFormat.B8G8R8A8_SRGB) {
                ABGR = true;
                break;
            } else if (format.format() == VkFormat.B8G8R8_SRGB) {
                OBGR = true;
                break;
            }
        }

        if (!(alphaSRGB || opaqueSRGB || ABGR || OBGR))
            throw new RuntimeException("The surface provided by your operating system is retarded and i am to retarded to work around it");

        swapchainBuilder.setImageColorSpace(VkImageColorSpace.SRGB_NONLINEAR);

        if (alphaSRGB)
            swapchainBuilder.setImageFormat(VkFormat.R8G8B8A8_SRGB);
        else if (opaqueSRGB) {
            swapchainBuilder.setImageFormat(VkFormat.R8G8B8_SRGB);
        } else if (ABGR) {
            swapchainBuilder.setImageFormat(VkFormat.B8G8R8A8_SRGB);
        } else if (OBGR) {
            swapchainBuilder.setImageFormat(VkFormat.B8G8R8_SRGB);
        }

        if (info.capabilities().currentExtent().x == 0xFFFFFFFF && info.capabilities().currentExtent().y == 0xFFFFFFFF)
            swapchainBuilder.setImageExtent(currentExtent);
        else
            swapchainBuilder.setImageExtent(info.capabilities().currentExtent());

        if (info.capabilities().supportedUsageFlags().contains(VkImageUsage.COLOR_ATTACHMENT))
            swapchainBuilder.imageUsage().add(VkImageUsage.COLOR_ATTACHMENT);
        else
            throw new RuntimeException("The surface provided by your operating system is retarded and i am to retarded to work around it");
    }

    private void rebuildSwapchain(boolean force) {
        propertiesLock.writeLock().lock();
        try {
            VkSurfaceInfo info = surface.getSurfaceInfo();
            if (!(info.capabilities().currentExtent().x == 0xFFFFFFFF && info.capabilities().currentExtent().y == 0xFFFFFFFF)) {
                if (!recentGLFWExtent.equals(info.capabilities().currentExtent())) return;
            }

            if (!force) {
                if (currentExtent.equals(recentGLFWExtent)) return;
            }

            log.info("Rebuilding swapchain");
            swapchainBuilder.setImageExtent(glfwWindow.getFrameBufferSize());
            VkSwapchain newSwapchain = swapchainBuilder.create(swapchain.get());

            log.info("Waiting on lock");
            renderLock.writeLock().lock();
            try {
                log.info("Lock acquired Waiting on renderer to stop");
                waitForRendererIdle();
                log.info("Renderer stopped");
                Destroyable.destroy(swapchain.getAndSet(newSwapchain));

                currentExtent.set(swapchainBuilder.getImageExtent());

                if (newSwapchain.getImageCount() != swapchain.get().getImageCount() || force)
                    imagesInFlight.set(new VkFence[newSwapchain.getImageCount()]);
                else {
                    Arrays.fill(imagesInFlight.get(), null);
                    reset();
                }
                log.info("Swapchain rebuilt, new size = [width=" + currentExtent.x + ", height=" + currentExtent.y + "]");
            } finally {
                renderLock.writeLock().unlock();
            }
        } finally {
            propertiesLock.writeLock().unlock();
        }
    }

    private VkCommandBuffer[] newCommandBuffers(int framesInFlight) {
        return pool.createCommandBuffer(framesInFlight);
    }

    //not thread safe
    private VkBinarySemaphore[] newPresentCompleteSemaphores(int framesInFlight) {
        VkBinarySemaphore[] semaphores = new VkBinarySemaphore[framesInFlight];
        for (int i = 0; i < framesInFlight; i++)
            semaphores[i] = new VkBinarySemaphore(device.getDevice());
        return semaphores;
    }

    //not thread safe
    private VkBinarySemaphore[] newRenderFinishedSemaphores(int framesInFlight) {
        VkBinarySemaphore[] semaphores = new VkBinarySemaphore[framesInFlight];
        for (int i = 0; i < framesInFlight; i++)
            semaphores[i] = new VkBinarySemaphore(device.getDevice());
        return semaphores;
    }

    //not thread safe
    private VkFence[] newInFlightFences(int framesInFlight) {
        VkFence[] fences = new VkFence[framesInFlight];
        for (int i = 0; i < framesInFlight; i++)
            fences[i] = new VkFence(device.getDevice(), true);
        return fences;
    }

    private VkFence[] newRenderFinishedFences(int framesInFlight) {
        VkFence[] fences = new VkFence[framesInFlight];
        for (int i = 0; i < framesInFlight; i++)
            fences[i] = new VkFence(device.getDevice(), true);
        return fences;
    }

    //unsafe!
    private void reset() {
        this.currentFrame = 0;
    }

    private void waitForRendererIdle() {
        waitForRendererIdle(-1);
    }

    private void waitForRendererIdle(long timeout) {
        if (inFlightFences.get() == null) return;
        for (VkFence fence : inFlightFences.get()) {
            fence.waitFor(timeout);
        }
        if (renderFinishedFences.get() == null) return;
        for (VkFence fence : renderFinishedFences.get()) {
            fence.waitFor(timeout);
        }
    }

    public void setFramesInFlight(int newFramesInFlight) {
        propertiesLock.writeLock().lock();
        try {
            VkBinarySemaphore[] newPresentCompleteSemaphores = newPresentCompleteSemaphores(newFramesInFlight);
            VkFence[] newInFlightFences = newInFlightFences(newFramesInFlight);
            VkBinarySemaphore[] newRenderFinishedSemaphores = newRenderFinishedSemaphores(newFramesInFlight);
            VkFence[] newRenderFinishedFences = newRenderFinishedFences(newFramesInFlight);
            renderLock.writeLock().lock();
            try {
                waitForRendererIdle();
                Destroyable.destroy(presentCompleteSemaphores.getAndSet(newPresentCompleteSemaphores));
                Destroyable.destroy(inFlightFences.getAndSet(newInFlightFences));
                Destroyable.destroy(renderFinishedSemaphores.getAndSet(newRenderFinishedSemaphores));
                Destroyable.destroy(renderFinishedFences.getAndSet(newRenderFinishedFences));
                commandBuffers.set(newCommandBuffers(newFramesInFlight));
                this.framesInFlight = newFramesInFlight;
                reset();
            } finally {
                renderLock.writeLock().unlock();
            }
        } finally {
            propertiesLock.writeLock().unlock();
        }
    }

    private Thread createRenderThread() {
        //final long targetFrameTimeNs = (long) (1f/fps*1000000000);
        final long rendererStart = System.nanoTime();
        return new Thread(() -> {
            long start;
            boolean needsRebuild = false;
            boolean outOfDate = false;
            while (!Thread.currentThread().isInterrupted()) {
                Renderer renderer;

                synchronized (rendererNullLock) {
                    while ((renderer = this.renderer.get()) == null) {
                        log.info("Renderer is null sleeping");
                        try {
                            rendererNullLock.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                        log.info("Awoke");
                    }
                }

                renderLock.readLock().lock();
                try {
                    start = System.nanoTime();
                    time = (start - rendererStart) / 1_000_000_000.0;
                    if (!currentExtent.equals(recentGLFWExtent)) {
                        if (Math.abs((currentExtent.x + currentExtent.y) - (recentGLFWExtent.x + recentGLFWExtent.y)) > RESIZE_DELAY_SKIP_DELTA)
                            needsRebuild = true;
                        else if (start - lastResize > RESIZE_DELAY)
                            needsRebuild = true;
                    }

                    VkFence frameFence = inFlightFences.get()[currentFrame];
                    frameFence.waitFor();
                    frameFence.reset();

                    int imageIndex = swapchain.get().acquireNextImage(presentCompleteSemaphores.get()[currentFrame]);
                    if (imageIndex == -1) {
                        rebuildSwapchain(true);
                        continue;
                    }

                    VkImageView imageView = swapchain.get().getImageView(imageIndex);

                    renderer.updateRenderArea(currentExtent);
                    renderer.render(imageView, commandBuffers.get()[currentFrame].reset());

                    device.getGraphicsQueue().submit(inFlightFences.get()[currentFrame], new VkQueue.VkSubmitInfoBuilder()
                            .setWaitSemaphores(
                                    new VkQueue.VkSubmitInfoBuilder.VkSemaphoreSubmitInfo(
                                            presentCompleteSemaphores.get()[currentFrame], VkPipelineStage.COLOR_ATTACHMENT_OUTPUT
                                    )
                            )
                            .setCommandBuffers(commandBuffers.get()[currentFrame])
                            .setSignalSemaphores(
                                    new VkQueue.VkSubmitInfoBuilder.VkSemaphoreSubmitInfo(
                                            renderFinishedSemaphores.get()[currentFrame], VkPipelineStage.COLOR_ATTACHMENT_OUTPUT
                                    )
                            ));


                    VkFence renderFinishedFence = renderFinishedFences.get()[currentFrame];
                    renderFinishedFence.waitFor();
                    renderFinishedFence.reset();

                    outOfDate = presentQueue.present(
                            renderFinishedFence,
                            renderFinishedSemaphores.get()[currentFrame],
                            swapchain.get(),
                            imageIndex);

                    frame++;
                    currentFrame = frame % framesInFlight;
                    frameTimeLog.put(System.nanoTime() - start);
                } finally {
                    renderLock.readLock().unlock();
                }
                if (outOfDate) {
                    rebuildSwapchain(true);
                    continue;
                }

                if (needsRebuild) {
                    rebuildSwapchain(false);
                    needsRebuild = false;
                }
            }
        }, "WindowRenderThread");
    }

    public void setRenderer(Renderer renderer) {
        log.info("Setting new renderer " + (renderer == null ? "null" : renderer.getClass()));
        renderLock.writeLock().lock();
        try {
            this.renderer.set(renderer);
        } finally {
            renderLock.writeLock().unlock();
        }
        synchronized (rendererNullLock) {
            rendererNullLock.notifyAll();
        }
    }

    public Renderer getRenderer() {
        renderLock.readLock().lock();
        try {
            return renderer.get();
        } finally {
            renderLock.readLock().unlock();
        }
    }

    @Override
    public void destroy() {
        renderThread.interrupt();
        try {
            renderThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        waitForRendererIdle();

        swapchain.get().destroy();
        Destroyable.destroy(presentCompleteSemaphores.get());
        Destroyable.destroy(renderFinishedSemaphores.get());
        Destroyable.destroy(inFlightFences.get());
        Destroyable.destroy(renderFinishedFences.get());
        pool.destroy();
        surface.destroy();

    }

    public GLFWWindow getGlfwWindow() {
        return glfwWindow;
    }

    public VkFormat getFormat() {
        propertiesLock.readLock().lock();
        try {
            return swapchainBuilder.getImageFormat();
        } finally {
            propertiesLock.readLock().unlock();
        }
    }

    public int getFrame() {
        propertiesLock.readLock().lock();
        try {
            return frame;
        } finally {
            propertiesLock.readLock().unlock();
        }
    }

    public Vector2i getExtent() {
        propertiesLock.readLock().lock();
        try {
            return new Vector2i(currentExtent);
        } finally {
            propertiesLock.readLock().unlock();
        }
    }

    public double getTime() {
        return time;
    }

    public FiniteLog getFrameTimeLog() {
        return frameTimeLog;
    }
}
