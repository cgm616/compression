import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Level;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The GUI handler for the Expand tab, containing the logic required to power
 * that tab.
 */
public class Expand extends ColdenTab {
    public Expand(Stage stage) {
        super(stage);
    }

    @Override
    protected void doOperation() {
        log("Running expansion... (input: " + inputFile.getPath() + ", output: " + outputFile.getPath() + ")",
                Level.INFO);

        // Try to read the input file into memory
        byte[] inputData;
        try {
            inputData = Files.readAllBytes(inputFile.toPath());
        } catch (IOException e) {
            log("Input file could not be read: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        // Make sure the input file is long enough to be valid
        if (inputData.length <= 10) {
            log("Please select an input file that is longer than 10 bytes (the minimum compressed filesize). Aborting.",
                    Level.SEVERE);
            return;
        } else {
            log("Expanding " + inputData.length + " bytes...", Level.INFO);
        }

        // Try to parse a file from the data
        Artifact compressedData;
        try {
            compressedData = Artifact.fromBytes(inputData);
        } catch (Exception e) {
            log("The input file selected is malformed: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        byte[] treeData = compressedData.getSerializedTree();
        byte[] body = compressedData.getBody();

        log("Input file successfully parsed (tree length: " + treeData.length + ", body length: " + body.length
                + ")...", Level.INFO);

        // Try to deserialize a tree from the header
        Huffman expander;
        try {
            expander = Huffman.deserialize(treeData);
        } catch (Exception e) {
            log("Huffman tree could not be deserialized: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        log("Huffman tree successfully deserialized from file...", Level.INFO);

        // If the user wants to save a graph, do that
        if (saveGraph.isSelected()) {
            // Make sure a file was chosen
            if (graphFile == null) {
                log("No graph output file selected. Expansion will continue.", Level.WARNING);
            } else {
                // Make sure the file doesn't exist
                if (graphFile.exists()) {
                    log("Graph output file already exists. Graph will not be written.", Level.WARNING);
                } else {
                    // Try and write the graph
                    try {
                        Graph graph = new Graph(expander);
                        graph.write(graphFile);
                    } catch (IOException e) {
                        log("Could not write Huffman tree to graph (file: " + graphFile.getPath() + "): "
                                + e.getMessage() + ". Expansion will continue.", Level.WARNING);
                    }

                    log("Graph output file written...", Level.INFO);
                }
            }
        }

        // Try to expand the data and write it to a file
        try (OutputStream outStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            expander.expand(body, outStream);
            outStream.flush();
        } catch (Exception e) {
            log("Data could not be expanded: " + e.getMessage() + ". Aborting.", Level.SEVERE);
        }

        log("Expansion and output done.", Level.INFO);

        return;
    }

    @Override
    protected FileChooser createOutputChooser() {
        // Try to choose a good name based on the input
        if (inputFile != null && inputFile.getName().endsWith(".112")) {
            fileChooser.setInitialFileName(inputFile.getName().substring(0, inputFile.getName().length() - 4));
        }
        return fileChooser;
    }
}