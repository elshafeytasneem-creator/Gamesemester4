package game.gui;

import java.io.IOException;

import game.engine.Game;
import game.engine.Role;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class CharacterSelectionPane extends StackPane {

    private final Role[] selectedRole = {null};

    // Role card containers for selection state
    private VBox scarerCard;
    private VBox laugherCard;

    public CharacterSelectionPane(SceneManager sceneManager) {
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setAlignment(Pos.CENTER);

        // ── Back button (top-left) ──
        Button backButton = new Button("< BACK");
        backButton.getStyleClass().add("outline-button");
        backButton.setOnAction(e -> sceneManager.showMainMenu());
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(24, 0, 0, 28));

        // ── Central Content Panel ──
        VBox panel = new VBox(28);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(46, 60, 46, 60));
        panel.setMaxWidth(820);
        panel.getStyleClass().add("glass-panel");

        // Header
        Label titleLabel = new Label("CHOOSE YOUR SIDE");
        titleLabel.setStyle(
            "-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,229,200,0.5), 20, 0.4, 0, 0);"
        );

        Label subLabel = new Label("Pick a role — your monster will be assigned from that side");
        subLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #55558A; -fx-font-style: italic;");

        // ── Role Selection Cards ──
        scarerCard = buildRoleCard(
            "SCARER", "Fear Incarnate",
            "Dominate through terror and dread.\nFrighten monsters into submission.",
            "#E05858", "#3A1010"
        );
        laugherCard = buildRoleCard(
            "LAUGHER", "Joy Supreme",
            "Overwhelm opponents with laughter energy.\nSpread infectious joy across the floor.",
            "#40C870", "#103A1C"
        );

        scarerCard.setCursor(Cursor.HAND);
        laugherCard.setCursor(Cursor.HAND);

        scarerCard.setOnMouseClicked(e -> selectRole(Role.SCARER));
        laugherCard.setOnMouseClicked(e -> selectRole(Role.LAUGHER));

        HBox roleRow = new HBox(24);
        roleRow.setAlignment(Pos.CENTER);
        roleRow.getChildren().addAll(scarerCard, laugherCard);

        // ── Divider ──
        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setStyle(
            "-fx-background-color: linear-gradient(to right, transparent, #252560, transparent);"
        );

        // ── Monster Roster ──
        Label rosterLabel = new Label("YOUR MONSTERS");
        rosterLabel.setStyle(
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #48487A;"
        );

        HBox monsterRow = new HBox(14);
        monsterRow.setAlignment(Pos.CENTER);
        monsterRow.getChildren().addAll(
            buildMonsterCard("DASHER",      "◆", "#4898E0",
                "Moves 2x distance normally.\nPower-up: 3x speed for 3 turns!"),
            buildMonsterCard("DYNAMO",      "★", "#40C870",
                "Doubles all energy gains.\nPower-up: Freezes opponent 1 turn!"),
            buildMonsterCard("MULTITASKER", "▲", "#CCA800",
                "Gains +200 energy per change.\nPower-up: Full speed for 2 turns!"),
            buildMonsterCard("SCHEMER",     "●", "#9848E0",
                "Gains +10 energy per step.\nPower-up: Steals from all monsters!")
        );

        // ── Confirm button ──
        Button confirmButton = new Button("ENTER FACTORY FLOOR");
        confirmButton.getStyleClass().add("modern-button");
        confirmButton.setMinWidth(280);
        confirmButton.setMinHeight(48);
        confirmButton.setStyle(
            "-fx-font-size: 15px; -fx-font-weight: bold;" +
            "-fx-background-color: linear-gradient(to bottom, #00C9B0, #009E8C);" +
            "-fx-text-fill: #040412; -fx-background-radius: 12px; -fx-border-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,201,176,0.55), 20, 0, 0, 0);"
        );
        confirmButton.setOnAction(e -> {
            if (selectedRole[0] == null) {
                sceneManager.showCustomDialog("Selection Required",
                    "Please choose a side (SCARER or LAUGHER) before entering the Factory Floor.");
                return;
            }
            Game gameSession;
            try {
                gameSession = new Game(selectedRole[0]);
            } catch (IOException ex) {
                sceneManager.showCustomDialog("Initialization Error", ex.getMessage());
                return;
            }
            sceneManager.showCustomDialog("Match Setup",
                "You're fighting as a " + selectedRole[0] + "!\n" +
                "Your monster: " + gameSession.getPlayer().getName() +
                "\nOpponent: " + gameSession.getOpponent().getName() +
                "\n\nEntering the Factory Floor...");
            sceneManager.showGameBoard(gameSession);
        });

        panel.getChildren().addAll(
            titleLabel, subLabel, roleRow,
            divider, rosterLabel, monsterRow,
            confirmButton
        );

        // ── Entry animation ──
        panel.setOpacity(0);
        panel.setTranslateX(30);

        FadeTransition fade = new FadeTransition(Duration.millis(600), panel);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(600), panel);
        slide.setFromX(30);
        slide.setToX(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(fade, slide).play();

        getChildren().addAll(panel, backButton);
    }

    private void selectRole(Role role) {
        selectedRole[0] = role;
        if (role == Role.SCARER) {
            setCardSelected(scarerCard, "#E05858");
            setCardUnselected(laugherCard);
        } else {
            setCardSelected(laugherCard, "#40C870");
            setCardUnselected(scarerCard);
        }
    }

    private void setCardSelected(VBox card, String borderColor) {
        card.setStyle(
            "-fx-background-color: rgba(14, 14, 38, 0.95);" +
            "-fx-background-radius: 14px; -fx-border-radius: 14px;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: 2.5px; -fx-padding: 22px 24px;" +
            "-fx-effect: dropshadow(gaussian, " + borderColor + "66, 18, 0.3, 0, 0);"
        );
    }

    private void setCardUnselected(VBox card) {
        card.setStyle(
            "-fx-background-color: rgba(10, 10, 28, 0.75);" +
            "-fx-background-radius: 14px; -fx-border-radius: 14px;" +
            "-fx-border-color: #252255; -fx-border-width: 1.5px;" +
            "-fx-padding: 22px 24px; -fx-opacity: 0.7;"
        );
    }

    private VBox buildRoleCard(String roleName, String tagline, String desc,
                                String accentColor, String bgColor) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(260);
        card.setPrefHeight(170);

        setCardUnselected(card);

        Label nameLabel = new Label(roleName);
        nameLabel.setStyle(
            "-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + accentColor + ";" +
            "-fx-effect: dropshadow(gaussian, " + accentColor + "88, 8, 0.3, 0, 0);"
        );

        Label taglineLabel = new Label(tagline);
        taglineLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + accentColor + "; -fx-opacity: 0.7;");

        Label descLabel = new Label(desc);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #6868A0;");
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(nameLabel, taglineLabel, descLabel);
        return card;
    }

    private VBox buildMonsterCard(String name, String icon, String color, String ability) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(140);
        card.setPadding(new Insets(14, 12, 14, 12));
        card.setStyle(
            "-fx-background-color: rgba(10, 10, 26, 0.85);" +
            "-fx-background-radius: 12px; -fx-border-radius: 12px;" +
            "-fx-border-color: #1E1E50; -fx-border-width: 1px;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: " + color + "; -fx-opacity: 0.85;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + color + ";" +
            "-fx-effect: dropshadow(gaussian, " + color + "66, 5, 0, 0, 0);"
        );

        Label abilityLabel = new Label(ability);
        abilityLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #484878;");
        abilityLabel.setWrapText(true);
        abilityLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iconLabel, nameLabel, abilityLabel);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }
}
