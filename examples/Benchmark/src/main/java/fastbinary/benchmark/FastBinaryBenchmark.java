package fastbinary.benchmark;

import fastbinary.BitPack;
import fastbinary.BitStreamReader;
import fastbinary.BitStreamWriter;
import fastbinary.FastBinary;
import fastbinary.VarInt;
import fastbinary.ZigZag;
import org.openjdk.jmh.annotations.*;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class FastBinaryBenchmark {

    private ByteBuffer varIntBuffer;
    private byte[] bitStreamBytes;

    @Setup
    public void setup() {
        varIntBuffer = ByteBuffer.allocate(1024);
        for (int i = 0; i < 200; i++) {
            FastBinary.writeVarInt(i * 15, varIntBuffer);
        }

        BitStreamWriter writer = FastBinary.bitWriter();
        for (int i = 0; i < 50; i++) {
            writer.writeBits(i % 16, 4);
            writer.writeBit((i & 1) == 0);
        }
        bitStreamBytes = writer.toByteArray();
    }

    @Benchmark
    public int benchmarkVarIntEncode() {
        ByteBuffer buf = ByteBuffer.allocate(16);
        return FastBinary.writeVarInt(123456, buf);
    }

    @Benchmark
    public int benchmarkZigZagEncode() {
        return ZigZag.encode(-123456);
    }

    @Benchmark
    public int benchmarkBitPackField() {
        int word = 0;
        word = BitPack.setField(word, 0, 5, 25);
        word = BitPack.setField(word, 5, 8, 180);
        return BitPack.getField(word, 5, 8);
    }

    @Benchmark
    public int benchmarkBitStreamRead() {
        BitStreamReader reader = FastBinary.bitReader(bitStreamBytes);
        int sum = 0;
        while (reader.remainingBits() >= 5) {
            sum += reader.readBits(4);
            sum += reader.readBit() ? 1 : 0;
        }
        return sum;
    }
}
