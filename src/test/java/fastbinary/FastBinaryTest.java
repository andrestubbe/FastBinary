package fastbinary;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;

public class FastBinaryTest {

    @Test
    public void testVarIntEncoding() {
        ByteBuffer buf = ByteBuffer.allocate(64);

        int[] testValues = {0, 1, 127, 128, 255, 300, 16383, 16384, 65535, 2097151, Integer.MAX_VALUE};
        for (int v : testValues) {
            FastBinary.writeVarInt(v, buf);
        }

        buf.flip();
        for (int v : testValues) {
            assertEquals(v, FastBinary.readVarInt(buf));
        }
    }

    @Test
    public void testZigZagSignedVarInt() {
        ByteBuffer buf = ByteBuffer.allocate(64);

        int[] signedValues = {0, -1, 1, -2, 2, -100, 100, -10000, 10000, Integer.MIN_VALUE, Integer.MAX_VALUE};
        for (int v : signedValues) {
            FastBinary.writeSignedVarInt(v, buf);
        }

        buf.flip();
        for (int v : signedValues) {
            assertEquals(v, FastBinary.readSignedVarInt(buf));
        }
    }

    @Test
    public void testBitPacking() {
        byte packedBooleans = BitPack.packBooleans(true, false, true, true, false, false, true, false);
        assertTrue(BitPack.getBit(packedBooleans, 0));
        assertFalse(BitPack.getBit(packedBooleans, 1));
        assertTrue(BitPack.getBit(packedBooleans, 2));
        assertTrue(BitPack.getBit(packedBooleans, 3));
        assertFalse(BitPack.getBit(packedBooleans, 4));
        assertFalse(BitPack.getBit(packedBooleans, 5));
        assertTrue(BitPack.getBit(packedBooleans, 6));
        assertFalse(BitPack.getBit(packedBooleans, 7));

        // Nibbles
        byte nibbles = BitPack.packNibbles(0x0A, 0x0F);
        assertEquals(0x0A, BitPack.getLowNibble(nibbles));
        assertEquals(0x0F, BitPack.getHighNibble(nibbles));

        // Custom Bitfield
        int word = 0;
        word = BitPack.setField(word, 0, 5, 31);   // 5 bits = 31
        word = BitPack.setField(word, 5, 8, 200);  // 8 bits = 200
        word = BitPack.setField(word, 13, 10, 1023); // 10 bits = 1023

        assertEquals(31, BitPack.getField(word, 0, 5));
        assertEquals(200, BitPack.getField(word, 5, 8));
        assertEquals(1023, BitPack.getField(word, 13, 10));
    }

    @Test
    public void testBitStreamStreaming() {
        BitStreamWriter writer = FastBinary.bitWriter();
        writer.writeBit(true)
                .writeBit(false)
                .writeBits(7, 3)     // 3-bit: 7 (111)
                .writeBits(15, 4)    // 4-bit: 15 (1111)
                .writeBits(500, 10)  // 10-bit: 500
                .writeBit(true);

        byte[] bytes = writer.toByteArray();
        assertNotNull(bytes);

        BitStreamReader reader = FastBinary.bitReader(bytes);
        assertTrue(reader.readBit());
        assertFalse(reader.readBit());
        assertEquals(7, reader.readBits(3));
        assertEquals(15, reader.readBits(4));
        assertEquals(500, reader.readBits(10));
        assertTrue(reader.readBit());
    }

    @Test
    public void testEndianUtilities() {
        byte[] buf = new byte[16];
        EndianUtil.writeIntLE(0x12345678, buf, 0);
        assertEquals(0x12345678, EndianUtil.readIntLE(buf, 0));

        EndianUtil.writeLongLE(0x1122334455667788L, buf, 4);
        assertEquals(0x1122334455667788L, EndianUtil.readLongLE(buf, 4));

        assertEquals(0x78563412, EndianUtil.swap(0x12345678));
    }
}
