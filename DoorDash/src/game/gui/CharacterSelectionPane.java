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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class CharacterSelectionPane extends StackPane {

    private final Role[] selectedRole = {null};

    // Role card containers for selection state
    private VBox scarerCard;
    private VBox laugherCard;
    
    // Audio asset for starting the game floor match
    private AudioClip gameStartSound;

    public CharacterSelectionPane(SceneManager sceneManager) {
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setAlignment(Pos.CENTER);

        // ── PRE-LOAD MATCH START AUDIO EFFECT ──
        try {
            // Looks for game/gui/start_game.mp3 (or .wav) inside your resource path
            String soundPath = getClass().getResource("start_game.mp3").toExternalForm();
            gameStartSound = new AudioClip(soundPath);
        } catch (Exception e) {
            System.err.println("Game start sound track could not be loaded: " + e.getMessage());
            // Safe fallback so the game doesn't crash if file is missing
            gameStartSound = null; 
        }

        // ── BACKGROUND IMAGE LAYER (Perfected Opacity Left Untouched) ──
        try {
            Image backgroundImg = new Image(getClass().getResourceAsStream("image_c2df21.jpg"));
            ImageView backgroundView = new ImageView(backgroundImg);
            
            backgroundView.fitWidthProperty().bind(this.widthProperty());
            backgroundView.fitHeightProperty().bind(this.heightProperty());
            backgroundView.setPreserveRatio(false);
            
            backgroundView.setOpacity(0.25);
            
            getChildren().add(backgroundView);
        } catch (Exception e) {
            System.err.println("Background image could not be loaded: " + e.getMessage());
        }

        // ── Back button (top-left) ──
        Button backButton = new Button("< BACK");
        backButton.getStyleClass().add("outline-button");
        backButton.setOnAction(e -> sceneManager.showMainMenu());
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(24, 0, 0, 28));
        backButton.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #555588;" +
            "-fx-background-color: rgba(255, 255, 255, 0.4);" +
            "-fx-background-radius: 10px;" +
            "-fx-border-color: rgba(11, 115, 145, 0.3); -fx-border-width: 1.5px; -fx-border-radius: 10px;"
        );

        // ── Main UI Window Wrapper ──
        StackPane containerStack = new StackPane();
        containerStack.setMaxWidth(820); 
        containerStack.setAlignment(Pos.CENTER);

        // ── Central Content Panel ──
        VBox panel = new VBox(28);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(46, 75, 46, 75)); 
        panel.getStyleClass().add("glass-panel");
        
        // 58% Translucent White Glass Layout
        panel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.58);" + 
            "-fx-background-radius: 24px;" +
            "-fx-border-radius: 24px;" +
            "-fx-border-color: rgba(255, 255, 255, 0.5);" +
            "-fx-border-width: 1.5px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.12), 30, 0, 0, 10);"
        );

        // Header
        Label titleLabel = new Label("CHOOSE YOUR SIDE");
        titleLabel.setStyle(
            "-fx-font-family: 'Impact', 'Arial Black', sans-serif;" +
            "-fx-font-size: 42px;" + 
            "-fx-text-transform: uppercase;" + 
            "-fx-text-fill: #0B7391;" + 
            "-fx-effect: dropshadow(one-pass-box, white, 1.5, 1.0, 1.5, 0) " +
                        "dropshadow(one-pass-box, white, 1.5, 1.0, -1.5, 0) " +
                        "dropshadow(one-pass-box, white, 1.5, 1.0, 0, 1.5) " +
                        "dropshadow(one-pass-box, white, 1.5, 1.0, 0, -1.5);"
        );

        Label subLabel = new Label("Pick a role — your monster will be assigned from that side");
        subLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555588; -fx-font-style: italic; -fx-font-weight: bold;");

        // ── Role Selection Cards ──
        scarerCard = buildRoleCard(
            "SCARER", "Fear Incarnate",
            "Dominate through terror and dread.\nFrighten monsters into submission.",
            "#E05858"
        );
        laugherCard = buildRoleCard(
            "LAUGHER", "Joy Supreme",
            "Overwhelm opponents with laughter energy.\nSpread infectious joy across the floor.",
            "#40C870"
        );

        scarerCard.setCursor(Cursor.HAND);
        laugherCard.setCursor(Cursor.HAND);

        scarerCard.setOnMouseClicked(e -> selectRole(Role.SCARER));
        laugherCard.setOnMouseClicked(e -> selectRole(Role.LAUGHER));

        Label vsLabel = new Label("VS");
        vsLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #55558A;");

        HBox roleRow = new HBox(24);
        roleRow.setAlignment(Pos.CENTER);
        roleRow.getChildren().addAll(scarerCard, vsLabel, laugherCard);

        // ── Divider ──
        Region divider = new Region();
        divider.setPrefHeight(2);
        divider.setStyle(
            "-fx-background-color: linear-gradient(to right, transparent, #00C9B0, transparent);"
        );

        // ── Monster Roster ──
        Label rosterLabel = new Label("YOUR MONSTERS");
        rosterLabel.setStyle(
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #555588; -fx-letter-spacing: 1px;"
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
                // ── SELECTION REQUIRED POPUP ──
                Stage customPopup = new Stage();
                customPopup.initModality(Modality.APPLICATION_MODAL);
                customPopup.initStyle(StageStyle.TRANSPARENT); 
                customPopup.initOwner(this.getScene().getWindow());

                VBox dialogRoot = new VBox(22);
                dialogRoot.setAlignment(Pos.CENTER);
                dialogRoot.setPadding(new Insets(35, 45, 35, 45));
                dialogRoot.setStyle(
                    "-fx-background-color: rgba(245, 248, 250, 0.96);" + 
                    "-fx-background-radius: 35px;" +
                    "-fx-border-radius: 35px;" +
                    "-fx-border-color: #00FF66;" +
                    "-fx-border-width: 3.5px;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0, 255, 102, 0.6), 25, 0.4, 0, 0);"
                );

                Label dialogTitle = new Label("SELECTION REQUIRED");
                dialogTitle.setStyle(
                    "-fx-font-family: 'Impact', 'Arial Black', sans-serif;" +
                    "-fx-font-size: 22px;" +
                    "-fx-text-fill: #0B7391;" +
                    "-fx-letter-spacing: 0.5px;"
                );

                Label dialogDesc = new Label("Please choose a side (SCARER or LAUGHER) before entering the Factory Floor.");
                dialogDesc.setWrapText(true);
                dialogDesc.setAlignment(Pos.CENTER);
                dialogDesc.setPrefWidth(320);
                dialogDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #555588; -fx-font-weight: bold; -fx-line-spacing: 3px;");

                Button continueBtn = new Button("CONTINUE");
                continueBtn.setPrefWidth(140);
                continueBtn.setPrefHeight(38);
                continueBtn.setCursor(Cursor.HAND);
                continueBtn.setStyle(
                    "-fx-font-size: 13px; -fx-font-weight: bold;" +
                    "-fx-background-color: linear-gradient(to bottom, #00C9B0, #009E8C);" +
                    "-fx-text-fill: #040412; -fx-background-radius: 20px;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,201,176,0.3), 10, 0, 0, 0);"
                );
                continueBtn.setOnAction(evt -> customPopup.close());

                dialogRoot.getChildren().addAll(dialogTitle, dialogDesc, continueBtn);

                Scene popupScene = new Scene(dialogRoot);
                popupScene.setFill(Color.TRANSPARENT); 
                customPopup.setScene(popupScene);
                customPopup.showAndWait();
                return;
            }
            
            Game gameSession;
            try {
                gameSession = new Game(selectedRole[0]);
            } catch (IOException ex) {
                sceneManager.showCustomDialog("Initialization Error", ex.getMessage());
                return;
            }

            // ── MATCH SETUP POPUP ──
            Stage matchPopup = new Stage();
            matchPopup.initModality(Modality.APPLICATION_MODAL);
            matchPopup.initStyle(StageStyle.TRANSPARENT); 
            matchPopup.initOwner(this.getScene().getWindow());

            VBox matchRoot = new VBox(20);
            matchRoot.setAlignment(Pos.CENTER);
            matchRoot.setPadding(new Insets(35, 50, 35, 50));
            matchRoot.setStyle(
                "-fx-background-color: rgba(245, 248, 250, 0.96);" + 
                "-fx-background-radius: 35px;" +
                "-fx-border-radius: 35px;" +
                "-fx-border-color: #00FF66;" +
                "-fx-border-width: 3.5px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0, 255, 102, 0.6), 25, 0.4, 0, 0);"
            );

            Label matchTitle = new Label("MATCH SETUP");
            matchTitle.setStyle(
                "-fx-font-family: 'Impact', 'Arial Black', sans-serif;" +
                "-fx-font-size: 24px;" +
                "-fx-text-fill: #0B7391;" +
                "-fx-letter-spacing: 0.8px;"
            );

            String matchText = "You're fighting as a " + selectedRole[0] + "!\n\n" +
                               "Your monster: " + gameSession.getPlayer().getName() + "\n" +
                               "Opponent: " + gameSession.getOpponent().getName() + "\n\n" +
                               "Entering the Factory Floor...";
                               
            Label matchDesc = new Label(matchText);
            matchDesc.setWrapText(true);
            matchDesc.setAlignment(Pos.CENTER);
            matchDesc.setPrefWidth(340);
            matchDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #444466; -fx-font-weight: bold; -fx-line-spacing: 4px;");

            Button matchContinueBtn = new Button("CONTINUE");
            matchContinueBtn.setPrefWidth(140);
            matchContinueBtn.setPrefHeight(38);
            matchContinueBtn.setCursor(Cursor.HAND);
            matchContinueBtn.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold;" +
                "-fx-background-color: linear-gradient(to bottom, #00C9B0, #009E8C);" +
                "-fx-text-fill: #040412; -fx-background-radius: 20px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,201,176,0.3), 10, 0, 0, 0);"
            );
            
            matchContinueBtn.setOnAction(evt -> {
                // ── PLAY GAME START SOUND EFFECT ──
                if (gameStartSound != null) {
                    gameStartSound.play();
                }
                
                matchPopup.close();
            });

            matchRoot.getChildren().addAll(matchTitle, matchDesc, matchContinueBtn);

            Scene matchScene = new Scene(matchRoot);
            matchScene.setFill(Color.TRANSPARENT); 
            matchPopup.setScene(matchScene);
            matchPopup.showAndWait();

            // Screen transmission to the main game board
            sceneManager.showGameBoard(gameSession);
        });

        // Assemble panel items
        panel.getChildren().addAll(
            titleLabel, subLabel, roleRow,
            divider, rosterLabel, monsterRow,
            confirmButton
        );

        containerStack.getChildren().add(panel);
        getChildren().add(containerStack);

        ParallelTransition masterAnimation = new ParallelTransition();

        // ── POSITIONING & ANIMATING RANDALL ──
        ImageView randallView = null;
        try {
            Image randallImg = new Image(getClass().getResourceAsStream("randal.png"));
            randallView = new ImageView(randallImg);
            randallView.setFitHeight(200); 
            randallView.setPreserveRatio(true);
            
            StackPane.setAlignment(randallView, Pos.TOP_LEFT);
            StackPane.setMargin(randallView, new Insets(85, 0, 0, 175)); 
            getChildren().add(randallView); 

            randallView.setOpacity(0);
            FadeTransition randallFade = new FadeTransition(Duration.millis(600), randallView);
            randallFade.setFromValue(0);
            randallFade.setToValue(1);

            TranslateTransition randallSlide = new TranslateTransition(Duration.millis(600), randallView);
            randallSlide.setFromX(-35); 
            randallSlide.setToX(0);
            randallSlide.setInterpolator(Interpolator.EASE_OUT);

            masterAnimation.getChildren().addAll(randallFade, randallSlide);
        } catch (Exception e) {
            System.err.println("Randall image could not be loaded: " + e.getMessage());
        }

        // ── POSITIONING & ANIMATING FUNGUS ──
        ImageView fungusView = null;
        try {
            Image fungusImg = new Image(getClass().getResourceAsStream("fungus.png"));
            fungusView = new ImageView(fungusImg);
            fungusView.setFitHeight(200); 
            fungusView.setPreserveRatio(true);
            
            StackPane.setAlignment(fungusView, Pos.TOP_RIGHT);
            StackPane.setMargin(fungusView, new Insets(85, 58, 0, 0)); 
            getChildren().add(fungusView); 

            fungusView.setOpacity(0);
            FadeTransition fungusFade = new FadeTransition(Duration.millis(600), fungusView);
            fungusFade.setFromValue(0);
            fungusFade.setToValue(1);

            TranslateTransition fungusSlide = new TranslateTransition(Duration.millis(600), fungusView);
            fungusSlide.setFromX(35); 
            fungusSlide.setToX(0);
            fungusSlide.setInterpolator(Interpolator.EASE_OUT);

            masterAnimation.getChildren().addAll(fungusFade, fungusSlide);
        } catch (Exception e) {
            System.err.println("Fungus image could not be loaded: " + e.getMessage());
        }

        // ── Main Panel Motion Pipeline ──
        containerStack.setOpacity(0);
        containerStack.setTranslateX(0);
        containerStack.setTranslateY(30); 

        FadeTransition panelFade = new FadeTransition(Duration.millis(650), containerStack);
        panelFade.setFromValue(0);
        panelFade.setToValue(1);

        TranslateTransition panelSlide = new TranslateTransition(Duration.millis(650), containerStack);
        panelSlide.setFromY(30);
        panelSlide.setToY(0);
        panelSlide.setInterpolator(Interpolator.EASE_OUT);

        masterAnimation.getChildren().addAll(panelFade, panelSlide);
        
        masterAnimation.play();

        getChildren().add(backButton);
    }

    private void selectRole(Role role) {
        if (role == Role.SCARER) {
            selectedRole[0] = Role.SCARER;
            setCardSelected(scarerCard, "#E05858");
            setCardUnselected(laugherCard);
        } else {
            selectedRole[0] = Role.LAUGHER;
            setCardSelected(laugherCard, "#40C870");
            setCardUnselected(scarerCard);
        }
    }

    private void setCardSelected(VBox card, String borderColor) {
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.75);" +
            "-fx-background-radius: 14px; -fx-border-radius: 14px;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: 2.5px; -fx-padding: 22px 24px;" +
            "-fx-effect: dropshadow(gaussian, " + borderColor + "44, 18, 0.3, 0, 0);"
        );
    }

    private void setCardUnselected(VBox card) {
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.45);" +
            "-fx-background-radius: 14px; -fx-border-radius: 14px;" +
            "-fx-border-color: rgba(85, 85, 136, 0.2);" + 
            "-fx-padding: 22px 24px; -fx-opacity: 0.75;"
        );
    }

    private void setCardHovered(VBox card, String accentColor) {
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.65);" +
            "-fx-background-radius: 14px; -fx-border-radius: 14px;" +
            "-fx-border-color: " + accentColor + "88;" + 
            "-fx-border-width: 2.5px; -fx-padding: 22px 24px;" +
            "-fx-effect: dropshadow(gaussian, " + accentColor + "22, 12, 0.2, 0, 0);" +
            "-fx-opacity: 1.0;"
        );
    }

    private VBox buildRoleCard(String roleName, String tagline, String desc, String accentColor) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(260);
        card.setPrefHeight(170);

        setCardUnselected(card);

        Label nameLabel = new Label(roleName);
        nameLabel.setStyle(
            "-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + accentColor + ";" +
            "-fx-effect: dropshadow(gaussian, " + accentColor + "33, 8, 0.3, 0, 0);"
        );

        Label taglineLabel = new Label(tagline);
        taglineLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555588; -fx-font-weight: bold; -fx-opacity: 0.85;");

        Label descLabel = new Label(desc);
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #444466; -fx-line-spacing: 2px;");
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(nameLabel, taglineLabel, descLabel);

        card.setOnMouseEntered(e -> {
            Role currentCardRole = roleName.equals("SCARER") ? Role.SCARER : Role.LAUGHER;
            if (selectedRole[0] != currentCardRole) {
                setCardHovered(card, accentColor);
            }
        });

        card.setOnMouseExited(e -> {
            Role currentCardRole = roleName.equals("SCARER") ? Role.SCARER : Role.LAUGHER;
            if (selectedRole[0] != currentCardRole) {
                setCardUnselected(card);
            }
        });

        return card;
    }

    private VBox buildMonsterCard(String name, String icon, String color, String ability) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(140);
        card.setPadding(new Insets(14, 12, 14, 12));
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.65);" +
            "-fx-background-radius: 12px; -fx-border-radius: 12px;" +
            "-fx-border-color: rgba(255, 255, 255, 0.5); -fx-border-width: 1px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.02), 8, 0, 0, 2);"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: " + color + "; -fx-opacity: 0.85;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + color + ";" +
            "-fx-effect: dropshadow(gaussian, " + color + "33, 5, 0, 0, 0);"
        );

        Label abilityLabel = new Label(ability);
        abilityLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #444466; -fx-line-spacing: 1.5px;");
        abilityLabel.setWrapText(true);
        abilityLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iconLabel, nameLabel, abilityLabel);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }
}