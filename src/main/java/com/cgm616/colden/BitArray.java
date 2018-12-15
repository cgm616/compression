package com.cgm616.colden;

import java.util.ArrayList;

/**
 * A resizeable array of individual bits. They can be read one by one, in groups
 * of bytes, or in groups of ints. Each bit is individually indexed.
 * 
 * Used
 * https://stackoverflow.com/questions/4439595/how-to-create-a-sub-array-from-another-array-in-java
 * for creating subarrays
 */
public class BitArray {
    // The length of the array in bits
    protected int bitLength;

    // The array of bytes that backs the BitArray and holds its data
    protected ArrayList<Byte> back;

    /**
     * Class constructor of the BitArray, initializing the array with a default
     * capacity of 64 bits
     */
    public BitArray() {
        this(64);
    }

    /**
     * Class constructor of BitArray that takes an initial capacity
     * 
     * @param capacity The initial capacity of the array
     */
    public BitArray(int capacity) {
        // Construct a new ArrayList that has the required capacity
        this.back = new ArrayList<Byte>((capacity / 8) + 1);
        this.bitLength = 0;
    }

    /**
     * Class constructor of BitArray that takes another BitArray to clone
     * 
     * @param other The BitArray to clone
     */
    public BitArray(BitArray other) {
        // Set the new bitLength to the other's
        this.bitLength = other.bitLength;
        // Create a new arraylist to back this instance with the same capacity as the
        // other one
        ArrayList<Byte> newBack = new ArrayList<Byte>(other.back.size());
        // For each byte in the other's backing array, add that byte to the new array,
        // making sure that a new Byte object is created (deep copy)
        for (Byte b : other.back) {
            newBack.add(new Byte((byte) b));
        }
        // Set the backing array to the new one just created
        this.back = newBack;
    }

    /**
     * Private class constructor of BitArray that takes an ArrayList of Bytes and a
     * bitLength. This method has a great potentional for being misused, so it is
     * private
     * 
     * @param array     The array of bits to back this collection with. It cannot be
     *                  tampered with outside of this class
     * @param bitLength The number of valid bits in the array
     */
    private BitArray(ArrayList<Byte> array, int bitLength) {
        this.back = array;
        this.bitLength = bitLength;
    }

    /**
     * This method gets a specific bit in the array, returning true for a value of 1
     * and false for a value of 0
     * 
     * @param bitIndex The index of the desired bit
     * @return The value of the bit
     */
    public boolean get(int bitIndex) {
        // Determine which byte in the backing ArrayList contains the bit
        int byteIndex = bitIndex / 8;
        // Figure out which bit, from most significant to least, we want in the byte
        // that holds it
        int offset = bitIndex % 8;
        // Get the byte that holds our bit from the backing array
        byte holder = this.back.get(byteIndex);
        // Create a bitmask to only pass through the value of the specific bit we want
        int mask = 1 << (7 - offset);
        // Return the bit's value with bitmasking. If the mask returns a 0, then the bit
        // we're looking at is 0. If it returns anything else, the bit is a 1
        return (holder & mask) != 0;
    }

    /**
     * This method sets a specific bit in the array to either 1 or 0
     * 
     * @param bitIndex The index of the bit to set
     * @param value    The value to set the bit to
     */
    public void set(int bitIndex, boolean value) {
        if (bitIndex > bitLength) {
            throw new ArrayIndexOutOfBoundsException();
        }
        // Get the byte that stores the bit we want to set from the backing array
        int storage = (int) this.back.get(bitIndex / 8);
        // Create a mask that we can use to only change the specific bit we want to
        int mask = 1 << (7 - (bitIndex % 8));

        // Check if the bit should be set to 1 or 0
        if (value) {
            // The bit should be set to 1, so we bitwise-or the mask with the storage byte
            storage = storage | mask;
        } else {
            // The bit should be set to 0, so we invert the mask (every bit except the one
            // we want to set becomes 1) and bitwise-and it with the storage byte
            storage = storage & (~mask);
        }

        // Replace the updated byte into the array
        this.back.set(bitIndex / 8, (byte) storage);
    }

    /**
     * This method adds a new bit the end of the array
     * 
     * @param value The value of the bit to add
     */
    public void push(boolean value) {
        // Check if there is enough capacity to add another bit
        if (this.bitLength + 1 > this.back.size() * 8) {
            // If there isn't, add a byte to the backing array, increasing the capacity
            this.back.add((byte) 0x00);
        }

        // At this point, we know that the last byte in the backing array has space for
        // at least one more bit, so we get that byte and then create a mask, locating
        // the next available byte by using the bitLength of the instance itself
        int last = (int) this.back.get(bitLength / 8);
        int index = (bitLength) % 8;
        int mask = 1 << (7 - (index));

        // Check if the bit being pushed is a 1 or a 0
        if (value) {
            // The bit should be set to 1, so we bitwise-or the mask with the storage byte
            last = last | mask;
        } else {
            // The bit should be set to 0, so we invert the mask (every bit except the one
            // we want to set becomes 1) and bitwise-and it with the storage byte
            last = last & (~mask);
        }

        // Set the last byte of the backing array to the updated value
        this.back.set(bitLength / 8, (byte) last);

        // Increment the bitLength of the array by 1
        this.bitLength += 1;
    }

