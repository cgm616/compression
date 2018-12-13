import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * Entry point of the application, run using JavaFX.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // Creating a Group object
        HBox root = new HBox();

        // Creating a scene object
        Scene scene = new Scene(root);

        // Instantiating Compress and Expand objects
        Compress compress = new Compress(stage);
        Expand expand = new Expand(stage);

        // Generating the GUI Tabs for compression and expansion. These are where all of
        // the logic happens
        Tab compressTab = createTab("Compress", compress.render());
        Tab expandTab = createTab("Expand", expand.render());

        // Organizing Tabs through a TabPane
        TabPane tabPane = new TabPane(compressTab, expandTab);

        HBox.setHgrow(tabPane, Priority.ALWAYS);

        // Adding the TabPane to the root
        root.getChildren().add(tabPane);

        // Setting title to the Stage
        stage.setTitle("Colden");

        // Window cannot be resized
        stage.setResizable(false);

        // Adding scene to the stage
        stage.setScene(scene);

        // Displaying the contents of the stage
        stage.show();
    }

    // Methods to detect when user input is happening

    public static void main(String args[]) {
        launch(args);
    }

    /**
     * Used to generate and format the "Compress" and "Expand" tabs of the GUI
     * 
     * @param name    The label of the tab when displayed on-screen
     * @param content The content that the tab will display. For the purposes of the
     *                project, it will always take a GridPane
     * @return A tab with the specified attributes
     */
    private Tab createTab(String name, Node content) {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setText(name);
        tab.setContent(content);
        return tab;
    }
}
