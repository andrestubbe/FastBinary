package fastbinary;

/**
 * Sequential bit-level stream reader.
 * Reads arbitrary bit quantities (1..32 bits) across a byte array.
 */
public final class BitStreamReader {

    private final byte[] data;
    private int byteOffset = 0;
    private int bitOffset = 0;
    private final int totalBits;

    /**
     * Constructs a BitStreamReader wrapping a byte array.
     *
     * @param data Byte array source.
     */
    public BitStreamReader(byte[] data) {
        this.data = data != null ? data : new byte[0];
        this.totalBits = this.data.length * 8;
    }

    /**
     * Reads a single bit as a boolean.
     *
     * @return True for 1, false for 0.
     */
    public boolean readBit() {
        return readBits(1) == 1;
    }

    /**
     * Reads arbitrary bit count (1..32 bits) from the stream.
     *
     * @param numBits Number of bits to read (1..32).
     * @return Unsigned integer containing the requested bits.
     */
    public int readBits(int numBits) {
        if (numBits <= 0 || numBits > 32) {
            throw new IllegalArgumentException("numBits must be between 1 and 32");
        }
        if (remainingBits() < numBits) {
            throw new IllegalStateException("Bit underflow: requested " + numBits + " bits but only " + remainingBits() + " remaining");
        }

        int result = 0;
        while (numBits > 0) {
            int bitsCanRead = 8 - bitOffset;
            int bitsToRead = Math.min(bitsCanRead, numBits);

            int currentByte = data[byteOffset] & 0xFF;
            int shift = bitsCanRead - bitsToRead;
            int chunk = (currentByte >>> shift) & ((1 << bitsToRead) - 1);

            result = (result << bitsToRead) | chunk;
            bitOffset += bitsToRead;

            if (bitOffset == 8) {
                byteOffset++;
                bitOffset = 0;
            }

            numBits -= bitsToRead;
        }
        return result;
    }

    /**
     * Returns the remaining unread bits in the stream.
     *
     * @return Number of unread bits.
     */
    public int remainingBits() {
        return totalBits - (byteOffset * 8 + bitOffset);
    }
}
