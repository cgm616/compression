import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The GUI handler for the Compress tab, containing the logic required to power
 * that tab.
 */
public class Compress extends ColdenTab {

    public Compress(Stage stage) {
        super(stage);
    }

    @Override
    public void doOperation() {
        log("Running compression... (input: " + input.getPath() + ", output: " + output.getPath() + ")", Level.INFO);

        byte[] inputData;

        try {
            inputData = Files.readAllBytes(input.toPath());
        } catch (IOException e) {
            log("Input file could not be read: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        if (inputData.length < 1) {
            log("Please select an input file that is longer than 0 bytes. Aborting.", Level.SEVERE);
            return;
        } else {
            log("Compressing " + inputData.length + " bytes...", Level.INFO);
        }

        Huffman compressor;

        try {
            compressor = new Huffman(inputData);
        } catch (Exception e) {
            log("Huffman tree could not be made: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        log("Huffman tree successfully constructed...", Level.INFO);

        if (saveGraph.isSelected()) {
            if (graphFile == null) {
                log("No graph output file selected.", Level.WARNING);
            } else {
                if (graphFile.exists()) {
                    log("Graph output file already exists. Graph will not be written.", Level.WARNING);
                } else {
                    try {
                        Graph graph = new Graph(compressor);
                        graph.write(graphFile);
                    } catch (IOException e) {
                        log("Could not write Huffman tree to graph (file: " + graphFile.getPath() + "): "
                                + e.getMessage() + ".", Level.WARNING);
                    }

                    log("Graph output file written...", Level.INFO);
                }
            }
        }

        byte[] outputBody;

        try {
            outputBody = compressor.compress(inputData);
        } catch (Exception e) {
            log("Could not compress data with the Huffman tree: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        Artifact outputData = Artifact.build(compressor, outputBody);

        double compressionRatio = ((double) outputData.bytes.length * 8) / (double) inputData.length;

        log("Data successfully compressed with a ratio of " + compressionRatio + " bits per byte...", Level.INFO);

        try {
            outputData.writeToPath(output.toPath());
        } catch (IOException e) {
            log("Could not write compressed file: " + e.getMessage() + ". Aborting.", Level.SEVERE);
        }

        log("Compression and output done.", Level.INFO);

        return;
    }

    @Override
    public FileChooser createOutputChooser() {
        if (input != null) {
            fileChooser.setInitialFileName(input.getName() + ".112");
        } else {
            fileChooser.setInitialFileName("untitled.112");
        }

        return fileChooser;
    }
}