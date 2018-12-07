import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class Graph {
    Huffman tree;

    public Graph(Huffman tree) {
        this.tree = tree;
    }

    public void write(File file) throws IOException {
        // Make new process of `dot` and then feed it the written graph to directly
        // produce pdfs.

        if (file.exists()) {
            throw new IOException("File " + file.getPath() + " already exists");
        }

        ProcessBuilder pb = new ProcessBuilder("dot", "-Tpdf");
        pb.directory(file.getParentFile());
        pb.redirectOutput(file);
        Process p = pb.start();

        this.tree.writeToGraph(new BufferedWriter(new OutputStreamWriter(p.getOutputStream(), StandardCharsets.UTF_8)));
    }
}