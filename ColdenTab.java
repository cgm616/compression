import java.io.File;
import java.util.logging.Level;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ColdenTab {
    Stage stage;

    File input;
    File output;
    File tree;

    GridPane grid;
    FileChooser fileChooser;

    TextField inputText;
    TextField outputText;
    TextField treeLocation;

    Button inputButton;
    Button outputButton;
    Button runButton;
    Button clearInputButton;
    Button clearOutputButton;
    Button clearLog;
    Button clearTreeLocation;
    Button treeButton;
    CheckBox saveGraph;
    TextArea log;

    // Constructor requires a Stage object
    public ColdenTab(Stage stage) {
        makeGUI(stage);
    }

    //
    /**
     * This method is called from the constructor to assign values and create the
     * layout of the GUI.
     * 
     * @param stage is the only parameter, it comes from the Main class to provide a
     *              background for the rest of the javafx content.
     * @return Nothing.
     */
    public void makeGUI(Stage stage) {

        // Defining fields - all fields are used in creating the GUI
        this.stage = stage;

        // The parent element for all of the GUI
        this.grid = new GridPane();

        // A handler for getting files from the OS
        this.fileChooser = new FileChooser();

        // The location of the input and output files
        this.inputText = new TextField();
        this.outputText = new TextField();

        // The buttons to choose the input and output files
        this.inputButton = new Button("Choose file");
        this.outputButton = new Button("Choose file");

        // The button to run the current operation
        this.runButton = new Button("Run Colden");

        // The buttons to clear the input and output files
        this.clearInputButton = new Button("Clear");
        this.clearOutputButton = new Button("Clear");

        // The button to clear the log window
        this.clearLog = new Button("Clear");

        // The button to clear the location of the saved graph, if one is saved
        this.clearTreeLocation = new Button("Clear");

        // The button to select a file to save the visualization into
        this.treeButton = new Button("Choose file");

        // The textarea to hold log output
        this.log = new TextArea();

        // The checkbox to choose if graphs are saved or not
        this.saveGraph = new CheckBox("Save Huffman Tree Graph");

        // The box to show where the graph is going to be saved
        this.treeLocation = new TextField();

        // Set spacing of grid, so that objects are visually distinct and neat-looking
        this.grid.setPadding(new Insets(10, 10, 10, 10)); // grid is contained by 10 pixels of empty space on all 4
                                                          // sides
        this.grid.setVgap(5); // Gap between objects above/below each other is 5 pixels
        this.grid.setHgap(5); // Gap between objects next to each other is 5 pixels

        this.inputText.setEditable(false);
        this.outputText.setEditable(false);
        this.treeLocation.setEditable(false);
        this.log.setWrapText(true);
        this.log.setEditable(false);

        // Adding objects to GridPane
        // This code adds the objects to the grid
        // in the correct order
        Label temp = new Label("Input");
        GridPane.setConstraints(temp, 0, 0);
        this.grid.getChildren().add(temp);

        GridPane.setConstraints(this.inputButton, 1, 0);
        this.grid.getChildren().add(this.inputButton);

        GridPane.setConstraints(this.inputText, 0, 1);
        this.grid.getChildren().add(this.inputText);

        GridPane.setConstraints(this.clearInputButton, 1, 1);
        this.grid.getChildren().add(this.clearInputButton);

        temp = new Label("Output");
        GridPane.setConstraints(temp, 0, 2);
        this.grid.getChildren().add(temp);

        GridPane.setConstraints(this.outputButton, 1, 2);
        this.grid.getChildren().add(this.outputButton);

        GridPane.setConstraints(this.outputText, 0, 3);
        this.grid.getChildren().add(this.outputText);

        GridPane.setConstraints(this.clearOutputButton, 1, 3);
        this.grid.getChildren().add(this.clearOutputButton);

        temp = new Label("Options");
        GridPane.setConstraints(temp, 0, 4);
        this.grid.getChildren().add(temp);

        GridPane.setConstraints(saveGraph, 0, 5);
        this.grid.getChildren().add(saveGraph);

        GridPane.setConstraints(treeButton, 1, 5);
        this.grid.getChildren().add(treeButton);

        GridPane.setConstraints(treeLocation, 0, 6);
        this.grid.getChildren().add(treeLocation);

        GridPane.setConstraints(clearTreeLocation, 1, 6);
        this.grid.getChildren().add(clearTreeLocation);

        GridPane.setConstraints(this.runButton, 0, 7);
        this.grid.getChildren().add(this.runButton);

        GridPane.setConstraints(this.log, 0, 8);
        this.grid.getChildren().add(this.log);

        GridPane.setConstraints(this.clearLog, 1, 8);
        this.grid.getChildren().add(this.clearLog);
        // End of adding objects to grid.

        // When clicked, inputButton will create a pop-up window
        // to let the user select an input file
        this.inputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            /**
             * This method creates a FileChooser to allow the user to select an input file
             * from File Explorer.
             * 
             * @param e is the only parameter, lets the method know why it's being
             *          called.(the button has been clicked)
             * @return nothing.
             */
            public void handle(final ActionEvent e) {
                FileChooser chooser = createInputChooser();
                File file = chooser.showOpenDialog(stage);
                if (file != null) {
                    input = file;
                    inputText.setText(input.getPath());
                }
            }
        });

        // When clicked, outputButton will create a pop-up window
        // to let the user select an output file
        this.outputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            /**
             * This method creates a FileChooser to allow the user to select an output file
             * from File Explorer.
             * 
             * @param e is the only parameter, lets the method know why it's being
             *          called.(the button has been clicked)
             * @return nothing.
             */
            public void handle(final ActionEvent e) {
                FileChooser chooser = createOutputChooser();
                File file = chooser.showSaveDialog(stage);
                if (file != null) {
                    output = file;
                    outputText.setText(output.getPath());
                }
            }
        });

        // When clicked, treeButton will create a pop-up window
        // to let the user select an output file
        this.treeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            /**
             * This method creates a FileChooser to allow the user to select a location for
             * the tree from File Explorer.
             * 
             * @param e is the only parameter, lets the method know why it's being
             *          called.(the button has been clicked)
             * @return nothing.
             */
            public void handle(final ActionEvent e) {
                FileChooser chooser = createOutputChooser();
                File file = chooser.showSaveDialog(stage);
                if (file != null) {
                    tree = file;
                    treeLocation.setText(tree.getPath());
                }
            }
        });

        // If input and output files have been selected, runButton
        // will perform the Huffman compression/ expansion
        this.runButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            /**
             * This method calls run(), which compresses/expands the input file in the
             * classes Compress and Expand.
             * 
             * @param e is the only parameter, lets the method know why it's being
             *          called.(the button has been clicked)
             * @return nothing.
             */
            public void handle(final ActionEvent e) {
                if (input != null && output != null) {
                    run();
                } else {
                    log("Please select an input and output file before running Colden.", Level.SEVERE);
                }
            }
        });

        // On click, clearInputButton clears the content of the inputText text field
        this.clearInputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            /**
             * This method clears the content of the inputText text field.
             * 
             * @param e is the only parameter, lets the method know why it's being
             *          called.(the button has been clicked)
             * @return nothing.
             */
            public void handle(final ActionEvent e) {
                inputText.setText(""); // Log is now emptied
                input = null;
            }
        });

        // On click, clearOutputButton clears the content of the outputText text field
        this.clearOutputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            /**
             * This method clears the content of the outputText text field.
             * 
             * @param e is the only parameter, lets the method know why it's being
             *          called.(the button has been clicked)
             * @return nothing.
             */
            public void handle(final ActionEvent e) {
                outputText.setText(""); // No selected output file exists now
                output = null;
            }
        });

        // On click, clearTreeLocation clears the content of the treeLocation text field
        this.clearTreeLocation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            /**
             * This method clears the content of the treeLocation text field.
             * 
             * @param e is the only parameter, lets the method know why it's being
             *          called.(the button has been clicked)
             * @return nothing.
             */
            public void handle(final ActionEvent e) {
                treeLocation.setText(""); // Log is now emptied
                tree = null;
            }
        });

        // On click, clearLog wipes the text stored in the log
        this.clearLog.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                log.setText(""); // Log is now emptied
            }
        });

        this.log.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                log.selectPositionCaret(log.getLength());
                log.deselect();
            }
        });
    }

    /**
     * This method is just a stub; it will be overridden in subclasses Compress and
     * Expand
     * 
     * @param none.
     * @return nothing.
     */
    public void run() {
    }

    /**
     * Returns the GridPane field to display on-screen in Main.
     * 
     * @param none.
     * @return Node - This returns the grid field.
     */
    public Node render() {
        return this.grid;
    }

    /**
     * Prints any input messages to the console and adds them to the log.
     * 
     * @param message is the first parameter, and is the content of the message to
     *                print.
     * @param prio    is the second, specifies the type of message being printed.
     * @return nothing.
     */
    public void log(String message, Level prio) {
        System.out.println("[ " + prio.getName() + " ]: " + message);
        this.log.appendText("[ " + prio.getName() + " ]: " + message + "\n");
    }

    /**
     * Gets the fileChooser field to select an input file.
     * 
     * @param none.
     * @return FileChooser - returns this object's fileChooser field
     */
    private FileChooser createInputChooser() {
        return this.fileChooser;
    }

    /**
     * Gets the fileChooser field to select an output file.
     * 
     * @param none.
     * @return FileChooser - returns this object's fileChooser field
     */
    private FileChooser createOutputChooser() {
        return this.fileChooser;
    }

}