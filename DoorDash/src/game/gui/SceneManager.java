package game.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import game.engine.Game;

public class SceneManager {

    private final Stage primaryStage;
    private final Scene globalScene;

    public SceneManager(Stage primaryStage, Scene globalScene) {
        this.primaryStage = primaryStage;
        this.globalScene = globalScene;
        
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

        // Header
        VBox header = new VBox(0);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(22, 30, 16, 30));
        header.setStyle(
            "-fx-background-color: linear-gradient(to right, #12123A, #0C0C28);" +
            "-fx-border-color: #1C1C4A; -fx-border-width: 0 0 1px 0;"
        );
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dialog-title");
        header.getChildren().add(titleLabel);

        // Separator
        Region sep = new Region();
        sep.setPrefHeight(1);
        sep.setStyle("-fx-background-color: linear-gradient(to right, transparent, rgba(0,229,200,0.25), transparent);");

        // Body
        VBox body = new VBox(22);
        body.setAlignment(Pos.CENTER);
        body.setPadding(new Insets(22, 30, 28, 30));
        body.setStyle("-fx-background-color: linear-gradient(to bottom, #0E0E2E, #0A0A20);");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("dialog-message");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setAlignment(Pos.CENTER);

        Button okButton = new Button("CONTINUE");
        okButton.getStyleClass().add("modern-button");
        okButton.setMinWidth(130);
        okButton.setMinHeight(40);
        okButton.setOnAction(e -> dialog.close());

        body.getChildren().addAll(messageLabel, okButton);

        VBox layout = new VBox(0);
        layout.getChildren().addAll(header, sep, body);

        Scene dialogScene = new Scene(layout, 480, 260);
        dialogScene.getStylesheets().add(
            getClass().getResource("door_dash_style.css").toExternalForm()
        );
        dialog.setScene(dialogScene);

        // Entrance animation after dialog is showing
        layout.setOpacity(0);
        layout.setScaleX(0.92);
        layout.setScaleY(0.92);

        Platform.runLater(() -> {
            FadeTransition ft = new FadeTransition(Duration.millis(250), layout);
            ft.setFromValue(0);
            ft.setToValue(1);

            ScaleTransition st = new ScaleTransition(Duration.millis(250), layout);
            st.setFromX(0.92);
            st.setToX(1.0);
            st.setFromY(0.92);
            st.setToY(1.0);

            new ParallelTransition(ft, st).play();
        });

        dialog.showAndWait();
    }
}
