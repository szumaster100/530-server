package core.net.packet;

import core.cache.crypto.ISAACCipher;
import core.cache.misc.buffer.ByteBufferUtils;

import java.nio.ByteBuffer;

public class IoBuffer {

    private static final int[] BIT_MASK = new int[32];

    private int packetSize;

    private int opcode;

    private final PacketHeader header;

    private ByteBuffer buf;

    private int bitPosition = 0;

    public IoBuffer() {
        this(-1, PacketHeader.NORMAL, ByteBuffer.allocate(2048));
    }

    public IoBuffer(int opcode) {
        this(opcode, PacketHeader.NORMAL, ByteBuffer.allocate(2048));
    }

    public IoBuffer(int opcode, PacketHeader header) {
        this(opcode, header, ByteBuffer.allocate((1 << 16) + 1));
    }

    public IoBuffer(int opcode, PacketHeader header, ByteBuffer buf) {
        this.opcode = opcode;
        this.header = header;
        this.buf = buf;
    }

    static {
        for (int i = 0; i < 32; i++) {
            BIT_MASK[i] = (1 << i) - 1;
        }
    }

    public IoBuffer clear() {
        buf.clear();
        bitPosition = 0;
        return this;
    }

    public IoBuffer p1(int value) {
        buf.put((byte) value);
        return this;
    }

    public IoBuffer p1add(int value) {
        buf.put((byte) (value + 128));
        return this;
    }

    public IoBuffer p1sub(int value) {
        buf.put((byte) (128 - value));
        return this;
    }

    public IoBuffer p1neg(int value) {
        buf.put((byte) -value);
        return this;
    }

    public IoBuffer p2(int value) {
        buf.put((byte) (value >> 8));
        buf.put((byte) value);
        return this;
    }

    public IoBuffer p2add(int value) {
        buf.put((byte) (value >> 8));
        buf.put((byte) (value + 128));
        return this;
    }

    public IoBuffer ip2(int value) {
        buf.put((byte) value);
        buf.put((byte) (value >> 8));
        return this;
    }

    public IoBuffer ip2add(int value) {
        buf.put((byte) (value + 128));
        buf.put((byte) (value >> 8));
        return this;
    }

    public IoBuffer p3(int value) {
        buf.put((byte) (value >> 16));
        buf.put((byte) (value >> 8));
        buf.put((byte) value);
        return this;
    }

    public IoBuffer ip3(int value) {
        buf.put((byte) value);
        buf.put((byte) (value >> 8));
        buf.put((byte) (value >> 16));
        return this;
    }

    public IoBuffer p4(int value) {
        buf.put((byte) (value >> 24));
        buf.put((byte) (value >> 16));
        buf.put((byte) (value >> 8));
        buf.put((byte) value);
        return this;
    }

    public IoBuffer ip4(int value) {
        buf.put((byte) value);
        buf.put((byte) (value >> 8));
        buf.put((byte) (value >> 16));
        buf.put((byte) (value >> 24));
        return this;
    }

    public IoBuffer mp4(int value) {
        buf.put((byte) (value >> 16));
        buf.put((byte) (value >> 24));
        buf.put((byte) value);
        buf.put((byte) (value >> 8));
        return this;
    }

    public IoBuffer imp4(int value) {
        buf.put((byte) (value >> 8));
        buf.put((byte) value);
        buf.put((byte) (value >> 24));
        buf.put((byte) (value >> 16));
        return this;
    }

    public IoBuffer p8(long value) {
        buf.put((byte) (value >> 56));
        buf.put((byte) (value >> 48));
        buf.put((byte) (value >> 40));
        buf.put((byte) (value >> 32));
        buf.put((byte) (value >> 24));
        buf.put((byte) (value >> 16));
        buf.put((byte) (value >> 8));
        buf.put((byte) value);
        return this;
    }

