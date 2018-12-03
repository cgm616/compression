import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Files;

public class Artifact {
    public static final byte[] MAGIC = { 0x31, 0x41, 0x59, 0x26 };

    private byte[] bytes;

    public Artifact(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Artifact build(Huffman tree, byte[] body) {
        byte[] header = buildHeader(tree);
        byte[] ret = Arrays.copyOf(header, header.length + body.length);

        for (int i = 0; i < body.length; i++) {
            ret[header.length + i] = body[i];
        }

        return new Artifact(ret);
    }

    private static byte[] buildHeader(Huffman tree) {
        byte[] treeData = tree.serialize();
        byte[] ret = Arrays.copyOf(Artifact.MAGIC, 4 + treeData.length + 3);

        for (int i = 0; i < treeData.length; i++) {
            ret[4 + i] = treeData[i];
        }

        ret[ret.length - 1] = (byte) 0xFF;
        ret[ret.length - 2] = (byte) 0xFF;
        ret[ret.length - 3] = (byte) 0xFF;

        return ret;
    }

    public void writeToPath(Path path) {
        try {
            Files.write(path, this.bytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.out.println("There was an error");
        }
    }
}