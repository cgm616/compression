import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Level;

import javafx.concurrent.Task;
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
    public void run() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                log("Running expansion... (input: " + input.getPath() + ", output: " + output.getPath() + ")",
                        Level.INFO);

                byte[] inputData;

                try {
                    inputData = Files.readAllBytes(input.toPath());
                } catch (IOException e) {
                    log("Input file could not be read: " + e.getMessage() + ". Aborting.", Level.SEVERE);
                    return null;
                }

                if (inputData.length <= 10) {
                    log("Please select an input file that is longer than 10 bytes (the minimum compressed filesize). Aborting.",
                            Level.SEVERE);
                    return null;
                } else {
                    log("Expanding " + inputData.length + " bytes...", Level.INFO);
                }

                Artifact compressedData;

                try {
                    compressedData = Artifact.fromBytes(inputData);
                } catch (Exception e) {
                    log("The input file selected is malformed: " + e.getMessage() + ". Aborting.", Level.SEVERE);
                    return null;
                }

                byte[] treeData = compressedData.getSerializedTree();
                byte[] body = compressedData.getBody();

                log("Input file successfully parsed (tree length: " + treeData.length + ", body length: " + body.length
                        + ")...", Level.INFO);

                Huffman expander;

                try {
                    expander = Huffman.deserialize(treeData);
                } catch (Exception e) {
                    log("Huffman tree could not be deserialized: " + e.getMessage() + ". Aborting.", Level.SEVERE);
                    return null;
                }

                log("Huffman tree successfully deserialized from file...", Level.INFO);

                if (saveGraph.isSelected()) { // TODO: some checkbox for writing to a graph, as well as getting the
                                              // file
                    File graphOutput = new File(tree.getPath().substring(0, tree.getPath().length() - 3) + "pdf");

                    if (graphOutput.exists()) {
                        log("Graph output file already exists. Graph will not be written.", Level.WARNING);
                    } else {
                        try {
                            Graph graph = new Graph(expander);
                            graph.write(graphOutput);
                        } catch (IOException e) {
                            log("Could not write Huffman tree to graph (file: " + graphOutput.getPath() + "): "
                                    + e.getMessage() + ". Expansion will continue.", Level.WARNING);
                        }
                    }

                    log("Graph output file written...", Level.INFO);
                }

                try (OutputStream outStream = new BufferedOutputStream(new FileOutputStream(output))) {
                    expander.expand(body, outStream);
                    outStream.flush();
                } catch (Exception e) {
                    log("Data could not be expanded: " + e.getMessage() + ". Aborting.", Level.SEVERE);
                }

                log("Expansion and output done.", Level.INFO);

                return null;
            }
        };

        task.setOnFailed(evt -> {
            log("Expansion failed on the worker thread: " + task.getException().getMessage(), Level.SEVERE);
            task.getException().printStackTrace(System.err);
        });

        new Thread(task).start();
    }
}