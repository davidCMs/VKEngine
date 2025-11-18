package dev.davidCMs.vkengine.common;

public interface IFence extends Destroyable {
    IFence waitFor(long timeout);
    default IFence waitFor() {
        return waitFor(-1);
    }
    boolean isSignaled();
    IFence reset();
}
