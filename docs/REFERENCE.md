# FastBinary API Reference

This document outlines the API contracts, bit layouts, and data structures of **FastBinary** (version 0.1.0).

---

## 1. Class: `fastbinary.VarInt`

*   `public static int sizeOf(int value)` / `sizeOf(long value)`
*   `public static int write(int value, byte[] dest, int offset)`
*   `public static int write(int value, ByteBuffer buffer)`
*   `public static int write(int value, OutputStream out)`
*   `public static int readInt(ByteBuffer buffer)`
*   `public static int readInt(InputStream in)`
*   `public static int writeLong(long value, ByteBuffer buffer)`
*   `public static long readLong(ByteBuffer buffer)`

---

## 2. Class: `fastbinary.ZigZag`

*   `public static int encode(int n)`: `(n << 1) ^ (n >> 31)`
*   `public static int decode(int n)`: `(n >>> 1) ^ -(n & 1)`
*   `public static long encode(long n)`: `(n << 1) ^ (n >> 63)`
*   `public static long decode(long n)`: `(n >>> 1) ^ -(n & 1L)`

---

## 3. Class: `fastbinary.BitPack`

*   `public static byte packBooleans(boolean b0, boolean b1, boolean b2, boolean b3, boolean b4, boolean b5, boolean b6, boolean b7)`
*   `public static boolean getBit(int word, int bitIndex)`
*   `public static int setBit(int word, int bitIndex, boolean value)`
*   `public static byte packNibbles(int lowNibble, int highNibble)`
*   `public static int getLowNibble(byte b)` / `getHighNibble(byte b)`
*   `public static byte pack2Bit(int v0, int v1, int v2, int v3)`
*   `public static int getField(int word, int offset, int numBits)`
*   `public static int setField(int word, int offset, int numBits, int value)`

---

## 4. Class: `fastbinary.BitStreamWriter` & `BitStreamReader`

*   `public BitStreamWriter writeBit(boolean bit)`
*   `public BitStreamWriter writeBits(int value, int numBits)`
*   `public BitStreamWriter flush()`
*   `public byte[] toByteArray()`
*   `public boolean readBit()`
*   `public int readBits(int numBits)`
*   `public int remainingBits()`

---

## 5. Class: `fastbinary.EndianUtil`

*   `public static short swap(short v)` / `swap(int v)` / `swap(long v)`
*   `public static short readShortLE(byte[] b, int offset)`
*   `public static int readIntLE(byte[] b, int offset)`
*   `public static long readLongLE(byte[] b, int offset)`
*   `public static void writeShortLE(short v, byte[] b, int offset)`
*   `public static void writeIntLE(int v, byte[] b, int offset)`
*   `public static void writeLongLE(long v, byte[] b, int offset)`
