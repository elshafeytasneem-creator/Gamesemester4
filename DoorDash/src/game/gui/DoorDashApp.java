package game.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DoorDashApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("door_dash_style.css").toExternalForm());

        SceneManager sceneManager = new SceneManager(primaryStage, scene);
        sceneManager.showMainMenu();

        primaryStage.setTitle("DooR DasH: Scare vs Laugh Touchdown");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(720);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
