> [!WARNING]
> **🚧 WIP — Active AI Pipeline Construction & Architecture Optimization in Progress.**

# FastBinary [ALPHA-2026-09-08] — High-Performance Bit-Packing, VarInt Encoding & Endianness Engine for Java

[![Status](https://img.shields.io/badge/status-0.1.1-brightgreen.svg)](https://github.com/andrestubbe/FastBinary/releases/tag/0.1.1)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
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
- [Key Features](#key-features)
- [Real-World Use Cases](#real-world-use-cases)
- [Architecture Overview](#architecture-overview)
- [Performance Benchmarks](#performance-benchmarks)
- [API Quick Reference](#api-quick-reference)
- [Technical Demos & Benchmarks](#technical-demos--benchmarks)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastBinary?

Modern network protocols, game engines, and vector stores waste millions of bytes transmitting padded 32-bit and 64-bit primitive types when values are small or boolean in nature:

- **Massive Bandwidth & Memory Waste** — Standard integers occupy 4 full bytes even for values like `0`, `1`, or `42`.
- **GC Allocations in High-Frequency Packet Serialization** — Typical BitSet or bitfield libraries create wrapper objects on the heap during hot encoding loops.
- **Complex Third-Party Dependencies** — Pulling in Google Protobuf or Apache Commons just for LEB128 VarInt or bit-twiddling introduces heavy jar bloat.

FastBinary solves this with pure, zero-allocation primitive utilities:

| Feature | Standard Java Primitives | Apache Commons / Protobuf | FastBinary |
|:---|:---|:---|:---|
| **Small Integer Footprint** | Fixed 4 or 8 bytes (Padded) | 1–5 bytes (Heavy framework) | 1–5 bytes (LEB128 VarInt) |
| **Signed Compression** | Two's complement full width | Variable ZigZag via Protobuf | Zero-alloc inline `ZigZag` mapping |
| **Sub-Byte Bitfields** | `BitSet` allocates heap objects | BitField wrapper objects | Inlined register masks (`BitPack`) |
| **Bit-Level Streaming** | Complex manual bitwise shifts | Byte-padded streams | Sequential `BitStream` without padding |
| **Allocation Overhead** | Low | High wrapper churn | **0 bytes / op (Zero GC)** |

---

## Key Features

- ⚡ **LEB128 VarInt & VarLong** — Standard variable-length integer compression (1–5 bytes for `int`, 1–10 bytes for `long`).
- 🔀 **ZigZag Encoding** — Optimal signed integer compression mapping negative numbers to small positive numbers.
- 🎯 **Bit-Level Streams (`BitStream`)** — Write and read arbitrary bit widths (1..32 bits) across raw byte arrays without byte padding.
- 📦 **Sub-Byte Bitfield Packing (`BitPack`)** — Pack booleans, nibbles (4-bit), 2-bit flags, and custom bitfields into primitive words.
- 🔄 **Endianness Utilities (`EndianUtil`)** — Zero-allocation Little-Endian and Big-Endian integer conversions and byte-swapping.
- 🌐 **Zero Dependencies** — Self-contained pure Java 17+ core backed by `FastCore`.

---

## Real-World Use Cases

- 💾 **FastFileFormat Dual Serialization**: Provides bit-packing and VarInt stream encoding for headers, payload lengths, and compact binary records.
- 🎮 **Multiplayer Game State & Telemetry**: Packs 1-bit booleans, 4-bit entity states, and 10-bit rotation angles into tight network datagrams.
- 🧠 **Vector Index & Quantization Bitmasks**: Compresses sparse indexes, product quantization codes, and binary search bitmasks in `FastAIVectorDB`.
- 📊 **Time-Series & Sensor Delta Compression**: High-density delta-of-delta compression for timestamp sequences and numeric counters.

---

## Architecture Overview

FastBinary serves as the low-level bit and byte encoding foundation across FastJava:

- ⚡ **[FastBinary](https://github.com/andrestubbe/FastBinary)** (Bit/Byte Layer): VarInt, ZigZag, BitStream, and primitive register bit-packing.
- 📄 **[FastFileFormat](https://github.com/andrestubbe/FastFileFormat)** (Serialization): Builds on FastBinary to serialize dual-format text and binary files.
- 🧠 **[FastAIState](https://github.com/andrestubbe/FastAIState)** (Agent Memory): Uses FastBinary for dense multi-agent state snapshot encoding.
- 🚀 **[FastCore](https://github.com/andrestubbe/FastCore)** (Foundation): Native JNI loader and platform memory utilities.

---

## Performance Benchmarks

FastBinary is profiled using **JMH** to guarantee zero-allocation execution:

| Benchmark Operation | Score (ops/ms) | Ops per Second | Memory Allocation |
|:---|:---|:---|:---|
| **BitPack Field Set & Get** | **~1,455,000 ops/ms** | **> 1.45 Billion** | **0 bytes / op (Zero GC)** |
| **ZigZag Signed Mapping** | **~1,421,000 ops/ms** | **> 1.42 Billion** | **0 bytes / op (Zero GC)** |
| **VarInt Encode** | **~182,000 ops/ms** | **> 182 Million** | **0 bytes / op (Zero GC)** |
| **BitStream Sequential Read** | **~1,820 ops/ms** | **> 1.82 Million** | **0 bytes / op (Zero GC)** |

*Measured on Windows 11 x64, Intel Core i5 (Surface Pro 8), JDK 21.0.12.1.*

---

## API Quick Reference

| Class / Method | Return Type | Description |
|:---|:---|:---|
| `FastBinary.writeVarInt(val, buffer)` | `int` | Writes LEB128 variable-length integer into a buffer. |
| `FastBinary.readVarInt(buffer)` | `int` | Reads LEB128 variable-length integer from a buffer. |
| `FastBinary.writeSignedVarInt(val, buf)` | `int` | Compresses signed integer using ZigZag + VarInt. |
| `FastBinary.readSignedVarInt(buffer)` | `int` | Decodes signed integer using VarInt + ZigZag. |
| `ZigZag.encode(int)` / `decode(int)` | `int` | Maps signed integer to unsigned integer and back. |
| `BitPack.packBooleans(b0..b7)` | `byte` | Packs up to 8 boolean values into a single byte. |
| `BitPack.setField(word, offset, bits, val)` | `int` | Packs arbitrary sub-byte integer into an int word. |
| `BitPack.getField(word, offset, bits)` | `int` | Extracts arbitrary sub-byte integer from an int word. |
| `FastBinary.bitWriter()` | `BitStreamWriter` | Creates sequential bitstream encoder. |
| `FastBinary.bitReader(bytes)` | `BitStreamReader` | Creates sequential bitstream decoder. |

---

## Technical Demos & Benchmarks

| Case | Java Example | Launcher | Description |
|:---|:---|:---|:---|
| **Interactive Binary Showcase** | [Demo.java](examples/Demo/src/main/java/fastbinary/demo/Demo.java) | `run-demo.bat` | VarInt compression comparison, ZigZag signed mapping, and bitstream packing. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastbinary/benchmark/Benchmark.java) | `run-benchmark.bat` | High-throughput throughput benchmarks for bit manipulation and VarInt encoding. |

---

## Installation

### Option 1: Maven (Recommended via JitPack)

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
        <version>0.1.1</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastBinary:0.1.1'
}
```

### Option 3: Direct Download (No Build Tool)

Download the latest JARs directly to add them to your classpath:

1. 📦 **[FastBinary-0.1.1.jar](https://github.com/andrestubbe/FastBinary/releases/download/0.1.1/FastBinary-0.1.1.jar)** (The Core Library)
2. ⚙️ **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (FastJava runtime substrate)

---

## Documentation

- **[REFERENCE.md](docs/REFERENCE.md)**: Exhaustive catalog of API contracts, bit layouts, and algorithms.
- **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Zero-allocation and primitive bit manipulation design principles.
- **[ROADMAP.md](docs/ROADMAP.md)**: Planned milestone features and performance extensions.
- **[CHANGELOG.md](docs/CHANGELOG.md)**: Version history and release notes.
- **[COMPILE.md](docs/COMPILE.md)**: Full compilation guide (Maven Build Setup).

---

## Platform Support

| Platform | Architecture | Status | Notes |
|:---|:---|:---|:---|
| Windows 10/11 | x64, ARM64 | ✅ Fully Supported | Native high-performance pure Java |
| Linux | x64, ARM64 | ✅ Fully Supported | Tested on Ubuntu / Debian / RHEL |
| macOS | Apple Silicon, x64 | ✅ Fully Supported | Tested on macOS Sonoma / Sequoia |

---

## License

MIT License — See [LICENSE](LICENSE) file for details.

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