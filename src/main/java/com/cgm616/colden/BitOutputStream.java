package com.cgm616.colden;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream extends FilterOutputStream {
    int buffer;
    int length;

    public BitOutputStream(OutputStream out) {
        super(out);
        this.buffer = 0;
        this.length = 0;
    }

    @Override
    public void write(int b) throws IOException {
        if (this.length >= 24) {
            commit();
        }

        this.buffer |= (b & 0xFF) << (24 - this.length);
        this.length += 8;
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        commit();
        int offset = length;
        int toWrite = buffer >> 24;
        for (int i = off; i < len; i++) {
            int data = (int) b[i] & 0xFF;
            toWrite |= data >> offset;
            super.write(toWrite);
            toWrite = (data << (8 - offset)) & 0xFF;
        }
        this.buffer = toWrite;
    }

    public void writeInt(int b) throws IOException {
        commit();
        this.buffer |= ((b >> 24) & 0xFF) << (24 - this.length);
        this.buffer |= ((b >> 16) & 0xFF) << (16 - this.length);
        this.buffer |= ((b >> 8) & 0xFF) << (8 - this.length);
        this.length += 24;
        commit();
        this.buffer |= (b & 0xFF) << (24 - this.length);
        this.length += 8;
    }

    @Override
    public void flush() throws IOException {
        commit();
        if (this.length > 0) {
            int toWrite = this.buffer >> 24;
            super.write(toWrite);
            this.length = 0;
            this.buffer = 0;
        }
        super.flush();
    }

    public void flushWithoutPadding() throws IOException {
        commit();
        super.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        super.close();
    }

    public void write(boolean b) throws IOException {
        if (this.length >= 24) {
            commit();
        }

        if (b) {
            int mask = 1 << (31 - this.length);
            this.buffer |= mask;
        }

        this.length += 1;
    }

    public void appendBits(BitArray bits) throws IOException {
        for (int i = 0; i < bits.bitLength; i++) {
            write(bits.get(i));
        }
    }

    private void commit() throws IOException {
        int num = this.length / 8;
        for (int i = 0; i < num; i++) {
            int toWrite = (this.buffer >> ((3 - i) * 8)) & 0xFF;
            super.write(toWrite);
        }
        this.buffer = this.buffer << (num * 8);
        this.length -= num * 8;
    }
}