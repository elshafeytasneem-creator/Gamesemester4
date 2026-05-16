package game.gui;

import java.io.IOException;

import game.engine.Game;
import game.engine.Role;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CharacterSelectionPane extends VBox {

    public CharacterSelectionPane(SceneManager sceneManager) {
        super(20);
        setAlignment(Pos.CENTER);

        Label label = new Label("Character Selection Screen (Choose your Monster)");
        label.getStyleClass().add("game-title");

        ComboBox<Role> roleComboBox = new ComboBox<Role>();
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
        roleComboBox.setPromptText("Select Team Role");

        ComboBox<String> monsterComboBox = new ComboBox<String>();
        monsterComboBox.setItems(FXCollections.observableArrayList("DASHER", "DYNAMO", "MULTITASKER", "SCHEMER"));
        monsterComboBox.setPromptText("Select Monster Type");

        Button confirmButton = new Button("Confirm Selections & Launch Match");
        confirmButton.getStyleClass().add("modern-button");
        confirmButton.setOnAction(event -> {
            Role selectedRole = roleComboBox.getValue();
            String selectedMonsterType = monsterComboBox.getValue();

            if (selectedRole == null || selectedMonsterType == null) {
                sceneManager.showCustomDialog("Selection Required", "Please choose a Team Role and a Monster Type before continuing.");
                return;
            }

            Game gameSession;
            try {
                gameSession = new Game(selectedRole);
            } catch (IOException e) {
                sceneManager.showCustomDialog("Initialization Error", e.getMessage());
                return;
            }

            sceneManager.showCustomDialog("Match Setup", "Game session created successfully! Entering the Factory Floor...");
            sceneManager.showGameBoard(gameSession);
        });

        getChildren().addAll(label, roleComboBox, monsterComboBox, confirmButton);
    }
}
