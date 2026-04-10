package dev.davidCMs.vkengine.util;

import java.util.concurrent.atomic.AtomicInteger;

public class FiniteLog {

    private final double[] arr;
    private final AtomicInteger index = new AtomicInteger(0);
    private final AtomicInteger size = new AtomicInteger(0);

    public FiniteLog(int maxSize) {
        arr = new double[maxSize];
    }

    public void put(double val) {
        int i = index.getAndIncrement();
        int pos = i % arr.length;

        arr[pos] = val;

        size.updateAndGet(s -> Math.min(s + 1, arr.length));
    }

    public double getAverage() {
        int sizeLocal = size.get();

        if (sizeLocal == 0) return 0;

        double sum = 0;
        for (int i = 0; i < sizeLocal; i++) {
            sum += arr[i];
        }

        return sum / sizeLocal;
    }
}