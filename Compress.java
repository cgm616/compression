import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.geometry.Insets;
import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

public class Compress {
    Stage stage;

    File input;
    File output;

    GridPane grid;
    FileChooser fileChooser;

    TextField inputText;
    TextField outputText;

    Button inputButton;
    Button outputButton;
    Button runButton;

    public Compress(Stage stage) {
        this.stage = stage;

        this.grid = new GridPane();
        this.fileChooser = new FileChooser();
        this.inputText = new TextField();
        this.outputText = new TextField();
        this.inputButton = new Button("Select input file");
        this.outputButton = new Button("Select output file");
        this.runButton = new Button("Run compression");

        this.grid.setPadding(new Insets(10, 10, 10, 10));
        this.grid.setVgap(5);
        this.grid.setHgap(5);

        this.inputText.setDisable(true);
        this.outputText.setDisable(true);

        GridPane.setConstraints(this.inputButton, 0, 0);
        this.grid.getChildren().add(this.inputButton);

        GridPane.setConstraints(this.outputButton, 0, 1);
        this.grid.getChildren().add(this.outputButton);

        GridPane.setConstraints(this.runButton, 0, 2);
        this.grid.getChildren().add(this.runButton);

        GridPane.setConstraints(this.inputText, 1, 0);
        GridPane.setHgrow(this.inputText, Priority.ALWAYS);
        this.grid.getChildren().add(this.inputText);

        GridPane.setConstraints(this.outputText, 1, 1);
        this.grid.getChildren().add(this.outputText);

        this.inputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                FileChooser chooser = createInputChooser();
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    input = file;
                    inputText.setText(input.getPath());
                }
            }
        });

        this.outputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                FileChooser chooser = createOutputChooser();
                File file = fileChooser.showSaveDialog(stage);
                if (file != null) {
                    output = file;
                    outputText.setText(output.getPath());
                }
            }
        });

        this.runButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                if (input != null && output != null) {
                    System.out.println("Running compression on " + input + " and outputting into " + output);
                    run();
                } else {
                    // TODO: show error
                }
            }
        });
    }

    private void run() {
        Huffman tree = new Huffman(new byte[1]);
        Artifact file = new Artifact(new byte[1]);
        // TODO: finish logic here
    }

    public Node render() {
        return this.grid;
    }

    private FileChooser createInputChooser() {
        return this.fileChooser;
    }

    private FileChooser createOutputChooser() {
        return this.fileChooser;
    }
}