    public IoBuffer pVarInt(int value) {
        if ((value & 0xffffff80) != 0) {
            if ((value & 0xffffc000) != 0) {
                if ((value & 0xFFE00000) != 0) {
                    if ((value & 0xF0000000) != 0) {
                        this.p1(value >>> 28 | 0x80);
                    }
                    this.p1(value >>> 21 | 0x80);
                }
                this.p1(value >>> 14 | 0x80);
            }
            this.p1(value >>> 7 | 0x80);
        }
        return this.p1(value & 0x7F);
    }

    public IoBuffer pVarLong(int size, long value) {
        int bytes = size - 1;
        if (bytes < 0 || bytes > 7)
            throw new IllegalArgumentException();
        for (int shift = bytes * 8; shift >= 0; shift -= 8)
            this.p1((byte) (value >> shift));
        return this;
    }

    public IoBuffer psmarts(int value) {
        if (value >= 0 && value < 128)
            this.p1(value);
        else if (value >= 0 && value < 0x8000)
            this.p2(value + 0x8000);
        else
            throw new IllegalArgumentException("smart out of range: $value");
        return this;
    }

    public IoBuffer psize(int length) {
        buf.put(buf.position() - length - 1, (byte) length);
        return this;
    }

    public IoBuffer psizeadd(int length) {
        buf.put(buf.position() - length - 1, (byte) (length + 128));
        return this;
    }

    public int g1() {
        return buf.get() & 0xFF;
    }

    public int g1b() {
        return buf.get();
    }

    public int g1add() {
        return (buf.get() - 128) & 0xFF;
    }

    public int g1neg() {
        return -(buf.get() & 0xFF);
    }

    public int g1sub() {
        return (128 - buf.get()) & 0xFF;
    }

    public int g2() {
        return ((buf.get() & 0xFF) << 8) + (buf.get() & 0xFF);
    }

    public int g2add() {
        return ((buf.get() & 0xff) << 8) + ((buf.get() - 128) & 0xFF);
    }

    public int g2b() {
        int value = ((buf.get() & 0xFF) << 8) + (buf.get() & 0xFF);
        if (value > 32767)
            value -= 0x10000;
        return value;
    }

    public int ig2() {
        return (buf.get() & 0xFF) + ((buf.get() & 0xFF) << 8);
    }

    public int ig2add() {
        return ((buf.get() - 128) & 0xFF) + ((buf.get() & 0xFF) << 8);
    }

    public int g3() {
        return ((buf.get() & 0xFF) << 16) + ((buf.get() & 0xFF) << 8) + (buf.get() & 0xFF);
    }

    public int ig3() {
        return (buf.get() & 0xFF) + ((buf.get() & 0xFF) << 8) + ((buf.get() & 0xFF) << 16);
    }

    public int g4() {
        return ((buf.get() & 0xFF) << 24) + ((buf.get() & 0xFF) << 16) + ((buf.get() & 0xFF) << 8) + (buf.get() & 0xFF);
    }

    public int ig4() {
        return (buf.get() & 0xFF) + ((buf.get() & 0xFF) << 8) + ((buf.get() & 0xFF) << 16) + ((buf.get() & 0xFF) << 24);
    }

    public int m4() {
        return ((buf.get() & 0xFF) << 16) + ((buf.get() & 0xFF) << 24) + (buf.get() & 0xFF) + ((buf.get() & 0xFF) << 8);
    }

    public int im4() {
        return ((buf.get() & 0xFF) << 8) + (buf.get() & 0xFF) + ((buf.get() & 0xFF) << 24) + ((buf.get() & 0xFF) << 16);
    }

    public long g8() {
        long low = (long) this.g4() & 0xFFFFFFFFL;
        long high = (long) this.g4() & 0xFFFFFFFFL;
        return high + (low << 32);
    }

    public IoBuffer put(int val) {
        buf.put((byte) val);
        return this;
    }

    public IoBuffer putBytes(byte[] datas, int offset, int len) {
        for (int i = offset; i < len; i++) {
            put(datas[i]);
        }
        return this;
    }

    public final void getBytes(byte data[], int off, int len) {
        for (int k = off; k < len + off; k++) {
            data[k] = data[off++];
        }
    }

    public IoBuffer putA(int val) {
        buf.put((byte) (val + 128));
        return this;
    }

