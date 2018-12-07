import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import javafx.stage.Stage;

public class Expand extends ColdenTab {

    public Expand(Stage stage) {
        super(stage);
    }

    @Override
    public void run() {
        log("Running expansion... (input: " + this.input.getPath() + ", output:" + this.output.getPath() + ")",
                Level.INFO);

        byte[] inputData;

        try {
            inputData = Files.readAllBytes(this.input.toPath());
        } catch (IOException e) {
            log("Input file could not be read: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        if (inputData.length <= 10) {
            log("Please select an input file that is longer than 10 bytes (the minimum compressed filesize). Aborting.",
                    Level.SEVERE);
            return;
        } else {
            log("Expanding " + inputData.length + " bytes...", Level.INFO);
        }

        Artifact compressedData;

        try {
            compressedData = Artifact.fromBytes(inputData);
        } catch (Exception e) {
            log("The input file selected is malformed: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        byte[] tree = compressedData.getSerializedTree();
        byte[] body = compressedData.getBody();

        log("Input file successfully parsed (tree length: " + tree.length + ", body length: " + body.length + ")...",
                Level.INFO);

        Huffman expander;

        try {
            expander = Huffman.deserialize(tree);
        } catch (Exception e) {
            log("Huffman tree could not be deserialized: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        log("Huffman tree successfully deserialized from file...", Level.INFO);

        if (true) { // TODO: some checkbox for writing to a graph, as well as getting the file
            File graphOutput = new File("expand.dot");

            if (graphOutput.exists()) {
                log("Graph output file already exists. Graph will not be written.", Level.WARNING);
            } else {
                try {
                    Graph graph = new Graph(expander);
                    graph.write(graphOutput);
                } catch (IOException e) {
                    log("Could not write Huffman tree to graph (file: " + graphOutput.getPath() + "): " + e.getMessage()
                            + ". Expansion will continue.", Level.WARNING);
                }
            }

            log("Graph output file written...", Level.INFO);
        }

        byte[] outputData = expander.expand(body);

        log("Compressed input successfully expanded into " + outputData.length + " bytes...", Level.INFO);

        try {
            Artifact.writeBytes(output.toPath(), outputData);
        } catch (Exception e) {
            log("Could not write expanded file: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        log("Expansion and output done.", Level.INFO);
    }
}