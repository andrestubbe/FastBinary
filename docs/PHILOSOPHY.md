# The Philosophy of FastBinary

> [!IMPORTANT]
> **"Sub-Byte Density. Zero Heap Allocations. Nanosecond Bit Twiddling."**

Every byte and bit in network packets, disk serialization, and in-memory caches counts.

## Core Tenets

### 1. Zero Heap Allocations
Bit manipulation and integer compression routines must never instantiate wrapper objects or intermediate arrays. Every method operates on raw primitive registers, primitive arrays, and `ByteBuffer` views.

### 2. Maximum Payload Density
Why spend 4 bytes on an integer that holds `1` or `42`? FastBinary provides LEB128 VarInt and ZigZag encoding to shrink small integers into single bytes while preserving full 32-bit and 64-bit dynamic range.

### 3. Sub-Byte Bitfield Control
When storing flags, enum indices, and boolean states, FastBinary lets developers pack multiple fields into individual bytes and words without awkward manual mask math.

---

**⚡ FastBinary — Zero-bloat bit manipulation for the FastJava ecosystem.**
