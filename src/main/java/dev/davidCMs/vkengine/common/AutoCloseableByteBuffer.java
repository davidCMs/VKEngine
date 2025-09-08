package dev.davidCMs.vkengine.common;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import java.nio.*;

public class AutoCloseableByteBuffer implements AutoCloseable {

    private final ByteBuffer buf;

    public AutoCloseableByteBuffer(int size) {
        this.buf = MemoryUtil.memAlloc(size);
    }

    public int getInt() {
        return buf.getInt();
    }

    public ByteBuffer get(int index, byte[] dst, int offset, int length) {
        return buf.get(index, dst, offset, length);
    }

    public AutoCloseableByteBuffer putFloat(float value) {
        buf.putFloat(value);
        return this;
    }

    public AutoCloseableByteBuffer slice(int index, int length) {
        buf.slice(index, length);
        return this;
    }

    public int getInt(int index) {
        return buf.getInt(index);
    }

    public AutoCloseableByteBuffer alignedSlice(int unitSize) {
        buf.alignedSlice(unitSize);
        return this;
    }

    public int limit() {
        return buf.limit();
    }

    public AutoCloseableByteBuffer asReadOnlyBuffer() {
        buf.asReadOnlyBuffer();
        return this;
    }

    public AutoCloseableByteBuffer put(byte[] src) {
        buf.put(src);
        return this;
    }

    public AutoCloseableByteBuffer putChar(int index, char value) {
        buf.putChar(index, value);
        return this;
    }

    public AutoCloseableByteBuffer putDouble(int index, double value) {
        buf.putDouble(index, value);
        return this;
    }

    public LongBuffer asLongBuffer() {
        return buf.asLongBuffer();
    }

    public AutoCloseableByteBuffer position(int newPosition) {
        buf.position(newPosition);
        return this;
    }

    public AutoCloseableByteBuffer putChar(char value) {
        buf.putChar(value);
        return this;
    }

    public AutoCloseableByteBuffer reset() {
        buf.reset();
        return this;
    }

    public AutoCloseableByteBuffer putDouble(double value) {
        buf.putDouble(value);
        return this;
    }

    public byte get(int index) {
        return buf.get(index);
    }

    public int mismatch(ByteBuffer that) {
        return buf.mismatch(that);
    }

    public AutoCloseableByteBuffer put(int index, byte[] src, int offset, int length) {
        buf.put(index, src, offset, length);
        return this;
    }

    public CharBuffer asCharBuffer() {
        return buf.asCharBuffer();
    }

    public byte get() {
        return buf.get();
    }

    public long getLong(int index) {
        return buf.getLong(index);
    }

    public AutoCloseableByteBuffer compact() {
        buf.compact();
        return this;
    }

    public AutoCloseableByteBuffer putShort(int index, short value) {
        buf.putShort(index, value);
        return this;
    }

    public AutoCloseableByteBuffer putInt(int index, int value) {
        buf.putInt(index, value);
        return this;
    }

    public int capacity() {
        return buf.capacity();
    }

    public AutoCloseableByteBuffer get(byte[] dst, int offset, int length) {
        buf.get(dst, offset, length);
        return this;
    }

    public AutoCloseableByteBuffer put(ByteBuffer src) {
        buf.put(src);
        return this;
    }

    public AutoCloseableByteBuffer order(@NotNull ByteOrder bo) {
        buf.order(bo);
        return this;
    }

    public FloatBuffer asFloatBuffer() {
        return buf.asFloatBuffer();
    }

    public boolean hasRemaining() {
        return buf.hasRemaining();
    }

    public AutoCloseableByteBuffer flip() {
        buf.flip();
        return this;
    }

    public AutoCloseableByteBuffer putShort(short value) {
        buf.putShort(value);
        return this;
    }

    public long getLong() {
        return buf.getLong();
    }

    public boolean isReadOnly() {
        return buf.isReadOnly();
    }

    public AutoCloseableByteBuffer slice() {
        buf.slice();
        return this;
    }

    public AutoCloseableByteBuffer putInt(int value) {
        buf.putInt(value);
        return this;
    }

    public char getChar() {
        return buf.getChar();
    }

    public ShortBuffer asShortBuffer() {
        return buf.asShortBuffer();
    }

    public float getFloat(int index) {
        return buf.getFloat(index);
    }

    public AutoCloseableByteBuffer get(byte[] dst) {
        buf.get(dst);
        return this;
    }

    public boolean isDirect() {
        return buf.isDirect();
    }

    public AutoCloseableByteBuffer get(int index, byte[] dst) {
        buf.get(index, dst);
        return this;
    }

    public AutoCloseableByteBuffer limit(int newLimit) {
        buf.limit(newLimit);
        return this;
    }

    public AutoCloseableByteBuffer putLong(int index, long value) {
        buf.putLong(index, value);
        return this;
    }

    public char getChar(int index) {
        return buf.getChar(index);
    }

    public AutoCloseableByteBuffer duplicate() {
        buf.duplicate();
        return this;
    }

    public DoubleBuffer asDoubleBuffer() {
        return buf.asDoubleBuffer();
    }

    public AutoCloseableByteBuffer put(byte[] src, int offset, int length) {
        buf.put(src, offset, length);
        return this;
    }

    public float getFloat() {
        return buf.getFloat();
    }

    public ByteOrder order() {
        return buf.order();
    }

    public double getDouble() {
        return buf.getDouble();
    }

    public AutoCloseableByteBuffer clear() {
        buf.clear();
        return this;
    }

    public short getShort() {
        return buf.getShort();
    }

    public AutoCloseableByteBuffer putLong(long value) {
        buf.putLong(value);
        return this;
    }

    public AutoCloseableByteBuffer mark() {
        buf.mark();
        return this;
    }

    public double getDouble(int index) {
        return buf.getDouble(index);
    }

    public AutoCloseableByteBuffer put(byte b) {
        buf.put(b);
        return this;
    }

    public AutoCloseableByteBuffer put(int index, ByteBuffer src, int offset, int length) {
        buf.put(index, src, offset, length);
        return this;
    }

    public AutoCloseableByteBuffer putFloat(int index, float value) {
        buf.putFloat(index, value);
        return this;
    }

    public AutoCloseableByteBuffer rewind() {
        buf.rewind();
        return this;
    }

    public int alignmentOffset(int index, int unitSize) {
        return buf.alignmentOffset(index, unitSize);
    }

    public int remaining() {
        return buf.remaining();
    }

    public short getShort(int index) {
        return buf.getShort(index);
    }

    public int position() {
        return buf.position();
    }

    public AutoCloseableByteBuffer put(int index, byte b) {
        buf.put(index, b);
        return this;
    }

    public IntBuffer asIntBuffer() {
        return buf.asIntBuffer();
    }

    public AutoCloseableByteBuffer put(int index, byte[] src) {
        buf.put(index, src);
        return this;
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    @Override
    public int hashCode() {
        return buf.hashCode();
    }

    @Override
    public boolean equals(Object ob) {
        return buf.equals(ob);
    }

    public int compareTo(ByteBuffer that) {
        return buf.compareTo(that);
    }

    public ByteBuffer unwrap() {
        return buf;
    }

    @Override
    public void close() {
        MemoryUtil.memFree(buf);
    }
}
