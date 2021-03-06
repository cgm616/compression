package com.cgm616.colden;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class BitOutputStreamTest {
    @Test
    public void singleZeroBitWrite() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        out.write(false);
        out.flush();
        byte[] expected = { (byte) 0x00 };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }

    @Test
    public void singleOneBitWrite() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        out.write(true);
        out.flush();
        byte[] expected = { (byte) 0x80 };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }

    @Test
    public void singleByteWrite() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        out.write(0xAB);
        out.flush();
        byte[] expected = { (byte) 0xAB };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }

    @Test
    public void singleByteWriteAsBits() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        for (int i = 0; i < 8; i++) {
            out.write(true);
        }
        out.flush();
        byte[] expected = { (byte) 0xFF };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }

    @Test
    public void multipleBitWrite() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        for (int i = 0; i < 4; i++) {
            out.write(true);
            out.write(false);
        }
        for (int i = 0; i < 4; i++) {
            out.write(false);
            out.write(false);
        }
        for (int i = 0; i < 4; i++) {
            out.write(true);
            out.write(true);
        }
        out.flush();
        byte[] expected = { (byte) 0b10101010, (byte) 0b00000000, (byte) 0b11111111 };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }

    @Test
    public void largeMultipleBitWrite() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 4; i++) {
                out.write(true);
                out.write(false);
            }
            for (int i = 0; i < 4; i++) {
                out.write(false);
                out.write(false);
            }
            for (int i = 0; i < 4; i++) {
                out.write(true);
                out.write(true);
            }
        }
        out.flush();
        byte[] expected = { (byte) 0b10101010, (byte) 0b00000000, (byte) 0b11111111, (byte) 0b10101010,
                (byte) 0b00000000, (byte) 0b11111111, (byte) 0b10101010, (byte) 0b00000000, (byte) 0b11111111,
                (byte) 0b10101010, (byte) 0b00000000, (byte) 0b11111111, (byte) 0b10101010, (byte) 0b00000000,
                (byte) 0b11111111, (byte) 0b10101010, (byte) 0b00000000, (byte) 0b11111111, (byte) 0b10101010,
                (byte) 0b00000000, (byte) 0b11111111, (byte) 0b10101010, (byte) 0b00000000, (byte) 0b11111111 };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }

    @Test
    public void byteAlignedFiveByteWrite() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        out.write(0xFF);
        out.write(0x10);
        out.write(0xAB);
        out.write(0xAB);
        out.write(0xAB);
        out.flush();
        byte[] expected = { (byte) 0xFF, (byte) 0x10, (byte) 0xAB, (byte) 0xAB, (byte) 0xAB };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }

    @Test
    public void nonalignedByteWrite() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        out.write(true);
        out.write(false);
        out.write(true);
        out.write(0b01101001);
        out.write(0b01101001);
        out.write(0b00100111);
        out.write(0b10010101);
        out.write(0b00001001);
        out.flush();
        byte[] expected = { (byte) 0b10101101, (byte) 0b00101101, (byte) 0b00100100, (byte) 0b11110010,
                (byte) 0b10100001, (byte) 0b00100000 };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }

    @Test
    public void bitThenByteWrite() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        out.write(true);
        out.write(false);
        out.write(true);
        out.write(true);
        out.write(false);
        out.write(true);
        out.write(true);
        out.write(false);
        out.write(0x5A);
        out.write(0xFF);
        out.flush();
        byte[] expected = { (byte) 0b10110110, (byte) 0x5A, (byte) 0xFF };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }

    @Test
    public void writeSingleInt() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        out.writeInt(0xABCDEF01);
        out.flush();
        assertEquals(0, out.length);
        assertEquals(0, out.buffer);
        byte[] expected = { (byte) 0xAB, (byte) 0xCD, (byte) 0xEF, (byte) 0x01 };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }

    @Test
    public void flush() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        assertEquals(0, out.buffer);
        assertEquals(0, out.length);
        out.write(0xFF); // first byte
        assertEquals(0xFF << 24, out.buffer);
        assertEquals(8, out.length);
        out.write(0xFF); // second byte
        assertEquals(16, out.length);
        out.write(0xFF); // third byte
        assertEquals(24, out.length);
        out.write(0xFF); // length is now above 24, so the buffer should be flushed
        assertEquals(0xFF << 24, out.buffer);
        assertEquals(8, out.length);
        out.write(0xFF);
        out.flush();
        assertEquals(0, out.buffer);
        assertEquals(0, out.length);
        out.close();
    }

    @Test
    public void flushWithoutPadding() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        out.write(0xFF);
        out.write(false);
        out.write(true);
        out.write(true);
        assertEquals(11, out.length);
        out.flushWithoutPadding();
        assertEquals(3, out.length);
        byte[] expected = { (byte) 0xFF };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }
}
