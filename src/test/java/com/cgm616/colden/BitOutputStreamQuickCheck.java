package com.cgm616.colden;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class BitOutputStreamQuickCheck {
    @Property
    public void randomByteArrayWrite(byte[] data) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        out.write(data);
        out.flush();
        assertArrayEquals(data, bytes.toByteArray());
        out.close();
    }

    @Property
    public void randomByteWrite(byte data) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        BitOutputStream out = new BitOutputStream(bytes);
        out.write(data);
        out.flush();
        byte[] expected = { data };
        assertArrayEquals(expected, bytes.toByteArray());
        out.close();
    }
}