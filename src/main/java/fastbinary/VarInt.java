package fastbinary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * High-performance LEB128 variable-length integer (VarInt &amp; VarLong) encoder and decoder.
 * Compresses small numbers into 1..5 bytes (for 32-bit ints) and 1..10 bytes (for 64-bit longs).
 */
public final class VarInt {

    private VarInt() {}

    /**
     * Calculates the number of bytes required to encode a 32-bit integer as a VarInt.
     *
     * @param value 32-bit integer value.
     * @return Byte length (1..5).
     */
    public static int sizeOf(int value) {
        if ((value & (0xFFFFFFFF << 7)) == 0) return 1;
        if ((value & (0xFFFFFFFF << 14)) == 0) return 2;
        if ((value & (0xFFFFFFFF << 21)) == 0) return 3;
        if ((value & (0xFFFFFFFF << 28)) == 0) return 4;
        return 5;
    }

    /**
     * Calculates the number of bytes required to encode a 64-bit long as a VarLong.
     *
     * @param value 64-bit long value.
     * @return Byte length (1..10).
     */
    public static int sizeOf(long value) {
        int count = 0;
        do {
            count++;
            value >>>= 7;
        } while (value != 0);
        return count;
    }

    /**
     * Encodes a 32-bit integer into a destination byte array.
     *
     * @param value  32-bit integer value.
     * @param dest   Destination byte array.
     * @param offset Starting array offset.
     * @return Number of bytes written.
     */
    public static int write(int value, byte[] dest, int offset) {
        int pos = offset;
        while ((value & ~0x7F) != 0) {
            dest[pos++] = (byte) ((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        dest[pos++] = (byte) (value & 0x7F);
        return pos - offset;
    }

    /**
     * Encodes a 32-bit integer into a {@link ByteBuffer}.
     *
     * @param value  32-bit integer value.
     * @param buffer Destination buffer.
     * @return Number of bytes written.
     */
    public static int write(int value, ByteBuffer buffer) {
        int written = 0;
        while ((value & ~0x7F) != 0) {
            buffer.put((byte) ((value & 0x7F) | 0x80));
            value >>>= 7;
            written++;
        }
        buffer.put((byte) (value & 0x7F));
        return written + 1;
    }

    /**
     * Encodes a 32-bit integer to an {@link OutputStream}.
     *
     * @param value 32-bit integer value.
     * @param out   Destination stream.
     * @return Number of bytes written.
     * @throws IOException If write fails.
     */
    public static int write(int value, OutputStream out) throws IOException {
        int written = 0;
        while ((value & ~0x7F) != 0) {
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
            written++;
        }
        out.write(value & 0x7F);
        return written + 1;
    }

    /**
     * Decodes a 32-bit VarInt from a {@link ByteBuffer}.
     *
     * @param buffer Source buffer.
     * @return Decoded integer value.
     */
    public static int readInt(ByteBuffer buffer) {
        int result = 0;
        int shift = 0;
        while (shift < 35) {
            byte b = buffer.get();
            result |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
        }
        throw new IllegalArgumentException("Malformed VarInt: exceeds 5 bytes");
    }

    /**
     * Decodes a 32-bit VarInt from an {@link InputStream}.
     *
     * @param in Source input stream.
     * @return Decoded integer value.
     * @throws IOException If read fails or stream reaches EOF prematurely.
     */
    public static int readInt(InputStream in) throws IOException {
        int result = 0;
        int shift = 0;
        while (shift < 35) {
            int b = in.read();
            if (b == -1) throw new IOException("Premature EOF while reading VarInt");
            result |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
        }
        throw new IllegalArgumentException("Malformed VarInt: exceeds 5 bytes");
    }

    /**
     * Encodes a 64-bit long into a {@link ByteBuffer}.
     *
     * @param value  64-bit long value.
     * @param buffer Destination buffer.
     * @return Number of bytes written.
     */
    public static int writeLong(long value, ByteBuffer buffer) {
        int written = 0;
        while ((value & ~0x7FL) != 0) {
            buffer.put((byte) ((value & 0x7F) | 0x80));
            value >>>= 7;
            written++;
        }
        buffer.put((byte) (value & 0x7F));
        return written + 1;
    }

    /**
     * Decodes a 64-bit VarLong from a {@link ByteBuffer}.
     *
     * @param buffer Source buffer.
     * @return Decoded 64-bit long.
     */
    public static long readLong(ByteBuffer buffer) {
        long result = 0;
        int shift = 0;
        while (shift < 70) {
            byte b = buffer.get();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
        }
        throw new IllegalArgumentException("Malformed VarLong: exceeds 10 bytes");
    }
}
