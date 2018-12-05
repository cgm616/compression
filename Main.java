import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // Creating a Group object
        HBox root = new HBox();

        // Creating a scene object
        Scene scene = new Scene(root);

        Compress compress = new Compress(stage);
        Expand expand = new Expand(stage);

        Tab compressTab = createTab("Compress", compress.render());
        Tab expandTab = createTab("Expand", expand.render());

        TabPane tabPane = new TabPane(compressTab, expandTab);

        HBox.setHgrow(tabPane, Priority.ALWAYS);

        root.getChildren().add(tabPane);

        // Setting title to the Stage
        stage.setTitle("Colden");

        // Adding scene to the stage
        stage.setScene(scene);

        // Displaying the contents of the stage
        stage.show();
    }

    // Methods to detect when user input is happening

    public static void main(String args[]) {
        launch(args);
    }

    private Tab createTab(String name, Node content) {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.setText(name);
        tab.setContent(content);
        return tab;
    }
}
