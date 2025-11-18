package dev.davidCMs.vkengine.common;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.ReadableByteChannel;

public class NativeByteBuffer implements AutoCloseable {

    private final Object lock = new Object();
    
    private final ByteBuffer buf;
    private final int size;
    private final long address;

    private volatile boolean destroyed;

    private NativeByteBuffer(int size, ByteBuffer byteBuffer) {
        this.buf = byteBuffer;
        this.size = size;
        this.address = MemoryUtil.memAddress(byteBuffer);
    }

    public static NativeByteBuffer malloc(int size) {
        return new NativeByteBuffer(size, MemoryUtil.memAlloc(size)).order(ByteOrder.nativeOrder());
    }

    public static NativeByteBuffer malloc(int size, ByteOrder order) {
        return new NativeByteBuffer(size, MemoryUtil.memAlloc(size)).order(order);
    }

    public static NativeByteBuffer calloc(int size) {
        return new NativeByteBuffer(size, MemoryUtil.memCalloc(size)).order(ByteOrder.nativeOrder());
    }

    public static NativeByteBuffer calloc(int size, ByteOrder order) {
        return new NativeByteBuffer(size, MemoryUtil.memCalloc(size)).order(order);
    }

    public NativeByteBuffer clear() {
        synchronized (lock) {
            MemoryUtil.memSet(address, 0, size);
            return this;
        }
    }

    public int getInt(int index) {
        synchronized (lock) {
            return buf.getInt(index);
        }
    }

    public NativeByteBuffer putChar(int index, char value) {
        synchronized (lock) {
            buf.putChar(index, value);
            return this;
        }
    }

    public NativeByteBuffer putDouble(int index, double value) {
        synchronized (lock) {
            buf.putDouble(index, value);
            return this;
        }
    }

    public byte get(int index) {
        synchronized (lock) {
            return buf.get(index);
        }
    }

    public long getLong(int index) {
        synchronized (lock) {
        return buf.getLong(index);
        }
    }

    public NativeByteBuffer putShort(int index, short value) {
        synchronized (lock) {
            buf.putShort(index, value);
            return this;
        }
    }

    public NativeByteBuffer putInt(int index, int value) {
        synchronized (lock) {
            buf.putInt(index, value);
            return this;
        }
    }

    private NativeByteBuffer order(@NotNull ByteOrder bo) {
        synchronized (lock) {
            buf.order(bo);
            return this;
        }
    }

    public float getFloat(int index) {
        synchronized (lock) {
            return buf.getFloat(index);
        }
    }

    public NativeByteBuffer putLong(int index, long value) {
        synchronized (lock) {
            buf.putLong(index, value);
            return this;
        }
    }

    public char getChar(int index) {
        synchronized (lock) {
            return buf.getChar(index);
        }
    }

    public ByteOrder order() {
        synchronized (lock) {
            return buf.order();
        }
    }

    public double getDouble(int index) {
        synchronized (lock) {
            return buf.getDouble(index);
        }
    }

    public NativeByteBuffer putFloat(int index, float value) {
        synchronized (lock) {
            buf.putFloat(index, value);
            return this;
        }
    }
    public short getShort(int index) {
        synchronized (lock) {
            return buf.getShort(index);
        }
    }

    public NativeByteBuffer put(int index, byte b) {
        synchronized (lock) {
            buf.put(index, b);
            return this;
        }
    }

    public NativeByteBuffer put(int index, byte[] src) {
        synchronized (lock) {
            buf.put(index, src);
            return this;
        }
    }

    @Override
    public String toString() {
        synchronized (lock) {
            return buf.toString();
        }
    }

    @Override
    public int hashCode() {
        synchronized (lock) {
            return buf.hashCode();
        }
    }

    @Override
    public boolean equals(Object ob) {
        synchronized (lock) {
            if (ob instanceof NativeByteBuffer cast)
                return buf.equals(cast.buf);
            return buf.equals(ob);
        }
    }

    public boolean isDestroyed() {
        synchronized (lock) {
            return destroyed;
        }
    }

    public int getSize() {
        return size;
    }

    public void copyTo(NativeByteBuffer buffer) {
        copyTo(buffer, size);
    }

    public void copyTo(NativeByteBuffer buffer, long size) {
        synchronized (buffer.lock) {
            copyTo(buffer.address, size);
        }
    }

    public void copyTo(long address) {
        copyTo(address, size);
    }

    public void copyTo(long address, long size) {
        synchronized (lock) {
            if (destroyed) throw new RuntimeException("Tried copying buffer when it was already destroyed");
            MemoryUtil.memCopy(this.address, address, size);
        }
    }

    public int readFrom(ReadableByteChannel channel) throws IOException {
        synchronized (lock) {
            return channel.read(buf);
        }
    }

    public long getAddress() {
        return address;
    }

    @Override
    public void close() {
        synchronized (lock) {
            this.destroyed = true;
            MemoryUtil.memFree(buf);
        }
    }
}
