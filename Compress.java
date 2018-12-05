import java.io.IOException;
import java.nio.file.Files;

import javafx.stage.Stage;

public class Compress extends ColdenTab {
    public Compress(Stage stage) {
        super(stage);
    }

    @Override
    public void run() {
        System.out.println("Running compression on " + this.input + " and outputting into " + this.output);

        byte[] inputData;

        try {
            inputData = Files.readAllBytes(this.input.toPath());
        } catch (IOException e) {
            System.out.println("There was an error");
            inputData = new byte[0];
        }

        Huffman compressor = new Huffman(inputData);

        byte[] outputBody = compressor.compress(inputData);

        Artifact outputData = Artifact.build(compressor, outputBody);

        outputData.writeToPath(this.output.toPath());
    }
}