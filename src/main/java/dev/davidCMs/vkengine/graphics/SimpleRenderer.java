package dev.davidCMs.vkengine.graphics;

import dev.davidCMs.vkengine.common.ColorRGBA;
import dev.davidCMs.vkengine.graphics.camera.Camera;
import dev.davidCMs.vkengine.graphics.vk.*;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SimpleRenderer extends Renderer {

    private final VkImageMemoryBarrierBuilder top = new VkImageMemoryBarrierBuilder()
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

    private final VkRenderingAttachmentInfoBuilder renderingAttachment = new VkRenderingAttachmentInfoBuilder()
            .setImageLayout(VkImageLayout.COLOR_ATTACHMENT_OPTIMAL)
            .setLoadOp(VkAttachmentLoadOp.CLEAR)
            .setStoreOp(VkAttachmentStoreOp.STORE)
            .setClearValue(new ColorRGBA(0,0,0,0));

    private final VkRenderingInfoBuilder renderingInfo = new VkRenderingInfoBuilder()
            .setLayerCount(1)
            .setColorAttachments(
                    List.of(
                            renderingAttachment
                    )
            );

    public final VkImageMemoryBarrierBuilder bottom = new VkImageMemoryBarrierBuilder()
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

    private final VkViewport viewport = new VkViewport();
    private final VkRect2D scissor = new VkRect2D();

    private final VkPipelineContext pipeline;
    private Consumer<VkCommandBuffer> pushConstantsCallBack;
    private Camera camera;
    private final List<VkBuffer> vbos = new ArrayList<>();

    public SimpleRenderer(RenderDevice renderDevice, VkPipelineContext pipeline) {
        super(renderDevice);
        this.pipeline = pipeline;
    }

    @Override
    void updateRenderArea(Vector2i newArea) {
        renderingInfo.setRenderArea(new VkRect2D(new Vector2i(0,0), newArea));
        viewport.setWidth(newArea.x);
        viewport.setHeight(newArea.y);
        scissor.setWidth(newArea.x);
        scissor.setHeight(newArea.y);
    }

    @Override
    void render(VkImageView image, VkCommandBuffer buffer) {
        top.setImage(image.image());
        renderingAttachment.setImageView(image);
        bottom.setImage(image.image());

        buffer.begin();
        buffer.insertImageMemoryBarrier(top);
        buffer.beginRendering(renderingInfo);
        buffer.bindPipeline(VkPipelineBindPoint.GRAPHICS, pipeline);
        buffer.setViewport(viewport);
        buffer.setScissor(scissor);
        if (pushConstantsCallBack != null) pushConstantsCallBack.accept(buffer);
        for (int i = 0; i < vbos.size(); i++) {
            buffer.bindVertexBuffer(vbos.get(i));
            buffer.draw(4, 1, 0, 0);
        }
        buffer.endRendering();
        buffer.insertImageMemoryBarrier(bottom);
        buffer.end();
    }

    public SimpleRenderer setPushConstantsCallBack(Consumer<VkCommandBuffer> pushConstantsCallBack) {
        this.pushConstantsCallBack = pushConstantsCallBack;
        return this;
    }

    public List<VkBuffer> getVbos() {
        return vbos;
    }

    @Override
    public void destroy() {

    }
}
