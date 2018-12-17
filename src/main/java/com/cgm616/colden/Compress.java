package com.cgm616.colden;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

        Huffman compressor;
        int inputLength;
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
            inputLength = (int) inputFile.length();

            // Make sure the data is more than 0 bytes
            if (inputLength < 1) {
                log("Please select an input file that is longer than 0 bytes. Aborting.", Level.SEVERE);
                return;
            } else {
                log("Compressing " + inputLength + " bytes...", Level.INFO);
            }

            // Try to construct a compressor using the data
            compressor = new Huffman(in);
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

        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            Artifact.writeHeader(out, compressor);

            try (BitOutputStream bitOut = new BitOutputStream(out)) {
                compressor.compress(in, bitOut, inputLength);
            } catch (Exception e) {
                log("Could not compress data with the Huffman tree: " + e.getMessage() + ". Aborting.", Level.SEVERE);
                return;
            }
        } catch (Exception e) {
            log("Could not create outout file: " + e.getMessage() + ". Aborting.", Level.SEVERE);
            return;
        }

        double compressionRatio = ((double) outputFile.length() * 8) / (double) inputLength;
        log("Data successfully compressed with a ratio of " + compressionRatio + " bits per byte...", Level.INFO);

        log("Compression and output done.", Level.INFO);
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