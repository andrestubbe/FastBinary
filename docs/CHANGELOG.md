# Changelog: FastBinary

All notable changes to this project will be documented in this file.

## [0.1.0] - 2026-08-24
### Added
- **LEB128 Variable-Length Integer Encoding (`VarInt`)**: Zero-allocation encoding and decoding for 32-bit `int` (1..5 bytes) and 64-bit `long` (1..10 bytes) on byte arrays, ByteBuffers, and Streams.
- **ZigZag Signed Integer Mapping (`ZigZag`)**: Efficient bitwise bidirectional mapping for signed integers.
- **Sub-Byte Bitfield Packing (`BitPack`)**: Utilities for packing booleans, nibbles (4-bit), 2-bit flags, and custom bitfields.
- **Sequential Bit Streams (`BitStreamWriter`, `BitStreamReader`)**: Non-aligned bit-level streaming.
- **Endianness Operations (`EndianUtil`)**: Little-Endian and Big-Endian primitive encoders, decoders, and byte-swappers.
- **Central API Facade (`FastBinary`)**: Unified static methods.
- **Interactive Showcase & JMH Benchmarks**: Benchmark suite measuring ops/ms and memory allocation.
