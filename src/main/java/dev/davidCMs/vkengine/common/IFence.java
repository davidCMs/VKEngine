package dev.davidCMs.vkengine.common;

public interface IFence {
    IFence waitFor(long timeout);
    default IFence waitFor() {
        return waitFor(0);
    }
    boolean isSignaled();
    IFence reset();
    IFence destroy();
}
