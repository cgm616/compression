package com.cgm616.colden;

import java.io.File;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The superclass for each of the GUI tabs, handling and holding all
 * GUI-specific functionality that is common between the two.
 * 
 * JavaFX information from
 * https://docs.oracle.com/javase/8/javafx/user-interface-tutorial
 */
public class ColdenTab {
    private Stage stage; // The JavaFX stage (needed to open fileChoosers)

    protected File inputFile; // The file to accept input from
    protected File outputFile; // The file to output to
    protected File graphFile; // The file to write the graph to (if enabled)

    private GridPane grid; // The root GUI node for everything else

    protected FileChooser fileChooser; // The instance of the class that lets us choose files

    private TextField inputText; // The texfield that will display the path of the input file
    private TextField outputText; // Same for the output file
    private TextField graphText; // Same for the graph file

    private Button inputButton; // The button to choose the input
    private Button outputButton; // Same for output
    private Button graphButton; // Same for graph

    private Button clearInputButton; // The button to reset the input
    private Button clearOutputButton; // Same for output
    private Button clearGraphButton; // Same for graph

    protected CheckBox saveGraph; // The checkbox if saving the graph is enabled
    private Button runButton; // The button to run the operation

    private TextArea log; // The text box that shows the application log
    private Button clearLog; // The button to clear the log

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
    private void makeGUI(Stage stage) {
        // Initialize all GUI elements
        this.stage = stage;

        this.grid = new GridPane();

        this.fileChooser = new FileChooser();

        this.inputText = new TextField();
        this.outputText = new TextField();
        this.graphText = new TextField();

        this.inputButton = new Button("Choose file");
        this.outputButton = new Button("Choose file");
        this.graphButton = new Button("Choose file");

        this.clearInputButton = new Button("Clear");
        this.clearOutputButton = new Button("Clear");
        this.clearGraphButton = new Button("Clear");

        this.runButton = new Button("Run Colden");
        this.saveGraph = new CheckBox("Save Huffman Tree Graph");

        this.log = new TextArea();
        this.clearLog = new Button("Clear");
        // End of initialization

        // Set spacing of grid, so that objects are visually distinct and neat-looking.
        // Grid is contained by 10 pixels of empty space on all 4 sides
        this.grid.setPadding(new Insets(10, 10, 10, 10));
        this.grid.setVgap(5); // Gap between objects above/below each other is 5 pixels
        this.grid.setHgap(5); // Gap between objects next to each other is 5 pixels

        // Make sure users cannot edit the fields used to display the file paths
        this.inputText.setEditable(false);
        this.outputText.setEditable(false);
        this.graphText.setEditable(false);

        // Make sure users cannot edit the log, and make it display correctly
        this.log.setWrapText(true);
        this.log.setEditable(false);

        // Add GUI elements to the GridPane in the correct location
        Label temp = new Label("Input"); // Create a new label and add it
        GridPane.setConstraints(temp, 0, 0);
        this.grid.getChildren().add(temp);

        // Add the input controls
        GridPane.setConstraints(this.inputButton, 1, 0);
        this.grid.getChildren().add(this.inputButton);

        GridPane.setConstraints(this.inputText, 0, 1);
        this.grid.getChildren().add(this.inputText);

        GridPane.setConstraints(this.clearInputButton, 1, 1);
        this.grid.getChildren().add(this.clearInputButton);

        temp = new Label("Output"); // Create a new label and add it
        GridPane.setConstraints(temp, 0, 2);
        this.grid.getChildren().add(temp);

        // Add the output controls
        GridPane.setConstraints(this.outputButton, 1, 2);
        this.grid.getChildren().add(this.outputButton);

        GridPane.setConstraints(this.outputText, 0, 3);
        this.grid.getChildren().add(this.outputText);

        GridPane.setConstraints(this.clearOutputButton, 1, 3);
        this.grid.getChildren().add(this.clearOutputButton);

        // Add a spacing row between the necessary file choosing elements and the
        // optional graph part
        this.grid.addRow(4, new Text(""));

        temp = new Label("Options"); // Create a new label and add it
        GridPane.setConstraints(temp, 0, 5);
        this.grid.getChildren().add(temp);

        // Add the graph controls
        GridPane.setConstraints(saveGraph, 0, 6);
        this.grid.getChildren().add(saveGraph);

        GridPane.setConstraints(graphButton, 1, 6);
        this.grid.getChildren().add(graphButton);

        GridPane.setConstraints(graphText, 0, 7);
        this.grid.getChildren().add(graphText);

        GridPane.setConstraints(clearGraphButton, 1, 7);
        this.grid.getChildren().add(clearGraphButton);

        // Add another spacing row before the run button
        this.grid.addRow(8, new Text(""));

        GridPane.setConstraints(this.runButton, 0, 9);
        this.grid.getChildren().add(this.runButton);

        GridPane.setConstraints(this.log, 0, 10);
        this.grid.getChildren().add(this.log);

        GridPane.setConstraints(this.clearLog, 1, 10);
        this.grid.getChildren().add(this.clearLog);
        // End of adding objects to grid.

        // When clicked, inputButton will create a pop-up window
        // to let the user select an input file
        //
        // Consulted https://docs.oracle.com/javafx/2/events/handlers.htm for the basic
        // information on how to make event handlers
        this.inputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                // Show a new file open dialog
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    // Make sure the file isn't null, then update and show the file
                    inputFile = file;
                    inputText.setText(inputFile.getPath());
                }
            }
        });

        // When clicked, outputButton will create a pop-up window
        // to let the user select an output file
        this.outputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                // Create and show a new file save dialog
                FileChooser chooser = createOutputChooser();
                File file = chooser.showSaveDialog(stage);
                if (file != null) {
                    // Make sure the file isn't null, then update and show the file
                    outputFile = file;
                    outputText.setText(outputFile.getPath());
                }
            }
        });

        // When clicked, graphButton will create a pop-up window
        // to let the user select an output file
        this.graphButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                // Create and show a new file save dialog
                FileChooser chooser = createGraphChooser();
                File file = chooser.showSaveDialog(stage);
                if (file != null) {
                    // Make sure the file isn't null, then update and show the file
                    graphFile = file;
                    graphText.setText(graphFile.getPath());
                }
            }
        });

        // If input and output files have been selected, runButton
        // will perform the Huffman compression/ expansion
        //
        // Tasks learned here:
        // https://stackoverflow.com/questions/33112560/why-is-a-thread-blocking-my-javafx-ui-thread
        this.runButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                System.out.println("Running");
                // First make sure the user has chosen files to operate on
                if (inputFile != null && outputFile != null) {
                    // Now, make sure that the input file exists
                    if (!inputFile.exists()) {
                        log("Please select an input file that exists. Aborting.", Level.SEVERE);
                        return;
                    }

                    // and that the output file doesn't
                    if (outputFile.exists()) {
                        log("Output file already exists. Aborting.", Level.SEVERE);
                        return;
                    }

                    // Disable the run button to prevent this method from running again
                    runButton.setDisable(true);

                    // Create a new task on a worker thread to run the operation
                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            doOperation();
                            return null;
                        }
                    };

                    // If the task fails, log the error and show a stack trace
                    //
                    // Learned here:
                    // https://stackoverflow.com/questions/40300089/how-to-catch-the-task-exception-in-java-fx-application
                    task.setOnFailed(evt -> {
                        log("Operation failed on the worker thread: " + task.getException().getMessage(), Level.SEVERE);
                        task.getException().printStackTrace(System.err);
                    });

                    // If the task succeeds, re-enable the run button
                    task.setOnSucceeded(evt -> {
                        runButton.setDisable(false);
                    });

                    // Start the task on a new thread
                    new Thread(task).start();
                } else {
                    // If the files haven't been chosen, display an error
                    log("Please select an input and output file before running Colden.", Level.SEVERE);
                }
            }
        });

        // On click, clearInputButton clears the content of the inputText text field
        this.clearInputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                inputText.setText(""); // Text field is now emptied
                inputFile = null; // File is no longer chosen
            }
        });

        // On click, clearOutputButton clears the content of the outputText text field
        this.clearOutputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                outputText.setText(""); // Text field is now emptied
                outputFile = null; // File is no longer chosen
            }
        });

        // On click, clearGraphButton clears the content of the graphText text field
        this.clearGraphButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                graphText.setText(""); // Text field is now emptied
                graphFile = null; // File is no longer chosen
            }
        });

        // On click, clearLog wipes the text stored in the log
        this.clearLog.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                log.setText(""); // Log is now emptied
            }
        });

        // When the log is updated, try to scroll to the bottom. For some reason, this
        // doesn't work the first time that the log fills up past the window
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
    protected void doOperation() {
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
    protected void log(String message, Level prio) {
        // Run this on the UI thread to ensure that when it's called from the
        // doOperation() method, it doesn't break
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Log text to both stdout and the log on-screen
                System.out.println("[ " + prio.getName() + " ]: " + message);
                log.appendText("[ " + prio.getName() + " ]: " + message + "\n");
            }
        });
    }

    /**
     * Gets the fileChooser field to select an output file. Should be overriden by
     * subclasses
     * 
     * @param none.
     * @return FileChooser - returns this object's fileChooser field
     */
    protected FileChooser createOutputChooser() {
        return this.fileChooser;
    }

    /**
     * Gets the fileChooser field to select a graph output file.
     * 
     * @param none.
     * @return FileChooser - returns this object's fileChooser field
     */
    private FileChooser createGraphChooser() {
        this.fileChooser.setInitialFileName("graph.pdf");
        return this.fileChooser;
    }
}