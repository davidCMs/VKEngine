package org.davidCMs.vkengine.util;

import java.util.concurrent.atomic.AtomicInteger;

public class FiniteLog {

    private final double[] arr;
    private AtomicInteger index = new AtomicInteger(0);
    private int max = 1;

    public FiniteLog(int maxSize) {
        arr = new double[maxSize];
    }

    public void put(double val) {
        int i = index.addAndGet(1);
        i %= max;

        max = Math.max(i, max);
        arr[i] = val;
    }

    public double getAverage() {
        int maxLocal = max;

        double sum = 0;
        for (int i = 0; i < maxLocal; i++) {
            sum += arr[i];
        }

        return sum/maxLocal;
    }



}
