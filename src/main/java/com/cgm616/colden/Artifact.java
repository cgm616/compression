package com.cgm616.colden;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * Represents a file (an "Artifact") that is output or consumed by the program.
 * Basically, this class handles everything related to file formatting and
 * construction.
 */
public class Artifact {
    // The magic number at the start of every file (coincidentally, also the first 8
    // digits of if the hex digits are interpreted naively as decimal)
    public static final byte[] MAGIC = { 0x31, 0x41, 0x59, 0x26 };
    // The marker byte between the header and the body
    public static final byte MARKER = (byte) 0xFF;

    // The bytes that make up this file
    protected byte[] bytes;
    // The index of first byte of the three marker bytes
    protected int markerIndex;

    /**
     * Private class constructor that takes an array and an index to the first byte
     * of the header end marker. This is private because it can be easily misused,
     * especially if the wrong marker index is given
     * 
     * @param bytes       The array of information that make up the file
     * @param markerIndex The index into bytes that points to the first marker byte
     */
    private Artifact(byte[] bytes, int markerIndex) {
        this.bytes = bytes;
        this.markerIndex = markerIndex;
    }

    /**
     * This method takes in an array of bytes and interprets it as a file,
     * processing it and making it accessible to consumers as an instance of this
     * class
     * 
     * @param bytes The array of information that make up the file
     * @return The Artifact that has parsed this file
     * @exception Exception Thrown when the input does not represent a valid file
     */
    public static Artifact fromBytes(byte[] bytes) throws Exception {
        // Make sure that the input data is at least the minimum length for a compressed
        // file of this type
        // No artifact should ever be less than 10 bytes, which is the minimum
        // due to the magic number (2), the version (2), the tree (min 2), the header
        // end marker (3), and the compressed file (min 1).
        if (bytes.length <= 10) {
            // If it isn't, throw an exception
            throw new Exception("Not enough bytes to parse file");
        }

        // Try to find the marker bytes between the header and the body. To do this, we
        // iterate through the input, starting at the 5th element (the first four are
        // the magic number) and stopping when the marker is found
        int index = 4;
        while (true) {
            // If the input is not long enough to make the following check of the next two
            // bytes, the file is improperly constructed
            if (index + 2 >= bytes.length) {
                // This should occur when the marker never exists, so we throw an exception
                throw new Exception("File header end marker doesn't exist");
            }

            // Check if the current byte could be the start of the marker
            if (bytes[index] == (byte) MARKER) {
                // If it could, check that all three bytes of the marker are present
                if ((bytes[index + 1] == (byte) MARKER) && (bytes[index + 2] == (byte) MARKER)) {
                    // If they are, the current index is the index of the markers, so we break and
                    // set it that way
                    break;
                }
            }

            // Increment the index we are checking
            index += 1;
        }

        // Return a new Artifact constructed from the required parts
        return new Artifact(bytes, index);
    }

    /**
     * This method takes in an a tree and a body and builds a file for outputting
     * 
     * @param tree The tree to serialize into the beginning
     * @param body The compressed body
     * @return The Artifact that contains this information
     */
    public static Artifact build(Huffman tree, byte[] body) {
        // Build the file header
        byte[] header = buildHeader(tree);

        // Make a new array to hold the complete file info
        byte[] ret = Arrays.copyOf(header, header.length + body.length);

        // Copy the body info into a new array
        for (int i = 0; i < body.length; i++) {
            ret[header.length + i] = body[i];
        }

        // Create a new artifact from the data
        return new Artifact(ret, header.length - 3);
    }

    /**
     * This method takes in an a tree and builds a header
     * 
     * @param tree The tree to serialize into the beginning
     * @return The bytes of the header
     */
    private static byte[] buildHeader(Huffman tree) {
        // Serialize the tree
        byte[] treeData = tree.serialize();

        // Write the magic number into the header
        byte[] ret = Arrays.copyOf(Artifact.MAGIC, 4 + treeData.length + 3);

        // Write the data of the tree into the header
        for (int i = 0; i < treeData.length; i++) {
            ret[4 + i] = treeData[i];
        }

        // Add the header end markers
        ret[ret.length - 1] = Artifact.MARKER;
        ret[ret.length - 2] = Artifact.MARKER;
        ret[ret.length - 3] = Artifact.MARKER;

        // Return the header
        return ret;
    }

    /**
     * This method returns the tree part of the file
     * 
     * @return The bytes of the tree in the header
     */
    public byte[] getSerializedTree() {
        return Arrays.copyOfRange(this.bytes, 4, this.markerIndex);
    }

    /**
     * This method returns the compressed body of the file
     * 
     * @return The bytes of the body
     */
    public byte[] getBody() {
        return Arrays.copyOfRange(this.bytes, 3 + this.markerIndex, this.bytes.length);
    }

    /**
     * This method writes the file to a given path
     * 
     * @param path The path to write to
     */
    public void writeToPath(Path path) throws IOException {
        writeBytes(path, this.bytes);
    }

    /**
     * This method writes bytes to a given file
     * 
     * @param path  The path to write to
     * @param bytes The bytes to write
     * @exception IOException Thrown when the file cannot be written
     */
    public static void writeBytes(Path path, byte[] bytes) throws IOException {
        Files.write(path, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }

    public static void writeHeader(OutputStream out, Huffman tree) throws IOException {
        out.write(Artifact.MAGIC);
        out.write(tree.serialize());
        out.write(Artifact.MARKER);
        out.write(Artifact.MARKER);
        out.write(Artifact.MARKER);
    }
}
