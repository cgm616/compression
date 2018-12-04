import java.util.ArrayList;
import java.lang.ArrayIndexOutOfBoundsException;

public class BitArray {
    private int bitLength;
    private ArrayList<Byte> back;

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
        return !((holder & mask) == 0);
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
        int index = (bitLength + 1) % 8;
        int mask = 1 << (7 - (index));

        if (value) {
            last = last | mask;
        } else {
            last = last & (~mask);
        }

        this.back.set(this.back.size() - 1, (byte) last);
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
}