    /**
     * This method returns and removes the last bit added
     * 
     * @return The value of the last bit
     */
    public boolean pop() {
        boolean ret = get(bitLength - 1);
        this.bitLength -= 1;
        return ret;
    }

    /**
     * This method adds a new byte (8 bits) the end of the array
     * 
     * @param value The value of the byte to add
     */
    public void pushByte(byte value) {
        // Convert the byte input to an int, ensuring that no two's complement nonsense
        // goes on
        int toAdd = (int) value & 0xFF;

        // Iterate over each bit in the byte input, computing a mask to get that bit
        // specifically (from most significant to least)
        for (int i = 0; i < 8; i++) {
            int mask = 1 << (7 - i);
            // Check if the current bit is 1 or 0
            if ((toAdd & mask) != 0) {
                // The byte is 1; push a 1 bit to the array
                push(true);
            } else {
                // The byte is 0; push a 0 bit to the array
                push(false);
            }
        }
    }

    /**
     * This method adds a new int (32 bits) to the array
     * 
     * @param value The value of the int to add
     */
    public void pushInt(int value) {
        // Push the four bytes that make up this int to the array one by one, in order
        // from most significant to least significant
        this.pushByte((byte) (value >> 24));
        this.pushByte((byte) ((value << 8) >> 24));
        this.pushByte((byte) ((value << 16) >> 24));
        this.pushByte((byte) ((value << 24) >> 24));
    }

    /**
     * This method creates a new BitArray from an array of bytes, assuming that
     * every bit in the array is valid to access
     * 
     * @param bytes The byte array containing the data to use
     * @return A BitArray containing the data given
     */
    public static BitArray fromBytes(byte[] bytes) {
        // Create a new backing array that has enough space for the input data
        ArrayList<Byte> array = new ArrayList<Byte>(bytes.length + 2);

        // Iterate over the input data, adding it to the backing array
        for (byte b : bytes) {
            array.add(b);
        }

        // Create a new BitArray from the backing array and the length of the data
        return new BitArray(array, bytes.length * 8);
    }

    /**
     * This method creates an array of bytes from the data in the BitArray
     * 
     * @return An array of bytes containing the data in this structure
     */
    public byte[] toArray() {
        // Create a new output byte array of the correct size
        byte[] output = new byte[this.back.size()];

        // Iterate over the backing array and add each element to the return array
        for (int i = 0; i < this.back.size(); i++) {
            output[i] = this.back.get(i);
        }

        return output;
    }

    /**
     * This method reads 32 bits from the array, interpreting them as an int
     * 
     * @param bitIndex The index of the first bit to read. This must be byte-aligned
     * @return An int containing the next 32 bits
     */
    public int getInt(int bitIndex) {
        // Make sure the bitIndex is byte-aligned
        if (bitIndex % 8 != 0) {
            // If it isn't, throw an exception
            throw new ArrayIndexOutOfBoundsException("Index is not byte-aligned");
        }

        // Make sure that there are enough bits left in the array to read an int
        if (bitIndex + 31 >= this.bitLength) {
            // If not, throw an exception
            throw new ArrayIndexOutOfBoundsException();
        }

        // Calculate the index of the first byte in the backing array
        int index = bitIndex / 8;

        // Create an integer to return, initialized at 0
        int ret = 0;

        // Repeating three times,
        for (int i = 0; i < 3; i++) {
            // Get a byte from the backing array, setting the 8 least significant bytes of
            // the return integer to it
            ret |= (int) this.back.get(index + i) & 0xFF;
            // Shift the return integer left by 8 to make room for the next byte
            ret = ret << 8;
        }
        // Get the last byte and add its bits to the return int
        ret |= (int) this.back.get(index + 3) & 0xFF;

        return ret;
    }

    /**
     * This method reads a byte from the array (8 bits)
     * 
     * @param bitIndex The index of the first bit to read
     * @return A byte containing the 8 bits read
     */
    public Byte readByte(int bitIndex) {
        // Make sure that there are enough bits left in the array to read a byte
        if (bitIndex + 7 >= this.bitLength) {
            // If not, throw an exception
            throw new ArrayIndexOutOfBoundsException();
        }

        // Create a return value initialized to 0
        int value = 0;

        // Repeating 8 times,
        for (int i = 0; i < 8; i++) {
            // Get the bit at the current index
            if (get(bitIndex + i)) {
                // If it's a 1, set the corresponding bit in the return value
                int mask = 1 << (7 - i);
                value = value | mask;
            }
            // Otherwise, we don't need to do anything because every bit already is 0
        }

        // Return the value
        return (byte) value;
    }

    /**
     * This method adds the bits of one BitArray to this one
     * 
     * @param other The BitArray to append
     */
    public void appendBits(BitArray other) {
        for (int i = 0; i < other.bitLength; i++) {
            push(other.get(i));
        }
    }
}