    public IoBuffer putC(int val) {
        buf.put((byte) -val);
        return this;
    }

    public IoBuffer putS(int val) {
        buf.put((byte) (128 - val));
        return this;
    }

    public IoBuffer putTri(int val) {
        buf.put((byte) (val >> 16));
        buf.put((byte) (val >> 8));
        buf.put((byte) val);
        return this;
    }

    public IoBuffer putShort(int val) {
        buf.putShort((short) val);
        return this;
    }

    public IoBuffer putLEShort(int val) {
        buf.put((byte) val);
        buf.put((byte) (val >> 8));
        return this;
    }

    public IoBuffer putShortA(int val) {
        buf.put((byte) (val >> 8));
        buf.put((byte) (val + 128));
        return this;
    }

    public IoBuffer putLEShortA(int val) {
        buf.put((byte) (val + 128));
        buf.put((byte) (val >> 8));
        return this;
    }

    public IoBuffer putInt(int val) {
        buf.putInt(val);
        return this;
    }

    public IoBuffer putLEInt(int val) {
        buf.put((byte) val);
        buf.put((byte) (val >> 8));
        buf.put((byte) (val >> 16));
        buf.put((byte) (val >> 24));
        return this;
    }

    public IoBuffer putIntA(int val) {
        buf.put((byte) (val >> 8));
        buf.put((byte) val);
        buf.put((byte) (val >> 24));
        buf.put((byte) (val >> 16));
        return this;
    }

    public IoBuffer putIntB(int val) {
        buf.put((byte) (val >> 16));
        buf.put((byte) (val >> 24));
        buf.put((byte) val);
        buf.put((byte) (val >> 8));
        return this;
    }

    public IoBuffer putLong(long val) {
        buf.putLong(val);
        return this;
    }

    public IoBuffer putSmart(int val) {
        if (val > Byte.MAX_VALUE) {
            buf.putShort((short) (val + 32768));
        } else {
            buf.put((byte) val);
        }
        return this;
    }

    public IoBuffer putIntSmart(int val) {
        if (val > Short.MAX_VALUE) {
            buf.putInt(val + 32768);
        } else {
            buf.putShort((short) val);
        }
        return this;
    }

    public IoBuffer putString(String val) {
        buf.put(val.getBytes());
        buf.put((byte) 0);
        return this;
    }

    public IoBuffer putJagString(String val) {
        buf.put((byte) 0);
        buf.put(val.getBytes());
        buf.put((byte) 0);
        return this;
    }

    public IoBuffer putJagString2(String val) {
        byte[] packed = new byte[256];
        int length = ByteBufferUtils.packGJString2(0, packed, val);
        buf.put((byte) 0).put(packed, 0, length).put((byte) 0);
        return this;
    }

    public IoBuffer put(byte[] val) {
        buf.put(val);
        return this;
    }

    public void putReverseA(byte[] data, int start, int offset) {
        for (int i = offset + start; i >= start; i--) {
            putA(data[i]);
        }
    }

    public void putReverse(byte[] data, int start, int offset) {
        for (int i = offset + start; i >= start; i--) {
            put(data[i]);
        }
    }

    public IoBuffer putBits(int numBits, int value) {
        int bytePos = getBitPosition() >> 3;
        int bitOffset = 8 - (getBitPosition() & 7);
        bitPosition += numBits;
        for (; numBits > bitOffset; bitOffset = 8) {
            byte b = buf.get(bytePos);
            buf.put(bytePos, b &= ~BIT_MASK[bitOffset]);
            buf.put(bytePos++, b |= value >> numBits - bitOffset & BIT_MASK[bitOffset]);
            numBits -= bitOffset;
        }
        byte b = buf.get(bytePos);
        if (numBits == bitOffset) {
            buf.put(bytePos, b &= ~BIT_MASK[bitOffset]);
            buf.put(bytePos, b |= value & BIT_MASK[bitOffset]);
        } else {
            buf.put(bytePos, b &= ~(BIT_MASK[numBits] << bitOffset - numBits));
            buf.put(bytePos, b |= (value & BIT_MASK[numBits]) << bitOffset - numBits);
        }
        return this;
    }

