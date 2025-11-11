package dev.davidCMs.vkengine.common;

import java.util.Collection;
import java.util.List;

public interface Destroyable {
    void destroy();

    static void destroy(Destroyable destroyable) {
        if (destroyable == null) return;
        destroyable.destroy();
    }

    static void destroy(Destroyable[] destroyables) {
        if (destroyables == null) return;
        for (int i = 0; i < destroyables.length; i++) {
            destroyables[i].destroy();
        }
    }

    static void destroy(List<Destroyable> destroyables) {
        if (destroyables == null) return;
        for (int i = 0; i < destroyables.size(); i++) {
            destroyables.get(i).destroy();
        }
    }

    static void destroy(Collection<Destroyable> destroyables) {
        if (destroyables == null) return;
        for (Destroyable destroyable : destroyables) {
            destroyable.destroy();
        }
    }

}
