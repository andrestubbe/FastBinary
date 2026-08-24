# FastBinary Roadmap

## Milestones

### Version 0.1.0 (Current)
- [x] LEB128 VarInt & VarLong encoding/decoding.
- [x] ZigZag signed number compression.
- [x] Sub-byte bitfield packing (`BitPack`).
- [x] Sequential bitstream writer and reader (`BitStream`).
- [x] Endianness swapping and primitive serialization.

### Version 0.2.0 (Planned)
- [ ] SIMD-accelerated batch VarInt decoding with AVX2.
- [ ] Direct Unsafe / Foreign Function & Memory (FFM) Panama primitive serialization.
- [ ] Bitset operations and bitwise vector math.