    public IoBuffer put(IoBuffer buffer) {
        buffer.toByteBuffer().flip();
        buf.put(buffer.toByteBuffer());
        return this;
    }

    public IoBuffer putA(IoBuffer buffer) {
        buffer.toByteBuffer().flip();
        while (buffer.toByteBuffer().hasRemaining()) {
            putA(buffer.toByteBuffer().get());
        }
        return this;
    }

    public IoBuffer put(ByteBuffer buffer) {
        buf.put(buffer);
        return this;
    }

    public IoBuffer setBitAccess() {
        bitPosition = buf.position() * 8;
        return this;
    }

    public IoBuffer setByteAccess() {
        buf.position((getBitPosition() + 7) / 8);
        return this;
    }

    public int get() {
        return buf.get();
    }

    public int getA() {
        return (buf.get() & 0xFF) - 128;
    }

    public int getC() {
        return -buf.get();
    }

    public int getS() {
        return 128 - (buf.get() & 0xFF);
    }

    public int getTri() {
        return ((buf.get() << 16) & 0xFF) | ((buf.get() << 8) & 0xFF) | (buf.get() & 0xFF);
    }

    public int getShort() {
        return buf.getShort();
    }

    public int getLEShort() {
        return (buf.get() & 0xFF) | ((buf.get() & 0xFF) << 8);
    }

    public int getShortA() {
        return ((buf.get() & 0xFF) << 8) | (buf.get() - 128 & 0xFF);
    }

    public int getLEShortA() {
        return (buf.get() - 128 & 0xFF) | ((buf.get() & 0xFF) << 8);
    }

    public int getInt() {
        return buf.getInt();
    }

    public int getLEInt() {
        return (buf.get() & 0xFF) + ((buf.get() & 0xFF) << 8) + ((buf.get() & 0xFF) << 16) + ((buf.get() & 0xFF) << 24);
    }

    public int getIntA() {
        return ((buf.get() & 0xFF) << 8) + (buf.get() & 0xFF) + ((buf.get() & 0xFF) << 24) + ((buf.get() & 0xFF) << 16);
    }

    public int getIntB() {
        return ((buf.get() & 0xFF) << 16) + ((buf.get() & 0xFF) << 24) + (buf.get() & 0xFF) + ((buf.get() & 0xFF) << 8);
    }

    public long getLongL() {
        long first = getIntB();
        long second = getIntB();
        if (second < 0)
            second = second & 0xffffffffL;
        return (first << -41780448) + second;
    }

    public long getLong() {
        return buf.getLong();
    }

    public int getSmart() {
        int peek = buf.get(buf.position());
        if (peek <= (0xFF & peek)) {
            return buf.get() & 0xFF;
        }
        return (buf.getShort() & 0xFFFF) - 32768;
    }

    public int getIntSmart() {
        int peek = buf.getShort(buf.position());
        if (peek <= Short.MAX_VALUE) {
            return buf.getShort() & 0xFFFF;
        }
        return (buf.getInt() & 0xFFFFFFFF) - 32768;
    }

    public String getString() {
        return ByteBufferUtils.getString(buf);
    }

    public String getJagString() {
        byte b = buf.get();
        if (b == 0) return "";
        return ((char) b) + ByteBufferUtils.getString(buf);
    }

    public IoBuffer getReverseA(byte[] is, int offset, int length) {
        for (int i = (offset + length - 1); i >= offset; i--) {
            is[i] = (byte) (buf.get() - 128);
        }
        return this;
    }

    public void cypherOpcode(ISAACCipher cipher) {
        this.opcode += (byte) cipher.getNextValue();
    }

    public ByteBuffer toByteBuffer() {
        return buf;
    }

    public int opcode() {
        return opcode;
    }

    public int readableBytes() {
        return buf.capacity() - buf.remaining();
    }

    public PacketHeader getHeader() {
        return header;
    }

    public byte[] array() {
        return buf.array();
    }

    public int getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(int packetSize) {
        this.packetSize = packetSize;
    }

    public int getBitPosition() {
        return bitPosition;
    }

}
