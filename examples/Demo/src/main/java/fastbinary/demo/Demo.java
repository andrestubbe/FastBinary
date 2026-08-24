package fastbinary.demo;

import fastbinary.BitPack;
import fastbinary.BitStreamReader;
import fastbinary.BitStreamWriter;
import fastbinary.FastBinary;
import fastbinary.VarInt;
import fastbinary.ZigZag;

import java.nio.ByteBuffer;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("⚡ FastBinary 0.1.0 — Bit-Packing & VarInt Engine");
        System.out.println("=================================================\n");

        // 1. LEB128 VarInt Compression
        System.out.println("📦 [1/3] LEB128 VarInt Compression vs Raw 4-Byte Int:");
        int[] numbers = {0, 42, 127, 300, 16384, 1000000};
        ByteBuffer buf = ByteBuffer.allocate(64);
        for (int n : numbers) {
            int size = VarInt.sizeOf(n);
            FastBinary.writeVarInt(n, buf);
            System.out.printf("  Value: %-8d -> VarInt size: %d byte(s) (Saved: %d%%)\n",
                    n, size, (4 - size) * 25);
        }
        System.out.println();

        // 2. ZigZag Signed Integer Compression
        System.out.println("🔀 [2/3] ZigZag Signed Integer Mapping:");
        int[] signed = {0, -1, 1, -2, 2, -100, 100, -5000};
        for (int s : signed) {
            int encoded = ZigZag.encode(s);
            int decoded = ZigZag.decode(encoded);
            System.out.printf("  Signed: %-6d -> ZigZag Unsigned: %-6d (VarInt size: %d bytes) -> Decoded: %d\n",
                    s, encoded, VarInt.sizeOf(encoded), decoded);
        }
        System.out.println();

        // 3. Bit-Level Stream Packing
        System.out.println("🎯 [3/3] Bit-Level Stream Packing (Sub-Byte Precision):");
        BitStreamWriter writer = FastBinary.bitWriter();
        writer.writeBit(true)            // 1 bit
                .writeBits(3, 2)         // 2-bit value (0..3)
                .writeBits(14, 4)        // 4-bit nibble (0..15)
                .writeBits(750, 10)      // 10-bit integer (0..1023)
                .writeBit(false);        // 1 bit

        byte[] bitBytes = writer.toByteArray();
        System.out.printf("  Written 18 total bits into %d bytes (vs 20 bytes if using full ints/longs!)\n", bitBytes.length);

        BitStreamReader reader = FastBinary.bitReader(bitBytes);
        System.out.println("  Read Bit 1: " + reader.readBit());
        System.out.println("  Read 2-bit: " + reader.readBits(2));
        System.out.println("  Read 4-bit: " + reader.readBits(4));
        System.out.println("  Read 10-bit: " + reader.readBits(10));
        System.out.println("  Read Bit 5: " + reader.readBit());

        System.out.println("\n✅ FastBinary Showcase Completed Successfully!");
    }
}
