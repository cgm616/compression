package com.cgm616.colden;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * A class that tests the functionality of a number of parts of the application
 */
public class Tester {
    public static void main(String[] args) throws Exception {
        testArtifact();
        testBitArray();
        testHuffman();
    }

    public static void testArtifact() throws Exception {
        byte[] input = { 0x31, 0x41, 0x59, 0x26, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00,
                0x00, 0x01, 0x00 };
        byte[] body = { 0x00, 0x00, 0x00, 0x01, 0x00 };
        byte[] tree = { 0x00, 0x00, 0x00 };

        Artifact art = Artifact.fromBytes(input);

        assert art.markerIndex == 7;
        assert Arrays.equals(art.getBody(), body);
        assert Arrays.equals(art.getSerializedTree(), tree);
    }

    public static void testBitArray() throws Exception {
        BitArray first = new BitArray();
        first.pushInt(0x77777777);
        for (int i = 0; i < 32; i++) {
            if (i % 4 == 0) {
                assert !first.get(i);
            } else {
                assert first.get(i);
            }
        }

        BitArray second = new BitArray();
        second.push(true); // 0
        second.push(false); // 1
        second.push(true); // 2
        second.push(true); // 3
        second.push(true); // 4
        second.push(false); // 5
        second.push(true); // 6
        second.push(true); // 7
        second.push(false); // 8
        second.push(false); // 9: { 0b10111011, 0b00000000 }

        assert second.get(0);
        assert !second.get(1);
        assert second.get(2);
        assert second.get(3);
        assert second.get(4);
        assert !second.get(5);
        assert second.get(6);
        assert second.get(7);

        assert !second.get(8);
        assert !second.get(9);

        second.push(false); // 10
        second.push(true); // 11 : { 0b10111011, 0b00010000 }

        assert second.get(0);
        assert !second.get(1);
        assert second.get(2);
        assert second.get(3);
        assert second.get(4);
        assert !second.get(5);
        assert second.get(6);
        assert second.get(7);

        assert !second.get(8);
        assert !second.get(9);
        assert !second.get(10);
        assert second.get(11);

        second.pushByte((byte) 0x76); // 12, 13, 14, 15, 16, 17, 18, 19, 20: { 0b10111011, 0b00010111, 0b01100000 }

        assert second.get(0);
        assert !second.get(1);
        assert second.get(2);
        assert second.get(3);
        assert second.get(4);
        assert !second.get(5);
        assert second.get(6);
        assert second.get(7);

        assert !second.get(8);
        assert !second.get(9);
        assert !second.get(10);
        assert second.get(11);
        assert !second.get(12);
        assert second.get(13);
        assert second.get(14);
        assert second.get(15);

        assert !second.get(16);
        assert second.get(17);
        assert second.get(18);
        assert !second.get(19);

        second.push(false);
        second.push(true);
        second.push(false);
        second.push(true);

        second.pushByte((byte) 0x76); // { 0b10111011, 0b00010111, 0b01100101, 0b01110110 }

        assert !second.get(16);
        assert second.get(17);
        assert second.get(18);
        assert !second.get(19);
        assert !second.get(20);
        assert second.get(21);
        assert !second.get(22);
        assert second.get(23);

        assert !second.get(24);
        assert second.get(25);
        assert second.get(26);
        assert second.get(27);
        assert !second.get(28);
        assert second.get(29);
        assert second.get(30);
        assert !second.get(31);

        second.push(true); // { 0b10111011, 0b00010111, 0b01100101, 0b01110110, 0b10000000 }

        assert !second.get(24);
        assert second.get(25);
        assert second.get(26);
        assert second.get(27);
        assert !second.get(28);
        assert second.get(29);
        assert second.get(30);
        assert !second.get(31);

        assert second.get(32);

        assert second.readByte(24) == 0b01110110;
    }

    public static void testHuffman() throws Exception {
        byte[] input = { 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x02, 0x02, 0x03 };

        Huffman tree = new Huffman(input);

        byte[] output = tree.compress(input);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        tree.expand(output, outStream);
        byte[] reexpanded = outStream.toByteArray();

        assert Arrays.equals(reexpanded, input);
        assert output.length > 4;
    }
}
