package game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import game.engine.Game;

public class SceneManager {

    private final Stage primaryStage;
    private final Scene globalScene;

    public SceneManager(Stage primaryStage, Scene globalScene) {
        this.primaryStage = primaryStage;
        this.globalScene = globalScene;
        
        // Apply the global stylesheet to the scene
        String cssPath = getClass().getResource("door_dash_style.css").toExternalForm();
        if (!this.globalScene.getStylesheets().contains(cssPath)) {
            this.globalScene.getStylesheets().add(cssPath);
        }
    }

    public void showMainMenu() {
        StackPane root = (StackPane) globalScene.getRoot();
        root.getChildren().clear();
        root.getChildren().add(new MainMenuPane(this));
    }

    public void showCharacterSelection() {
        StackPane root = (StackPane) globalScene.getRoot();
        root.getChildren().clear();
        root.getChildren().add(new CharacterSelectionPane(this));
    }

    public void showGameBoard(Game gameSession) {
        StackPane root = (StackPane) globalScene.getRoot();
        root.getChildren().clear();
        root.getChildren().add(new GameBoardPane(this, gameSession));
    }

    public void showCustomDialog(String title, String message) {
        Stage dialog = new Stage();
        dialog.setTitle(title);
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.APPLICATION_MODAL);

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);

        Button okButton = new Button("OK");
        okButton.getStyleClass().add("modern-button");
        okButton.setOnAction(event -> dialog.close());

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(messageLabel, okButton);

        Scene dialogScene = new Scene(layout, 480, 220);
        dialogScene.getStylesheets().add(getClass().getResource("door_dash_style.css").toExternalForm());
        dialog.setScene(dialogScene);

        dialog.showAndWait();
    }
}
