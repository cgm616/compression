import java.io.File;
import java.util.logging.Level;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ColdenTab {
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
	
	CheckBox saveGraph;

    public ColdenTab(Stage stage) {
        makeGUI(stage);
    }

    public void makeGUI(Stage stage) {
        this.stage = stage;

        this.grid = new GridPane();
        this.fileChooser = new FileChooser();
        this.inputText = new TextField();
        this.outputText = new TextField();
        this.inputButton = new Button("Choose file");
        this.outputButton = new Button("Choose file");
        this.runButton = new Button("Run Colden");
		this.saveGraph = new CheckBox("Save Huffman Tree Graph");

        this.grid.setPadding(new Insets(10, 10, 10, 10));
        this.grid.setVgap(5);
        this.grid.setHgap(5);

        this.inputText.setDisable(true);
        this.outputText.setDisable(true);
		
		// Adding objects to GridPane
		Label temp = new Label("Input");
		GridPane.setConstraints(temp, 0, 0);
		this.grid.getChildren().add(temp);
		
        GridPane.setConstraints(this.inputButton, 1, 0);
        this.grid.getChildren().add(this.inputButton);

		GridPane.setConstraints(this.inputText, 0, 1);
        GridPane.setHgrow(this.inputText, Priority.ALWAYS);
        this.grid.getChildren().add(this.inputText);
		
		temp = new Label("Output");
		GridPane.setConstraints(temp, 0, 2);
		this.grid.getChildren().add(temp);
		
        GridPane.setConstraints(this.outputButton, 1, 2);
        this.grid.getChildren().add(this.outputButton);
		
		GridPane.setConstraints(this.outputText, 0, 3);
        this.grid.getChildren().add(this.outputText);

        GridPane.setConstraints(this.runButton, 0, 4);
        this.grid.getChildren().add(this.runButton);

        temp = new Label("Options");
		GridPane.setConstraints(temp, 0, 5);
		this.grid.getChildren().add(temp);

        GridPane.setConstraints(saveGraph, 0, 6);
		this.grid.getChildren().add(saveGraph);

        this.inputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                FileChooser chooser = createInputChooser();
                File file = chooser.showOpenDialog(stage);
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
                File file = chooser.showSaveDialog(stage);
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
                    run();
                } else {
                    // TODO: show error
                }
            }
        });
    }

    public void run() {
    }

    public Node render() {
        return this.grid;
    }

    public void log(String message, Level prio) {
        System.out.println("[ " + prio.getName() + " ]: " + message);
    }

    private FileChooser createInputChooser() {
        return this.fileChooser;
    }

    private FileChooser createOutputChooser() {
        return this.fileChooser;
    }

}