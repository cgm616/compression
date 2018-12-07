import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class Artifact {
    public static final byte[] MAGIC = { 0x31, 0x41, 0x59, 0x26 };
    public static final byte MARKER = (byte) 0xFF;

    protected byte[] bytes;
    protected int markerIndex; // Index of first byte of 0xFFFFFF

    private Artifact(byte[] bytes, int markerIndex) {
        this.bytes = bytes;
        this.markerIndex = markerIndex;
    }

    public static Artifact fromBytes(byte[] bytes) throws Exception {
        if (bytes.length <= 10) {
            throw new Exception("Not enough bytes to parse file");
            // No artifact should ever be less than 10 bytes, which is the minimum
            // due to the magic number (2), the version (2), the tree (min 2), the header
            // end marker (3), and the compressed file (min 1).
        }

        int index = 4;

        while (true) {
            if (index + 2 >= bytes.length) {
                throw new Exception("File header end marker doesn't exist");
                // This should occur when the marker never exists.
            }

            if (bytes[index] == (byte) 0xFF) {
                if ((bytes[index + 1] == (byte) 0xFF) && (bytes[index + 2] == (byte) 0xFF)) {
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

        return new Artifact(ret, header.length - 3);
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
        return Arrays.copyOfRange(this.bytes, 3 + this.markerIndex, this.bytes.length);
    }

    public void writeToPath(Path path) throws IOException {
        writeBytes(path, this.bytes);
    }

    public static void writeBytes(Path path, byte[] bytes) throws IOException {
        Files.write(path, bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }
}
