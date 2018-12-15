package com.cgm616.colden;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * A visualization of the Huffman tree (known as a graph for the way it is
 * rendered).
 * 
 * Used
 * https://stackoverflow.com/questions/1494492/graphviz-how-to-go-from-dot-to-a-graph
 * for learning how to use graphviz Also
 * https://stackoverflow.com/questions/14958346/dot-dash-in-name and
 * https://stackoverflow.com/questions/1806870/how-to-add-edge-labels-in-graphviz
 */
public class Graph {
    Huffman tree;

    /**
     * Class constructor for the Graph
     * 
     * @param tree The Huffman tree to which this Graph refers
     * @return An instance of Graph that can print this tree
     */
    public Graph(Huffman tree) {
        this.tree = tree;
    }

    /**
     * This method writes a visualization of the tree to a file. It relies on the
     * presence of the `dot` command in the path to convert from a graphviz textual
     * representation to a pdf file
     * 
     * @param file The byte array input containing the data to be expanded
     * @exception IOException Thrown when the file already exists, or when IO errors
     */
    public void write(File file) throws IOException {
        // Check that the file doesn't already exist
        if (file.exists()) {
            // If it does, throw an exception
            throw new IOException("File " + file.getPath() + " already exists");
        }

        // Create and run a new process of `dot` that accepts input from this program
        // and outputs data into the file passed in
        ProcessBuilder pb = new ProcessBuilder("dot", "-Tpdf");
        pb.directory(file.getParentFile());
        pb.redirectOutput(file);
        Process p = pb.start();

        // Call the tree's internal method that writes a textual representation of
        // itself to a given writer, in this case passing in the stdin to the process of
        // `dot`
        this.tree.writeToGraph(new BufferedWriter(new OutputStreamWriter(p.getOutputStream(), StandardCharsets.UTF_8)));
    }
}