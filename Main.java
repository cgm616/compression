import javafx.animation.RotateTransition;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    Compress decryptPage;
    Expand encryptPage;
        
        @Override
    public void start(Stage stage) {
        // GUI code

        // Creating a Group object
        Group root = new Group(hexagon);

        // Creating a scene object
        Scene scene = new Scene(root, 600, 300);

        // Setting title to the Stage
        stage.setTitle("Compression");

        // Adding scene to the stage
        stage.setScene(scene);

        // Displaying the contents of the stage
        stage.show();
    }

    // Methods to detect when user input is happening

    public static void main(String args[]) {
        launch(args);
    }
}
