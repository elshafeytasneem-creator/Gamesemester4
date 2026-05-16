package game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainMenuPane extends VBox {

    public MainMenuPane(SceneManager sceneManager) {
        super(25);

        setAlignment(Pos.CENTER);
        setPadding(new Insets(40));
        getStyleClass().add("glass-panel");

        Label title = new Label("DooR DasH");
        title.getStyleClass().add("game-title");

        Button enterFactoryFloorButton = new Button("Enter Factory Floor");
        enterFactoryFloorButton.getStyleClass().add("modern-button");
        enterFactoryFloorButton.setOnAction(event -> sceneManager.showCharacterSelection());

        getChildren().addAll(title, enterFactoryFloorButton);
    }
}
