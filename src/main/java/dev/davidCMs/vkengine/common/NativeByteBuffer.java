package dev.davidCMs.vkengine.common;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public class NativeByteBuffer implements AutoCloseable {

    private final Object lock = new Object();

    private final ByteBuffer buf;
    private final int size;
    private final long address;

    private boolean destroyed;

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

    public static NativeByteBuffer wrap(ByteBuffer buf) {
        if (!buf.isDirect()) throw new IllegalArgumentException("Buf must be a direct buffer");
        return new NativeByteBuffer(buf.capacity(), buf);
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

    public NativeByteBuffer mark() {
        synchronized (lock) {
            buf.mark();
            return this;
        }
    }

    public NativeByteBuffer put(byte b) {
        synchronized (lock) {
            buf.put(b);
            return this;
        }
    }

    public short getShort() {
        synchronized (lock) {
            return buf.getShort();
        }
    }

    public NativeByteBuffer putFloat(float value) {
        synchronized (lock) {
            buf.putFloat(value);
            return this;
        }
    }

    public NativeByteBuffer putChar(char value) {
        synchronized (lock) {
            buf.putChar(value);
            return this;
        }
    }

    public int getInt() {
        synchronized (lock) {
            return buf.getInt();
        }
    }

    public NativeByteBuffer position(int newPosition) {
        synchronized (lock) {
            buf.position(newPosition);
            return this;
        }
    }

    public NativeByteBuffer compact() {
        synchronized (lock) {
            buf.compact();
            return this;
        }
    }

    public NativeByteBuffer putDouble(double value) {
        synchronized (lock) {
            buf.putDouble(value);
            return this;
        }
    }

    public NativeByteBuffer flip() {
        synchronized (lock) {
            buf.flip();
            return this;
        }
    }

    public NativeByteBuffer putShort(short value) {
        synchronized (lock) {
            buf.putShort(value);
            return this;
        }
    }

    public boolean isReadOnly() {
        synchronized (lock) {
            return buf.isReadOnly();
        }
    }

    public NativeByteBuffer put(ByteBuffer src) {
        synchronized (lock) {
            buf.put(src);
            return this;
        }
    }

    public long getLong() {
        synchronized (lock) {
            return buf.getLong();
        }
    }

    public int position() {
        synchronized (lock) {
            return buf.position();
        }
    }

    public int remaining() {
        synchronized (lock) {
            return buf.remaining();
        }
    }

    public int capacity() {
        synchronized (lock) {
            return buf.capacity();
        }
    }

    public byte get() {
        synchronized (lock) {
            return buf.get();
        }
    }

    public NativeByteBuffer limit(int newLimit) {
        synchronized (lock) {
            buf.limit(newLimit);
            return this;
        }
    }

    public NativeByteBuffer reset() {
        synchronized (lock) {
            buf.reset();
            return this;
        }
    }

    public int mismatch(ByteBuffer that) {
        synchronized (lock) {
            return buf.mismatch(that);
        }
    }

    public NativeByteBuffer putInt(int value) {
        synchronized (lock) {
            buf.putInt(value);
            return this;
        }
    }

    public float getFloat() {
        synchronized (lock) {
            return buf.getFloat();
        }
    }

    public int compareTo(ByteBuffer that) {
        synchronized (lock) {
            return buf.compareTo(that);
        }
    }

    public int limit() {
        synchronized (lock) {
            return buf.limit();
        }
    }

    public char getChar() {
        synchronized (lock) {
            return buf.getChar();
        }
    }

    public NativeByteBuffer putLong(long value) {
        synchronized (lock) {
            buf.putLong(value);
            return this;
        }
    }

    public NativeByteBuffer put(int index, ByteBuffer src, int offset, int length) {
        synchronized (lock) {
            buf.put(index, src, offset, length);
            return this;
        }
    }

    public double getDouble() {
        synchronized (lock) {
            return buf.getDouble();
        }
    }

    public boolean hasRemaining() {
        synchronized (lock) {
            return buf.hasRemaining();
        }
    }

    public NativeByteBuffer rewind() {
        synchronized (lock) {
            buf.rewind();
            return this;
        }
    }

    public ByteBuffer asReadOnlyBuffer() {
        return buf.asReadOnlyBuffer().order(order());
    }

    @Override
    public String toString() {
        synchronized (lock) {
            ByteBuffer tmp = buf.duplicate();
            return StandardCharsets.UTF_8.decode(tmp).toString();
        }
    }

    public String toStringAndFree() {
        String s = toString();
        close();
        return s;
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

    public void copyTo(NativeByteBuffer dst, long size) {
        NativeByteBuffer first = this.address < dst.address ? this : dst;
        NativeByteBuffer second = first == this ? dst : this;

        synchronized (first.lock) {
            synchronized (second.lock) {
                if (this.destroyed) throw new IllegalStateException("Source already destroyed");
                if (dst.destroyed) throw new IllegalStateException("Destination already destroyed");
                MemoryUtil.memCopy(this.address, dst.address, size);
            }
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
            if (destroyed) return;
            this.destroyed = true;
            MemoryUtil.memFree(buf);
        }
    }
}
