package fastbinary;

/**
 * ZigZag encoding maps signed integers to unsigned integers so that numbers with
 * small absolute values (like -1, -2, 1, 2) have small positive representations,
 * enabling optimal VarInt compression for signed numbers.
 *
 * Mapping:
 *  0  -> 0
 * -1  -> 1
 *  1  -> 2
 * -2  -> 3
 *  2  -> 4
 */
public final class ZigZag {

    private ZigZag() {}

    /**
     * Encodes a 32-bit signed integer using ZigZag encoding.
     *
     * @param n Signed 32-bit integer.
     * @return ZigZag encoded unsigned integer.
     */
    public static int encode(int n) {
        return (n << 1) ^ (n >> 31);
    }

    /**
     * Decodes a 32-bit ZigZag encoded integer back to its signed value.
     *
     * @param n ZigZag encoded unsigned integer.
     * @return Decoded signed integer.
     */
    public static int decode(int n) {
        return (n >>> 1) ^ -(n & 1);
    }

    /**
     * Encodes a 64-bit signed long using ZigZag encoding.
     *
     * @param n Signed 64-bit long.
     * @return ZigZag encoded unsigned long.
     */
    public static long encode(long n) {
        return (n << 1) ^ (n >> 63);
    }

    /**
     * Decodes a 64-bit ZigZag encoded long back to its signed value.
     *
     * @param n ZigZag encoded unsigned long.
     * @return Decoded signed long.
     */
    public static long decode(long n) {
        return (n >>> 1) ^ -(n & 1L);
    }
}
