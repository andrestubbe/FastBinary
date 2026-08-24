# FastBinary 0.1.0 [ALPHA-2026-08-24] — High-Performance Bit-Packing, VarInt Encoding & Endianness Engine for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastBinary/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Cross--Platform-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastBinary)

---

**⚡ Universal, zero-bloat binary bit-packing, VarInt encoding, and endianness utilities for the FastJava ecosystem.**

**FastBinary** is a low-level, high-throughput primitive encoding toolkit. It provides **LEB128 variable-length integer compression (`VarInt` / `VarLong`)**, **ZigZag signed number mapping**, **sub-byte bitfield manipulation (`BitPack`)**, **sequential bit streams (`BitStream`)**, and **zero-allocation endianness operations (`EndianUtil`)**.

---

## Quick Start

### 1. LEB128 VarInt & ZigZag Signed Compression
```java
import fastbinary.FastBinary;
import fastbinary.VarInt;
import fastbinary.ZigZag;
import java.nio.ByteBuffer;

public class VarIntDemo {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(64);

        // 1. Unsigned VarInt (1..5 bytes)
        FastBinary.writeVarInt(42, buffer);     // 1 byte
        FastBinary.writeVarInt(16384, buffer);  // 3 bytes

        // 2. Signed VarInt with ZigZag Compression
        FastBinary.writeSignedVarInt(-1, buffer); // Maps -1 -> 1, takes only 1 byte!

        buffer.flip();
        int val1 = FastBinary.readVarInt(buffer);       // 42
        int val2 = FastBinary.readVarInt(buffer);       // 16384
        int signedVal = FastBinary.readSignedVarInt(buffer); // -1
    }
}
```

### 2. Sub-Byte Bit Packing & Bit Streams
```java
import fastbinary.BitPack;
import fastbinary.BitStreamReader;
import fastbinary.BitStreamWriter;
import fastbinary.FastBinary;

public class BitDemo {
    public static void main(String[] args) {
        // 1. Pack 8 booleans into 1 byte
        byte flags = BitPack.packBooleans(true, false, true, true, false, false, true, false);

        // 2. Pack sub-byte integers (e.g. 5-bit, 10-bit) into a raw bitstream
        BitStreamWriter writer = FastBinary.bitWriter();
        writer.writeBit(true)
              .writeBits(14, 4)     // 4-bit nibble (14)
              .writeBits(750, 10);  // 10-bit integer (750)

        byte[] payload = writer.toByteArray();

        BitStreamReader reader = FastBinary.bitReader(payload);
        boolean bit1 = reader.readBit();
        int nibble = reader.readBits(4);
        int tenBit = reader.readBits(10);
    }
}
```

---

## Table of Contents

