import java.util.ArrayList;

public class BitArray {
    protected int bitLength;
    protected ArrayList<Byte> back;

    public BitArray() {
        this(64);
    }

    public BitArray(int capacity) {
        this.back = new ArrayList<Byte>((capacity / 8) + 1);
        this.bitLength = 0;
    }

    private BitArray(ArrayList<Byte> array, int bitLength) {
        this.back = array;
        this.bitLength = bitLength;
    }

    public boolean get(int bitIndex) {
        int byteIndex = bitIndex / 8;
        int offset = bitIndex % 8;
        byte holder = this.back.get(byteIndex);
        int mask = 1 << (7 - offset);
        return (holder & mask) != 0;
    }

    public void set(int bitIndex, boolean value) {
        if (bitIndex >= this.bitLength) {
            throw new ArrayIndexOutOfBoundsException();
        }

        int storage = (int) this.back.get(bitIndex / 8);
        int mask = 1 << (7 - (bitIndex % 8));

        if (value) {
            storage = storage | mask;
        } else {
            storage = storage & (~mask);
        }

        this.back.set(bitIndex / 8, (byte) storage);
    }

    public void push(boolean value) {
        if (this.bitLength + 1 > this.back.size() * 8) {
            this.back.add((byte) 0);
        }

        int last = (int) this.back.get(this.back.size() - 1);
        int index = (bitLength) % 8;
        int mask = 1 << (7 - (index));

        if (value) {
            last = last | mask;
        } else {
            last = last & (~mask);
        }

        this.bitLength += 1;

        this.back.set(this.back.size() - 1, (byte) last);
    }

    public void pushByte(byte value) {
        int capacity = this.back.size() * 8;
        int offset = capacity - this.bitLength;

        if (offset == 0) {
            this.back.add(value);
        } else {
            int last = (int) this.back.get(this.back.size() - 1);
            last = last | (value >> (8 - offset));
            this.back.set(this.back.size() - 1, (byte) last);
            this.back.add((byte) (value << offset));
        }
        this.bitLength += 8;
    }

    public void pushInt(int value) {
        this.pushByte((byte) (value >> 24));
        this.pushByte((byte) ((value << 8) >> 24));
        this.pushByte((byte) ((value << 16) >> 24));
        this.pushByte((byte) ((value << 24) >> 24));
    }

    public static BitArray fromBytes(byte[] bytes) {
        ArrayList<Byte> array = new ArrayList<Byte>(bytes.length + 2);
        for (byte b : bytes) {
            array.add(b);
        }
        return new BitArray(array, bytes.length * 8);
    }

    public int length() {
        return this.bitLength;
    }

    public byte[] toArray() {
        byte[] output = new byte[this.back.size()];

        for (int i = 0; i < this.back.size(); i++) {
            output[i] = this.back.get(i);
        }

        return output;
    }

    public Integer getInt(int bitIndex) {
        if (bitIndex % 8 != 0) {
            return null;
        }

        if (bitIndex + 31 >= this.bitLength) {
            return null;
        }

        int index = bitIndex / 8;

        int ret = 0;

        for (int i = 0; i < 3; i++) {
            ret |= (int) this.back.get(index + i) & 0xFF;
            ret = ret << 8;
        }
        ret |= (int) this.back.get(index + 3) & 0xFF;

        return ret;
    }
}