package fastbinary;

/**
 * Ultra-fast Little-Endian and Big-Endian primitive encoders, decoders, and byte-swappers.
 */
public final class EndianUtil {

    private EndianUtil() {}

    /**
     * Swaps the endianness of a 16-bit short.
     *
     * @param v Short value.
     * @return Byte-swapped short.
     */
    public static short swap(short v) {
        return Short.reverseBytes(v);
    }

    /**
     * Swaps the endianness of a 32-bit integer.
     *
     * @param v Int value.
     * @return Byte-swapped int.
     */
    public static int swap(int v) {
        return Integer.reverseBytes(v);
    }

    /**
     * Swaps the endianness of a 64-bit long.
     *
     * @param v Long value.
     * @return Byte-swapped long.
     */
    public static long swap(long v) {
        return Long.reverseBytes(v);
    }

    /**
     * Reads a 16-bit short from a byte array in Little-Endian order.
     *
     * @param b      Byte array.
     * @param offset Starting offset.
     * @return Decoded short.
     */
    public static short readShortLE(byte[] b, int offset) {
        return (short) ((b[offset] & 0xFF) | ((b[offset + 1] & 0xFF) << 8));
    }

    /**
     * Reads a 32-bit integer from a byte array in Little-Endian order.
     *
     * @param b      Byte array.
     * @param offset Starting offset.
     * @return Decoded int.
     */
    public static int readIntLE(byte[] b, int offset) {
        return (b[offset] & 0xFF)
                | ((b[offset + 1] & 0xFF) << 8)
                | ((b[offset + 2] & 0xFF) << 16)
                | ((b[offset + 3] & 0xFF) << 24);
    }

    /**
     * Reads a 64-bit long from a byte array in Little-Endian order.
     *
     * @param b      Byte array.
     * @param offset Starting offset.
     * @return Decoded long.
     */
    public static long readLongLE(byte[] b, int offset) {
        return (b[offset] & 0xFFL)
                | ((b[offset + 1] & 0xFFL) << 8)
                | ((b[offset + 2] & 0xFFL) << 16)
                | ((b[offset + 3] & 0xFFL) << 24)
                | ((b[offset + 4] & 0xFFL) << 32)
                | ((b[offset + 5] & 0xFFL) << 40)
                | ((b[offset + 6] & 0xFFL) << 48)
                | ((b[offset + 7] & 0xFFL) << 56);
    }

    /**
     * Writes a 16-bit short to a byte array in Little-Endian order.
     *
     * @param v      Short value.
     * @param b      Byte array.
     * @param offset Starting offset.
     */
    public static void writeShortLE(short v, byte[] b, int offset) {
        b[offset] = (byte) v;
        b[offset + 1] = (byte) (v >>> 8);
    }

    /**
     * Writes a 32-bit integer to a byte array in Little-Endian order.
     *
     * @param v      Int value.
     * @param b      Byte array.
     * @param offset Starting offset.
     */
    public static void writeIntLE(int v, byte[] b, int offset) {
        b[offset] = (byte) v;
        b[offset + 1] = (byte) (v >>> 8);
        b[offset + 2] = (byte) (v >>> 16);
        b[offset + 3] = (byte) (v >>> 24);
    }

    /**
     * Writes a 64-bit long to a byte array in Little-Endian order.
     *
     * @param v      Long value.
     * @param b      Byte array.
     * @param offset Starting offset.
     */
    public static void writeLongLE(long v, byte[] b, int offset) {
        b[offset] = (byte) v;
        b[offset + 1] = (byte) (v >>> 8);
        b[offset + 2] = (byte) (v >>> 16);
        b[offset + 3] = (byte) (v >>> 24);
        b[offset + 4] = (byte) (v >>> 32);
        b[offset + 5] = (byte) (v >>> 40);
        b[offset + 6] = (byte) (v >>> 48);
        b[offset + 7] = (byte) (v >>> 56);
    }
}
