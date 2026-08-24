package fastbinary;

/**
 * Ultra-fast bit-packing utilities for packing booleans, nibbles (4-bit), 2-bit flags,
 * and arbitrary sub-byte integer fields into primitive 32-bit and 64-bit words.
 */
public final class BitPack {

    private BitPack() {}

    /**
     * Packs up to 8 boolean flags into a single byte.
     *
     * @param b0 Bit 0.
     * @param b1 Bit 1.
     * @param b2 Bit 2.
     * @param b3 Bit 3.
     * @param b4 Bit 4.
     * @param b5 Bit 5.
     * @param b6 Bit 6.
     * @param b7 Bit 7.
     * @return Packed byte (0..255).
     */
    public static byte packBooleans(boolean b0, boolean b1, boolean b2, boolean b3,
                                    boolean b4, boolean b5, boolean b6, boolean b7) {
        int v = (b0 ? 1 : 0)
                | (b1 ? 2 : 0)
                | (b2 ? 4 : 0)
                | (b3 ? 8 : 0)
                | (b4 ? 16 : 0)
                | (b5 ? 32 : 0)
                | (b6 ? 64 : 0)
                | (b7 ? 128 : 0);
        return (byte) v;
    }

    /**
     * Checks whether a specific bit index is set in a packed integer.
     *
     * @param word     Packed integer.
     * @param bitIndex Bit index (0..31).
     * @return True if bit is 1.
     */
    public static boolean getBit(int word, int bitIndex) {
        return ((word >>> bitIndex) & 1) != 0;
    }

    /**
     * Sets or clears a specific bit index in a packed integer.
     *
     * @param word     Original integer word.
     * @param bitIndex Bit index (0..31).
     * @param value    Boolean state.
     * @return Modified word.
     */
    public static int setBit(int word, int bitIndex, boolean value) {
        if (value) {
            return word | (1 << bitIndex);
        } else {
            return word & ~(1 << bitIndex);
        }
    }

    /**
     * Packs two 4-bit nibbles (0..15) into a single 8-bit byte.
     *
     * @param lowNibble  Lower 4 bits.
     * @param highNibble Upper 4 bits.
     * @return Packed byte.
     */
    public static byte packNibbles(int lowNibble, int highNibble) {
        return (byte) ((lowNibble & 0x0F) | ((highNibble & 0x0F) << 4));
    }

    /**
     * Extracts the lower 4-bit nibble from a byte.
     *
     * @param b Source byte.
     * @return Low nibble (0..15).
     */
    public static int getLowNibble(byte b) {
        return b & 0x0F;
    }

    /**
     * Extracts the upper 4-bit nibble from a byte.
     *
     * @param b Source byte.
     * @return High nibble (0..15).
     */
    public static int getHighNibble(byte b) {
        return (b >>> 4) & 0x0F;
    }

    /**
     * Packs four 2-bit values (0..3) into a single byte.
     *
     * @param v0 Value 0.
     * @param v1 Value 1.
     * @param v2 Value 2.
     * @param v3 Value 3.
     * @return Packed byte.
     */
    public static byte pack2Bit(int v0, int v1, int v2, int v3) {
        return (byte) ((v0 & 0x03) | ((v1 & 0x03) << 2) | ((v2 & 0x03) << 4) | ((v3 & 0x03) << 6));
    }

    /**
     * Extracts a custom bitfield of arbitrary width (1..32 bits) from an integer.
     *
     * @param word     Source integer word.
     * @param offset   Starting bit offset (0..31).
     * @param numBits  Bitfield width (1..32).
     * @return Unsigned extracted field.
     */
    public static int getField(int word, int offset, int numBits) {
        int mask = (numBits == 32) ? 0xFFFFFFFF : ((1 << numBits) - 1);
        return (word >>> offset) & mask;
    }

    /**
     * Sets a custom bitfield of arbitrary width into an integer word.
     *
     * @param word     Original integer word.
     * @param offset   Starting bit offset.
     * @param numBits  Bitfield width.
     * @param value    Value to store in the field.
     * @return Modified word.
     */
    public static int setField(int word, int offset, int numBits, int value) {
        int mask = (numBits == 32) ? 0xFFFFFFFF : ((1 << numBits) - 1);
        word &= ~(mask << offset);
        word |= (value & mask) << offset;
        return word;
    }
}
