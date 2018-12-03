import javafx.stage.Stage;
import java.nio.file.Files;
import java.io.IOException;

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

        Artifact compressedData = Artifact.fromBytes(inputData);

        Huffman expander = Huffman.deserialize(compressedData.getSerializedTree());

        byte[] outputData = expander.expand(compressedData.getBody());

        Artifact.writeBytes(output.toPath(), outputData);
    }
}