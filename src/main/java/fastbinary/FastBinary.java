package fastbinary;

import fastcore.FastCore;

import java.nio.ByteBuffer;

/**
 * Universal, Zero-Bloat Binary Bit-Packing, VarInt Encoding &amp; Endianness Engine for FastJava.
 */
public final class FastBinary {

    static {
        try {
            FastCore.loadLibrary("fastcore");
        } catch (Throwable ignored) {}
    }

    private FastBinary() {}

    /**
     * Calculates the VarInt byte size for a 32-bit int.
     *
     * @param value 32-bit integer.
     * @return Byte length (1..5).
     */
    public static int varIntSize(int value) {
        return VarInt.sizeOf(value);
    }

    /**
     * Writes a 32-bit VarInt into a ByteBuffer.
     *
     * @param value  32-bit integer.
     * @param buffer Destination buffer.
     * @return Number of bytes written.
     */
    public static int writeVarInt(int value, ByteBuffer buffer) {
        return VarInt.write(value, buffer);
    }

    /**
     * Reads a 32-bit VarInt from a ByteBuffer.
     *
     * @param buffer Source buffer.
     * @return Decoded integer.
     */
    public static int readVarInt(ByteBuffer buffer) {
        return VarInt.readInt(buffer);
    }

    /**
     * Writes a signed 32-bit integer using ZigZag + VarInt compression.
     *
     * @param value  Signed 32-bit integer.
     * @param buffer Destination buffer.
     * @return Number of bytes written.
     */
    public static int writeSignedVarInt(int value, ByteBuffer buffer) {
        return VarInt.write(ZigZag.encode(value), buffer);
    }

    /**
     * Reads a signed 32-bit integer using VarInt + ZigZag decoding.
     *
     * @param buffer Source buffer.
     * @return Decoded signed integer.
     */
    public static int readSignedVarInt(ByteBuffer buffer) {
        return ZigZag.decode(VarInt.readInt(buffer));
    }

    /**
     * Creates a new sequential {@link BitStreamWriter}.
     *
     * @return Fresh BitStreamWriter.
     */
    public static BitStreamWriter bitWriter() {
        return new BitStreamWriter();
    }

    /**
     * Creates a new sequential {@link BitStreamWriter} with initial capacity.
     *
     * @param initialByteCapacity Initial byte capacity.
     * @return Fresh BitStreamWriter.
     */
    public static BitStreamWriter bitWriter(int initialByteCapacity) {
        return new BitStreamWriter(initialByteCapacity);
    }

    /**
     * Creates a sequential {@link BitStreamReader} wrapping a byte array.
     *
     * @param data Source byte array.
     * @return Fresh BitStreamReader.
     */
    public static BitStreamReader bitReader(byte[] data) {
        return new BitStreamReader(data);
    }
}
