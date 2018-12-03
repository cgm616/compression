import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Files;

public class Artifact {
    public static final byte[] MAGIC = { 0x31, 0x41, 0x59, 0x26 };
    public static final byte MARKER = (byte) 0xFF;

    private byte[] bytes;
    private int markerIndex; // Index of first byte of 0xFFFFFF

    private Artifact(byte[] bytes, int markerIndex) {
        this.bytes = bytes;
        this.markerIndex = markerIndex;
    }

    public static Artifact fromBytes(byte[] bytes) {
        if (bytes.length < 7) {
            return null; // TODO: this is an error case.
            // No artifact should ever be less than 7 bytes, which is the minimum
            // due to the magic number, the version, and the header end marker.
        }

        int index = 4;

        while (true) {
            if (index + 2 >= bytes.length) {
                return null; // TODO: error handling
                // This should occur when the marker never exists.
            }

            if (bytes[index] == (byte) 0xFF) {
                if (bytes[index + 1] == (byte) 0xFF && bytes[index + 2] == (byte) 0xFF) {
                    break;
                }
            }

            index += 1;
        }

        return new Artifact(bytes, index);
    }

    public static Artifact build(Huffman tree, byte[] body) {
        byte[] header = buildHeader(tree);
        byte[] ret = Arrays.copyOf(header, header.length + body.length);

        for (int i = 0; i < body.length; i++) {
            ret[header.length + i] = body[i];
        }

        return new Artifact(ret, header.length + 4);
    }

    private static byte[] buildHeader(Huffman tree) {
        byte[] treeData = tree.serialize();
        byte[] ret = Arrays.copyOf(Artifact.MAGIC, 4 + treeData.length + 3);

        for (int i = 0; i < treeData.length; i++) {
            ret[4 + i] = treeData[i];
        }

        ret[ret.length - 1] = Artifact.MARKER;
        ret[ret.length - 2] = Artifact.MARKER;
        ret[ret.length - 3] = Artifact.MARKER;

        return ret;
    }

    public byte[] getSerializedTree() {
        return Arrays.copyOfRange(this.bytes, 4, this.markerIndex);
    }

    public byte[] getBody() {
        return Arrays.copyOfRange(this.bytes, 4 + this.markerIndex, this.bytes.length - 1);
    }

    public void writeToPath(Path path) {
        writeBytes(path, this.bytes);
    }

    public static void writeBytes(Path path, byte[] bytes) {
        try {
            Files.write(path, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.out.println("There was an error"); // TODO: make this better
        }
    }
}