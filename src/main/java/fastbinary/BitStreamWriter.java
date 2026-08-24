package fastbinary;

import java.io.ByteArrayOutputStream;

/**
 * Sequential bit-level stream writer.
 * Writes arbitrary bit quantities (1..32 bits) without forcing byte alignment.
 */
public final class BitStreamWriter {

    private final ByteArrayOutputStream buffer;
    private int currentByte = 0;
    private int numBitsInCurrentByte = 0;
    private int totalBitsWritten = 0;

    /**
     * Constructs a new BitStreamWriter.
     */
    public BitStreamWriter() {
        this(64);
    }

    /**
     * Constructs a new BitStreamWriter with initial byte capacity.
     *
     * @param initialByteCapacity Initial byte buffer capacity.
     */
    public BitStreamWriter(int initialByteCapacity) {
        this.buffer = new ByteArrayOutputStream(initialByteCapacity);
    }

    /**
     * Writes a single boolean bit.
     *
     * @param bit True for 1, false for 0.
     * @return This writer instance.
     */
    public BitStreamWriter writeBit(boolean bit) {
        return writeBits(bit ? 1 : 0, 1);
    }

    /**
     * Writes arbitrary bit count (1..32 bits) of an integer value.
     *
     * @param value   Data value.
     * @param numBits Number of bits to write (1..32).
     * @return This writer instance.
     */
    public BitStreamWriter writeBits(int value, int numBits) {
        if (numBits <= 0 || numBits > 32) {
            throw new IllegalArgumentException("numBits must be between 1 and 32");
        }

        totalBitsWritten += numBits;
        while (numBits > 0) {
            int bitsCanWrite = 8 - numBitsInCurrentByte;
            int bitsToWrite = Math.min(bitsCanWrite, numBits);

            int shift = numBits - bitsToWrite;
            int chunk = (value >>> shift) & ((1 << bitsToWrite) - 1);

            currentByte |= (chunk << (bitsCanWrite - bitsToWrite));
            numBitsInCurrentByte += bitsToWrite;

            if (numBitsInCurrentByte == 8) {
                buffer.write(currentByte);
                currentByte = 0;
                numBitsInCurrentByte = 0;
            }

            numBits -= bitsToWrite;
        }
        return this;
    }

    /**
     * Flushes any remaining unwritten bits in the current byte, padding with zeros.
     *
     * @return This writer instance.
     */
    public BitStreamWriter flush() {
        if (numBitsInCurrentByte > 0) {
            buffer.write(currentByte);
            currentByte = 0;
            numBitsInCurrentByte = 0;
        }
        return this;
    }

    /**
     * Returns total number of bits written.
     *
     * @return Bit count.
     */
    public int getBitsWritten() {
        return totalBitsWritten;
    }

    /**
     * Flushes padding and returns the resulting byte array.
     *
     * @return Serialized byte array.
     */
    public byte[] toByteArray() {
        flush();
        return buffer.toByteArray();
    }
}
