import java.io.IOException;
import java.nio.file.Files;

import javafx.stage.Stage;

public class Expand extends ColdenTab {
    public Expand(Stage stage) {
        super(stage);
    }

    @Override
    public void run() {
        System.out.println("Running expansion on " + this.input + " and outputting into " + this.output);

        byte[] inputData;

        try {
            inputData = Files.readAllBytes(this.input.toPath());
        } catch (IOException e) {
            System.out.println("There was an error");
            inputData = new byte[0];
        }

        System.out.println("Input data length: " + inputData.length);

        Artifact compressedData = Artifact.fromBytes(inputData);

        byte[] tree = compressedData.getSerializedTree();
        byte[] body = compressedData.getBody();

        System.out.println("Serialized tree length: " + tree.length);
        System.out.println("Compressed body length: " + body.length);

        Huffman expander = Huffman.deserialize(tree);
        byte[] outputData = expander.expand(body);

        System.out.println("Output data length: " + outputData.length);

        Artifact.writeBytes(output.toPath(), outputData);
    }
}