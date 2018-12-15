package com.cgm616.colden;

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
    protected void doOperation() {
        log("Running compression... (input: " + inputFile.getPath() + ", output: " + outputFile.getPath() + ")",
                Level.INFO);

        // Try to get input data from file, reading it into memory
        byte[] inputData;
        try {
            inputData = Files.readAllBytes(inputFile.toPath());
        } catch (IOException e) {
            log("Input file could not be read: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        // Make sure the data is more than 0 bytes
        if (inputData.length < 1) {
            log("Please select an input file that is longer than 0 bytes. Aborting.", Level.SEVERE);
            return;
        } else {
            log("Compressing " + inputData.length + " bytes...", Level.INFO);
        }

        // Try to construct a compressor using the data
        Huffman compressor;
        try {
            compressor = new Huffman(inputData);
        } catch (Exception e) {
            log("Huffman tree could not be made: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        log("Huffman tree successfully constructed...", Level.INFO);

        // Check if the user wants to save a graphviz version of the internal Huffman
        // tree
        if (saveGraph.isSelected()) {
            // Make sure a graph output file is actually selected
            if (graphFile == null) {
                log("No graph output file selected.", Level.WARNING);
            } else {
                // Make sure the file doesn't exist
                if (graphFile.exists()) {
                    log("Graph output file already exists. Graph will not be written.", Level.WARNING);
                } else {
                    // Try to write the graph
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

        // Try to compress the input data with the constructed tree
        byte[] outputBody;
        try {
            outputBody = compressor.compress(inputData);
        } catch (Exception e) {
            log("Could not compress data with the Huffman tree: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        // Build a new output file from the compressed data and the tree
        Artifact outputData = Artifact.build(compressor, outputBody);

        // Compute and log a compression ratio
        double compressionRatio = ((double) outputData.bytes.length * 8) / (double) inputData.length;
        log("Data successfully compressed with a ratio of " + compressionRatio + " bits per byte...", Level.INFO);

        // Try to write the file to the output path
        try {
            outputData.writeToPath(outputFile.toPath());
        } catch (IOException e) {
            log("Could not write compressed file: " + e.getMessage() + ". Aborting.", Level.SEVERE);
        }

        log("Compression and output done.", Level.INFO);

        return;
    }

    @Override
    protected FileChooser createOutputChooser() {
        // Try to choose an output file name smartly. If it can't, at least suggest a
        // file ending
        if (inputFile != null) {
            fileChooser.setInitialFileName(inputFile.getName() + ".112");
        } else {
            fileChooser.setInitialFileName("untitled.112");
        }

        return fileChooser;
    }
}