package dev.davidCMs.vkengine.common;

public interface ISignalableFence extends IFence {
    IFence signal();
}