- [Why FastBinary?](#why-fastbinary)
- [Quick Start](#quick-start)
- [Key Features](#key-features)
- [Real-World Scenarios](#real-world-scenarios)
- [Performance Benchmarks](#performance-benchmarks)
- [API Quick Reference](#api-quick-reference)
- [Technical Examples & Hero Demos](#technical-examples--hero-demos)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastBinary?

Modern network protocols, game engines, and vector stores waste millions of bytes transmitting padded 32-bit and 64-bit primitive types when values are small or boolean in nature:

1. **Massive Bandwidth & Memory Waste**  
   Standard integers occupy 4 full bytes even for values like `0`, `1`, or `42`.
2. **GC Allocations in High-Frequency Packet Serialization**  
   Typical BitSet or bitfield libraries create wrapper objects on heap during hot encoding loops.
3. **Complex Third-Party Dependencies**  
   Pulling in Google Protobuf or Apache Commons just for LEB128 VarInt or bit-twiddling introduces heavy jar bloat.

**FastBinary solves this with pure, zero-allocation primitive utilities:**
- **Zero Heap Allocations**: Functions operate directly on primitive registers, arrays, and `ByteBuffer` instances.
- **Maximum Density**: Sub-byte bitstream packing and ZigZag signed VarInt compression shrink payloads by up to 75%.

---

## Key Features

- **⚡ LEB128 VarInt & VarLong** — Standard variable-length integer compression (1–5 bytes for `int`, 1–10 bytes for `long`).
- **🔀 ZigZag Encoding** — Optimal signed integer compression mapping negative numbers to small positive numbers.
- **🎯 Bit-Level Streams (`BitStream`)** — Write and read arbitrary bit widths (1..32 bits) across raw byte arrays without byte padding.
- **📦 Sub-Byte Bitfield Packing (`BitPack`)** — Pack booleans, nibbles (4-bit), 2-bit flags, and custom bitfields into primitive words.
- **🔄 Endianness Utilities (`EndianUtil`)** — Zero-allocation Little-Endian and Big-Endian integer conversions and byte-swapping.
- **🌐 Zero Dependencies** — Self-contained pure Java 17+ core backed by `FastCore`.

---

## Real-World Scenarios

- **💾 FastFileFormat & Binary Serializers** — Low-level primitive compression for headers, chunk offsets, and variable arrays.
- **🎮 Multiplayer Game State & Telemetry** — Packing 1-bit flags, 4-bit entity states, and 10-bit rotation angles into minimal network packets.
- **📊 Time-Series & Sensor Data Compression** — High-density delta-of-delta compression for telemetry timestamps and numeric counters.
- **🧠 Vector Databases & AI Embeddings** — Compressing sparse indexes, quantization flags, and bitmasks in `FastAIVectorDB`.

---

## Performance Benchmarks

FastBinary is profiled using **JMH** to guarantee zero-allocation execution.

| Benchmark Operation | Score (ops/ms) | Ops per Second | Memory Allocation |
|---|---|---|---|
| **BitPack Field Set & Get** | **~2,193,000 ops/ms** | **> 2.19 Billion** | **0 bytes / op (Zero GC)** |
| **ZigZag Mapping** | **~1,297,000 ops/ms** | **> 1.29 Billion** | **0 bytes / op (Zero GC)** |
| **VarInt Encoding** | **~159,000 ops/ms** | **> 159 Million** | **0 bytes / op (Zero GC)** |
| **BitStream Sequential Read** | **~2,694 ops/ms** | **> 2.69 Million** | **0 bytes / op (Zero GC)** |

*Run the benchmarks locally:* `.\run-benchmark.bat`

---

## API Quick Reference

| Class / Method | Description |
|---|---|
| `FastBinary.writeVarInt(int, ByteBuffer)` | Writes LEB128 variable-length integer into a buffer. |
| `FastBinary.readVarInt(ByteBuffer)` | Reads LEB128 variable-length integer from a buffer. |
| `FastBinary.writeSignedVarInt(int, ByteBuffer)` | Compresses signed integer using ZigZag + VarInt. |
| `FastBinary.readSignedVarInt(ByteBuffer)` | Decodes signed integer using VarInt + ZigZag. |
| `ZigZag.encode(int)` / `decode(int)` | Maps signed integer to unsigned integer and back. |
| `BitPack.packBooleans(b0..b7)` | Packs up to 8 boolean values into a single byte. |
| `BitPack.setField(word, offset, bits, val)` | Packs arbitrary sub-byte integer into an int word. |
| `BitPack.getField(word, offset, bits)` | Extracts arbitrary sub-byte integer from an int word. |
| `FastBinary.bitWriter()` / `bitReader(bytes)` | Creates sequential bitstream encoder / decoder. |
| `EndianUtil.swap(int / long / short)` | Reverses byte order of primitive integer types. |

---

## Technical Examples & Hero Demos

| Case | Java Example | Launcher | Description |
|---|---|---|---|
| **Interactive Binary Showcase** | [Demo.java](examples/Demo/src/main/java/fastbinary/demo/Demo.java) | `run-demo.bat` | VarInt compression comparison, ZigZag signed mapping, and bitstream packing. |
| **JMH Microbenchmark Suite** | [FastBinaryBenchmark.java](examples/Benchmark/src/main/java/fastbinary/benchmark/FastBinaryBenchmark.java) | `run-benchmark.bat` | High-throughput throughput benchmarks for bit manipulation and VarInt encoding. |

---

## Installation

FastJava modules require **two** dependencies: the module itself, and `FastCore` (which handles native utilities and loading).

### Option 1: Maven (Recommended)

Add the JitPack repository and the dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastBinary</artifactId>
        <version>0.1.0</version>
    </dependency>
    <!-- Required FastJava loader -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastBinary:0.1.0'
    // Required FastJava loader
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JAR directly to add it to your classpath:

1. 📦 **[FastBinary-0.1.0.jar](https://github.com/andrestubbe/FastBinary/releases/download/0.1.0/FastBinary-0.1.0.jar)** (The Core Library)
2. 📦 **[FastCore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/FastCore-0.1.0.jar)** (Required FastJava loader)

---

## Documentation

* **[COMPILE.md](docs/COMPILE.md)**: Full compilation guide (Maven Build Setup).
* **[REFERENCE.md](docs/REFERENCE.md)**: Exhaustive catalog of API contracts, bit layouts, and algorithms.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Zero-allocation and primitive bit manipulation design principles.
* **[ROADMAP.md](docs/ROADMAP.md)**: Planned milestone features and performance extensions.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Version history and release notes.

---

## Platform Support

| Platform | Status |
|---|---|
| Windows 10/11 | ✅ Fully Supported |
| Linux | ✅ Fully Supported (Pure Java) |
| macOS | ✅ Fully Supported (Pure Java) |

---

## License

MIT License — See [LICENSE](LICENSE) for details.

---

## Related Projects

- [FastCore](https://github.com/andrestubbe/FastCore) — Native JNI Loader and Utilities
- [FastBytes](https://github.com/andrestubbe/FastBytes) — Zero-copy buffer slicing and SIMD byte search
- [FastString](https://github.com/andrestubbe/FastString) — Zero-allocation string formatting and scanning
- [FastFileFormat](https://github.com/andrestubbe/FastFileFormat) — Dual-format text & binary serialization engine
- [FastTheme](https://github.com/andrestubbe/FastTheme) — Native window styling and dynamic themes
- [FastAnimation](https://github.com/andrestubbe/FastAnimation) — Zero overhead timeline orchestration

---

**Part of the FastJava Ecosystem** — *Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋*
