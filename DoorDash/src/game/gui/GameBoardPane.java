package game.gui;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Game;
import game.engine.Role;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.cells.TransportCell;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class GameBoardPane extends StackPane {

    private final SceneManager sceneManager;
    private final Game gameSession;
    private final GridPane gridPane;
    private final StackPane overlayContainer;

    // HUD labels
    private final Label turnCountLabel;
    private final Label currentTurnValue;
    private final Label currentRoleLabel;

    // Player stats
    private final VBox playerStatsBox;
    private final Label playerNameLabel;
    private final Label playerEnergyValue;
    private final ProgressBar playerEnergyBar;
    private final HBox playerStatusBox;

    // Opponent stats
    private final VBox opponentStatsBox;
    private final Label opponentNameLabel;
    private final Label opponentEnergyValue;
    private final ProgressBar opponentEnergyBar;
    private final HBox opponentStatusBox;

    // Dice & controls
    private final Label diceValueLabel;
    private final Button rollDiceButton;
    private final Button powerupButton;

    public GameBoardPane(SceneManager sceneManager, Game gameSession) {
        this.sceneManager = sceneManager;
        this.gameSession = gameSession;
        setAlignment(Pos.CENTER);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        // Very light blue-purple background tint base
        setStyle("-fx-background-color: #F0F2FA;");

        // ── BACKGROUND IMAGE LAYER ──
        ImageView backgroundView = null;
        try {
            Image backgroundImg = new Image(getClass().getResourceAsStream("image_c2df21.jpg"));
            backgroundView = new ImageView(backgroundImg);
            backgroundView.fitWidthProperty().bind(this.widthProperty());
            backgroundView.fitHeightProperty().bind(this.heightProperty());
            backgroundView.setPreserveRatio(false);
            backgroundView.setOpacity(0.22); 
        } catch (Exception e) {
            System.err.println("⚠️ Could not load background asset 'image_c2df21.jpg': " + e.getMessage());
        }

        if (backgroundView != null) {
            getChildren().add(backgroundView);
        }

        // ════════════════════════════════════════
        // BOARD AREA (left)
        // ════════════════════════════════════════
        VBox boardArea = new VBox(15);
        boardArea.setAlignment(Pos.CENTER);
        boardArea.setPadding(new Insets(20, 16, 16, 20));
        HBox.setHgrow(boardArea, Priority.ALWAYS);

        // Board header
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label headerLabel = new Label("DooR DasH: Factory Floor");
        headerLabel.setStyle(
            "-fx-font-family: 'Lucida Sans Unicode', sans-serif; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #009E8C;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,201,176,0.25), 8, 0, 0, 0);"
        );

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        this.turnCountLabel = new Label("TURN #0");
        this.turnCountLabel.setStyle(
            "-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2B2B36;" +
            "-fx-background-color: rgba(0, 0, 0, 0.08); -fx-padding: 5px 14px; -fx-background-radius: 10px;" +
            "-fx-border-color: rgba(0, 0, 0, 0.18); -fx-border-width: 1px; -fx-border-radius: 10px;"
        );

        headerRow.getChildren().addAll(headerLabel, headerSpacer, turnCountLabel);

        // Grid Container styling 
        this.gridPane = new GridPane();
        this.gridPane.setHgap(4);
        this.gridPane.setVgap(4);
        this.gridPane.setAlignment(Pos.CENTER);
        this.gridPane.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.65); " +
            "-fx-padding: 8px; " +
            "-fx-background-radius: 14px; " +
            "-fx-border-color: rgba(0, 0, 0, 0.12); " +
            "-fx-border-width: 1px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);"
        );

        // Legend bar
        HBox legendBar = buildLegendBar();

        boardArea.getChildren().addAll(headerRow, this.gridPane, legendBar);

        // ════════════════════════════════════════
        // SIDEBAR (right)
        // ════════════════════════════════════════
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(20, 16, 20, 16));
        sidebar.setPrefWidth(295);
        sidebar.setMinWidth(295);
        sidebar.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.55); " +
            "-fx-border-color: rgba(0, 0, 0, 0.1) 0px 0px 0px 1px;"
        );

        // ── Active Turn Section ──
        VBox turnInfoBox = new VBox(3);
        turnInfoBox.setPadding(new Insets(10, 12, 10, 12));
        turnInfoBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.85); " +
            "-fx-background-radius: 12px; -fx-border-radius: 12px;" +
            "-fx-border-color: rgba(0, 0, 0, 0.12); -fx-border-width: 1px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.04), 6, 0, 0, 1);"
        );

        Label activeLabel = new Label("ACTIVE PLAYER");
        activeLabel.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #009E8C; -fx-letter-spacing: 0.5px;");

        this.currentTurnValue = new Label("---");
        this.currentTurnValue.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2B2B36;");

        this.currentRoleLabel = new Label("---");
        this.currentRoleLabel.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 11px; -fx-text-fill: #4A4A5A; -fx-font-weight: bold;");

        turnInfoBox.getChildren().addAll(activeLabel, currentTurnValue, currentRoleLabel);

        // Stats Cards Initializations
        this.playerStatsBox = new VBox(6);
        this.playerStatsBox.setPadding(new Insets(12));
        
        this.opponentStatsBox = new VBox(6);
        this.opponentStatsBox.setPadding(new Insets(12));

        Label playerHeader = new Label("YOU");
        playerHeader.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #3A3A4A;");

        this.playerNameLabel = new Label("---");
        this.playerNameLabel.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2B2B36;");

        this.playerStatusBox = new HBox(5);
        this.playerStatusBox.setAlignment(Pos.CENTER_LEFT);

        this.playerEnergyBar = new ProgressBar(0);
        this.playerEnergyBar.setMaxWidth(Double.MAX_VALUE);
        this.playerEnergyBar.setPrefHeight(10);
        this.playerEnergyBar.setStyle("-fx-accent: #009E8C; -fx-control-inner-background: rgba(0,0,0,0.08);");

        this.playerEnergyValue = new Label("0 Energy");
        this.playerEnergyValue.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #009E8C;");

        playerStatsBox.getChildren().addAll(
            playerHeader, playerNameLabel, playerStatusBox, playerEnergyBar, playerEnergyValue
        );

        Label opponentHeader = new Label("OPPONENT");
        opponentHeader.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #3A3A4A;");

        this.opponentNameLabel = new Label("---");
        this.opponentNameLabel.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2B2B36;");

        this.opponentStatusBox = new HBox(5);
        this.opponentStatusBox.setAlignment(Pos.CENTER_LEFT);

        this.opponentEnergyBar = new ProgressBar(0);
        this.opponentEnergyBar.setMaxWidth(Double.MAX_VALUE);
        this.opponentEnergyBar.setPrefHeight(10);
        this.opponentEnergyBar.setStyle("-fx-accent: #E03E3E; -fx-control-inner-background: rgba(0,0,0,0.08);");

        this.opponentEnergyValue = new Label("0 Energy");
        this.opponentEnergyValue.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #E03E3E;");

        opponentStatsBox.getChildren().addAll(
            opponentHeader, opponentNameLabel, opponentStatusBox, opponentEnergyBar, opponentEnergyValue
        );

        setStatCardIdle(playerStatsBox);
        setStatCardIdle(opponentStatsBox);

        // ── Dice Panel ──
        VBox diceBox = new VBox(2);
        diceBox.setAlignment(Pos.CENTER);
        diceBox.setPadding(new Insets(10, 14, 10, 14));
        diceBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9); " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: rgba(0, 0, 0, 0.12); " +
            "-fx-border-width: 1px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 6, 0, 0, 1);"
        );

        Label diceHeader = new Label("LAST ROLL");
        diceHeader.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #A67C00;");

        // SIGNIFICANTLY ENLARGED DICE FONT (Changed from 26px to 54px)
        this.diceValueLabel = new Label("-");
        this.diceValueLabel.setStyle(
            "-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 54px; -fx-font-weight: bold; -fx-text-fill: #8B6508;"
        );

        diceBox.getChildren().addAll(diceHeader, diceValueLabel);

        // Win goal reminder
        Label goalLabel = new Label("Goal: Cell 99 + 1000 Energy");
        goalLabel.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 11px; -fx-text-fill: #3A3A4A; -fx-font-weight: bold; -fx-font-style: italic;");
        goalLabel.setAlignment(Pos.CENTER);

        // Deck tracker
        Label deckTrackerLabel = new Label("🃏 DECK STATUS: 25 Shuffled Cards");
        deckTrackerLabel.setStyle(
            "-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 11px; -fx-font-weight: bold; " +
            "-fx-text-fill: #8B6508; -fx-background-color: rgba(212, 175, 55, 0.16); " +
            "-fx-background-radius: 8px; -fx-padding: 6px 12px; -fx-alignment: center;" +
            "-fx-border-color: rgba(139, 101, 8, 0.3); -fx-border-width: 1px; -fx-border-radius: 8px;"
        );
        deckTrackerLabel.setMaxWidth(Double.MAX_VALUE);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Controls Buttons
        this.rollDiceButton = new Button("ROLL DICE");
        this.rollDiceButton.setMaxWidth(Double.MAX_VALUE);
        this.rollDiceButton.setMinHeight(42);
        this.rollDiceButton.setStyle(
            "-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 13px; -fx-font-weight: bold;" +
            "-fx-background-color: linear-gradient(to bottom, #00C9B0, #009E8C);" +
            "-fx-text-fill: #FFFFFF; -fx-background-radius: 8px;" +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,201,176,0.35), 10, 0, 0, 2);"
        );
        this.rollDiceButton.setOnAction(e -> handleTurnExecution());

        this.powerupButton = new Button("USE POWER-UP  (500 E)");
        this.powerupButton.setMaxWidth(Double.MAX_VALUE);
        this.powerupButton.setMinHeight(36);
        this.powerupButton.setStyle(
            "-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-background-color: rgba(0, 0, 0, 0.08); -fx-text-fill: #2B2B36;" +
            "-fx-background-radius: 8px; -fx-border-color: rgba(0,0,0,0.18); -fx-border-width: 1px; -fx-cursor: hand;"
        );
        this.powerupButton.setOnAction(e -> handlePowerup());

        Button returnButton = new Button("RETURN TO MENU");
        returnButton.setMaxWidth(Double.MAX_VALUE);
        returnButton.setMinHeight(34);
        returnButton.setStyle(
            "-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 11px; -fx-font-weight: bold;" +
            "-fx-background-color: transparent; -fx-text-fill: #C42121;" +
            "-fx-border-color: rgba(196,33,33,0.5); -fx-border-width: 1px; -fx-border-radius: 8px; -fx-cursor: hand;"
        );
        returnButton.setOnAction(e -> {
            showStyledDialog("Leaving Match",
                "Returning to the main menu. Current progress will be lost.", () -> {
                    sceneManager.showMainMenu();
                });
        });

        sidebar.getChildren().addAll(
            turnInfoBox, playerStatsBox, opponentStatsBox, diceBox,
            goalLabel, deckTrackerLabel, spacer,
            rollDiceButton, powerupButton, returnButton
        );

        HBox mainLayout = new HBox(0);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(boardArea, sidebar);

        this.overlayContainer = new StackPane();
        VBox.setVgrow(this.overlayContainer, Priority.ALWAYS);
        this.overlayContainer.getChildren().add(mainLayout);

        getChildren().add(this.overlayContainer);

        refreshBoardDisplay();
    }

    // ── TALL LIGHT-BLUE PANEL DIALOG SYSTEM (NO SCROLL) ──
    private void showStyledDialog(String title, String bodyText, Runnable onConfirm) {
        javafx.application.Platform.runLater(() -> {
            StackPane modalBg = new StackPane();
            modalBg.setStyle("-fx-background-color: rgba(4, 4, 18, 0.55);");
            modalBg.setPadding(new Insets(20));

            VBox dialogBox = new VBox(16);
            dialogBox.setAlignment(Pos.CENTER);
            dialogBox.setMaxSize(360, 450); 
            dialogBox.setPadding(new Insets(32, 24, 32, 24));
            dialogBox.setStyle(
                "-fx-background-color: #F0F2FA; " + 
                "-fx-border-color: #009E8C; -fx-border-width: 1.5px; " + 
                "-fx-background-radius: 16px; -fx-border-radius: 16px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 158, 140, 0.2), 15, 0, 0, 4);"
            );

            Label modalTitle = new Label(title.toUpperCase());
            modalTitle.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #009E8C; -fx-letter-spacing: 0.8px;");

            Region modalDivider = new Region();
            modalDivider.setPrefHeight(2);
            modalDivider.setMaxWidth(110);
            modalDivider.setStyle("-fx-background-color: #009E8C; -fx-opacity: 0.6;");

            VBox bodyTextContainer = new VBox();
            bodyTextContainer.setAlignment(Pos.CENTER);
            VBox.setVgrow(bodyTextContainer, Priority.ALWAYS);

            Label bodyLabel = new Label(bodyText);
            bodyLabel.setWrapText(true);
            bodyLabel.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 13px; -fx-text-fill: #2B2B36; -fx-text-alignment: center; -fx-line-spacing: 5px;");
            bodyTextContainer.getChildren().add(bodyLabel);

            Button actionBtn = new Button("CONFIRM");
            if (title.contains("WIN") || title.contains("VICTORY") || title.contains("OVER")) {
                actionBtn.setText("RETURN TO MAIN MENU");
            } else if (title.contains("CARD") || title.contains("Socks") || title.contains("Blocked") || title.contains("Hazard")) {
                actionBtn.setText("CONTINUE MATCH");
            }
            
            actionBtn.setMaxWidth(200);
            actionBtn.setMinHeight(38);
            actionBtn.setStyle(
                "-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 11px; -fx-font-weight: bold;" +
                "-fx-background-color: linear-gradient(to bottom, #00C9B0, #009E8C);" +
                "-fx-text-fill: #FFFFFF; -fx-background-radius: 8px;" +
                "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,201,176,0.2), 8, 0, 0, 2);"
            );
            
            actionBtn.setOnAction(e -> {
                overlayContainer.getChildren().remove(modalBg);
                if (onConfirm != null) onConfirm.run();
            });

            dialogBox.getChildren().addAll(modalTitle, modalDivider, bodyTextContainer, actionBtn);
            modalBg.getChildren().add(dialogBox);
            overlayContainer.getChildren().add(modalBg);

            modalBg.setOpacity(0);
            dialogBox.setScaleX(0.85);
            dialogBox.setScaleY(0.85);

            FadeTransition bgFade = new FadeTransition(Duration.millis(180), modalBg);
            bgFade.setFromValue(0);
            bgFade.setToValue(1);

            ScaleTransition boxScale = new ScaleTransition(Duration.millis(180), dialogBox);
            boxScale.setFromX(0.85); boxScale.setToX(1.0);
            boxScale.setFromY(0.85); boxScale.setToY(1.0);

            bgFade.play();
            boxScale.play();
        });
    }

    // ── TURN EXECUTION ──
    private void handleTurnExecution() {
        rollDiceButton.setDisable(true);
        powerupButton.setDisable(true);

        Monster mover    = gameSession.getCurrent();
        Monster opponent = (mover == gameSession.getPlayer())
                            ? gameSession.getOpponent() : gameSession.getPlayer();

        int     startPos      = mover.getPosition();
        int     startEnergy   = mover.getEnergy();
        Role    startRole     = mover.getRole();
        boolean startShielded = mover.isShielded();

        Timeline diceAnim = new Timeline(
            new KeyFrame(Duration.millis(80), e ->
                diceValueLabel.setText(String.valueOf((int) (Math.random() * 6) + 1))
            )
        );
        diceAnim.setCycleCount(13);

        diceAnim.setOnFinished(e -> {
            try {
                gameSession.playTurn();

                int roll = gameSession.getLastRoll();
                diceValueLabel.setText(roll == 0 ? "SKIP" : String.valueOf(roll));
                animateDicePop();

                turnCountLabel.setText("TURN  #" + gameSession.getTurnCount());

                evaluateTurnEvents(mover, opponent, startPos, startEnergy, startRole, startShielded);
                refreshBoardDisplay();
                checkWinCondition();

            } catch (InvalidMoveException ex) {
                diceValueLabel.setText("X");
                showStyledDialog("Move Blocked", ex.getMessage(), null);
            } catch (Exception ex) {
                showStyledDialog("System Error", "An unexpected error occurred: " + ex.getMessage(), null);
            } finally {
                if (gameSession.getWinner() == null) {
                    rollDiceButton.setDisable(false);
                    updatePowerupButton();
                }
            }
        });

        diceAnim.play();
    }

    // ENHANCED POP MOTION SCALE FOR LARGER NUMBER DISPLAY
    private void animateDicePop() {
        ScaleTransition pop = new ScaleTransition(Duration.millis(180), diceValueLabel);
        pop.setFromX(0.7);
        pop.setToX(1.45);
        pop.setFromY(0.7);
        pop.setToY(1.45);
        pop.setAutoReverse(true);
        pop.setCycleCount(2);
        pop.play();
    }

    private void handlePowerup() {
        try {
            Monster opponent = (gameSession.getCurrent() == gameSession.getPlayer())
                    ? gameSession.getOpponent() : gameSession.getPlayer();
            gameSession.usePowerup();
            // Check if the opponent is now frozen (Dynamo effect)
            if (opponent.isFrozen()) {
                showStyledDialog("DYNAMO POWER-UP ACTIVATED",
                        "Dynamo's freeze effect activated!\n\n" +
                        opponent.getName() + " has been FROZEN.\n\n" +
                        "Their turn will be skipped.\n\n" +
                        "The freeze status is shown on their stat card.",
                        null);
            } else {
                showToastMessage("Power-Up Activated!");
            }
            refreshBoardDisplay();
            updatePowerupButton();
        } catch (OutOfEnergyException e) {
            showToastMessage("Not enough energy for Power-Up!");
        }
    }

    private void updatePowerupButton() {
        Monster current = gameSession.getCurrent();
        powerupButton.setDisable(current.getEnergy() < Constants.POWERUP_COST);
    }

    private void evaluateTurnEvents(Monster mover, Monster opponent,
            int startPos, int startEnergy,
            Role startRole, boolean startShielded) {
        int endPos = mover.getPosition();
        int endEnergy = mover.getEnergy();
        Role endRole = mover.getRole();
        String name = mover.getName();

        if (endPos == 0 && startPos != 0) {
            if (endEnergy == startEnergy && startRole == endRole) {
                showStyledDialog("Socks Hazard", name + " stepped on a Contamination Sock and got sent back to cell 0!", null);
            }
        }

        // If the roll came back as 0 it means the turn was skipped due to freeze
        if (gameSession.getLastRoll() == 0) {
            showStyledDialog("TURN SKIPPED - FROZEN",
                    name + "'s turn has been skipped!\n\n" +
                    "The Dynamo power-up has frozen this monster solid.\n" +
                    "No movement or actions are possible while frozen.",
                    null);
        } else if (mover.isFrozen()) {
            showToastMessage("❄ " + name + " is FROZEN — turn will be skipped!");
        }

        Cell[][] boardCells = gameSession.getBoard().getBoardCells();
        int checkPos = endPos;
        int row = checkPos / 10;
        int col = checkPos % 10;
        if (row % 2 == 1) col = 9 - col;

        Cell landedCell = boardCells[row][col];

        if (landedCell instanceof CardCell || (endPos == 0 && startPos != 0 && startPos != 10 && startPos != 20)) {
            if (endPos == 0) {
                showStyledDialog("🃏 CARD DRAWN", 
                    "Card Name: Start Over Card\n\n" +
                    "Effect: Sent back to cell 0!\n\n" +
                    "Action: Resets active monster progress to the starting grid floor floor.", null);
            }
            else if (startRole != endRole) {
                showStyledDialog("🃏 CARD DRAWN", 
                    "Card Name: Confusion Card\n\n" +
                    "Effect: Swapped roles to " + endRole + "!\n\n" +
                    "Action: Forces players to exchange active operation statuses.", null);
            }
            else if (endEnergy < startEnergy) {
                int loss = startEnergy - endEnergy;
                showStyledDialog("🃏 CARD DRAWN", 
                    "Card Name: Energy Drain Card\n\n" +
                    "Effect: Lost " + loss + " Energy!\n\n" +
                    "Action: Saps power from your active factory canister module.", null);
            }
            else {
                int gain = (endEnergy > startEnergy) ? (endEnergy - startEnergy) : 150; 
                showStyledDialog("🃏 CARD DRAWN", 
                    "Card Name: Energy Gift Card\n\n" +
                    "Effect: Gained " + gain + " Energy!\n\n" +
                    "Action: Boosts active monster's power reserve array.", null);
            }
        }
    }

    private void checkWinCondition() {
        Monster winner = gameSession.getWinner();
        if (winner != null) {
            rollDiceButton.setDisable(true);
            powerupButton.setDisable(true);
            showGameOverScreen(winner, (winner == gameSession.getPlayer() ? gameSession.getOpponent() : gameSession.getPlayer()), "🏆 MATCH VICTORY");
        }
    }

    // ── BOARD REFRESH DISPLAY ──
    private void refreshBoardDisplay() {
        this.requestFocus();
        setupTestingCheatKeys();
        gridPane.getChildren().clear();

        Monster player   = gameSession.getPlayer();
        Monster opponent = gameSession.getOpponent();
        Monster current  = gameSession.getCurrent();

        int playerPos   = player.getPosition();
        int opponentPos = opponent.getPosition();

        Board    board      = gameSession.getBoard();
        Cell[][] boardCells = board.getBoardCells();

        for (int index = 0; index < 100; index++) {
            int trackRow = index / 10;
            int trackCol = index % 10;
            if (trackRow % 2 == 1) trackCol = 9 - trackCol;

            int gridRow = 9 - trackRow;
            int gridCol = trackCol;

            Cell cell = boardCells[trackRow][trackCol];

            StackPane tileBlock = new StackPane();
            tileBlock.setPrefSize(74, 54);
            
            if (index % 2 == 0) {
                tileBlock.setStyle("-fx-background-color: rgba(255, 255, 255, 0.55); -fx-background-radius: 6px;");
            } else {
                tileBlock.setStyle("-fx-background-color: rgba(238, 238, 245, 0.8); -fx-background-radius: 6px;");
            }

            String typeIcon  = "";
            String typeColor = "#3A3A4A";

            if (cell instanceof MonsterCell) {
                tileBlock.setStyle("-fx-background-color: rgba(0, 150, 255, 0.12); -fx-border-color: rgba(0, 150, 255, 0.35); -fx-border-width: 1px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
                typeColor = "#007ACC";
                
                MonsterCell monsterCell = (MonsterCell) cell;
                String monsterOwner = "M"; 
                try {
                    if (monsterCell.getMonster() != null) {
                        monsterOwner = "M: " + monsterCell.getMonster().getName();
                    }
                } catch (Exception e1) {
                    try { monsterOwner = "M: " + monsterCell.getName(); } catch (Exception e2) { monsterOwner = "M-CELL"; }
                }
                typeIcon = monsterOwner;
            } else if (cell instanceof CardCell) {
                tileBlock.setStyle("-fx-background-color: rgba(212, 175, 55, 0.15); -fx-border-color: rgba(212, 175, 55, 0.4); -fx-border-width: 1px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
                typeIcon  = "CARD";
                typeColor = "#8B6508";
            } else if (cell instanceof ConveyorBelt) {
                tileBlock.setStyle("-fx-background-color: rgba(140, 0, 255, 0.1); -fx-border-color: rgba(140, 0, 255, 0.3); -fx-border-width: 1px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
                typeIcon  = "⏩";
                typeColor = "#6A0DAD";
            } else if (cell instanceof ContaminationSock) {
                tileBlock.setStyle("-fx-background-color: rgba(217, 56, 56, 0.1); -fx-border-color: rgba(217, 56, 56, 0.3); -fx-border-width: 1px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
                typeIcon  = "⚠ SOCK";
                typeColor = "#A61C1C";
            } else if (cell instanceof DoorCell) {
                DoorCell doorCell = (DoorCell) cell;
                if (doorCell.isActivated()) {
                    tileBlock.setStyle("-fx-background-color: rgba(0,0,0,0.05); -fx-border-color: rgba(0,0,0,0.12); -fx-border-width: 1px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
                    typeIcon = "✖ USED"; 
                    typeColor = "#4A4A5A";
                } else {
                    int doorEnergy = doorCell.getEnergy(); 
                    if (doorCell.getRole().toString().equals("SCARER")) {
                        tileBlock.setStyle("-fx-background-color: rgba(217, 56, 56, 0.15); -fx-border-color: #D93838; -fx-border-width: 1px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
                        typeIcon = "🚪 SCARER\n(" + doorEnergy + ")"; 
                        typeColor = "#A61C1C"; 
                    } else {
                        tileBlock.setStyle("-fx-background-color: rgba(0, 158, 140, 0.15); -fx-border-color: #009E8C; -fx-border-width: 1px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
                        typeIcon = "🚪 LAUGHER\n(" + doorEnergy + ")"; 
                        typeColor = "#007A6C"; 
                    }
                }
            } else if (cell instanceof TransportCell) {
                tileBlock.setStyle("-fx-background-color: rgba(0, 158, 140, 0.1); -fx-border-color: rgba(0, 158, 140, 0.3); -fx-border-width: 1px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
                typeIcon  = "PORTAL";
                typeColor = "#007A6C";
            }

            VBox tileContent = new VBox(2);
            tileContent.setAlignment(Pos.CENTER);

            Label indexLabel = new Label(String.valueOf(index + 1));
            indexLabel.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #4A4A5A;");
            tileContent.getChildren().add(indexLabel);

            if (!typeIcon.isEmpty()) {
                Label iconLabel = new Label(typeIcon);
                iconLabel.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + typeColor + "; -fx-text-alignment: center;");
                tileContent.getChildren().add(iconLabel);
            }

            // Tokens
            HBox tokensBox = new HBox(4);
            tokensBox.setAlignment(Pos.CENTER);

            if (index == playerPos) {
                Label pt = new Label("YOU");
                pt.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-background-color: #009E8C; -fx-padding: 2px 5px; -fx-background-radius: 4px;");
                tokensBox.getChildren().add(pt);
            }
            if (index == opponentPos) {
                Label ot = new Label("OPP");
                ot.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-background-color: #D93838; -fx-padding: 2px 5px; -fx-background-radius: 4px;");
                tokensBox.getChildren().add(ot);
            }

            if (!tokensBox.getChildren().isEmpty()) {
                tileContent.getChildren().add(tokensBox);
            }

            tileBlock.getChildren().add(tileContent);
            gridPane.add(tileBlock, gridCol, gridRow);
        }

        currentTurnValue.setText(current.getName());
        currentRoleLabel.setText(current.getRole().toString() + "  |  Cell " + (current.getPosition() + 1));

        String playerType = ""; try { playerType = " [" + player.getClass().getSimpleName() + "]"; } catch(Exception e) {}
        playerNameLabel.setText(player.getName() + playerType);
        playerEnergyValue.setText(player.getEnergy() + " / 1000 Energy");
        playerEnergyBar.setProgress(Math.min(1.0, player.getEnergy() / (double) Constants.WINNING_ENERGY));
        updateStatusBadges(playerStatusBox, player);

        String opponentType = ""; try { opponentType = " [" + opponent.getClass().getSimpleName() + "]"; } catch(Exception e) {}
        opponentNameLabel.setText(opponent.getName() + opponentType);
        opponentEnergyValue.setText(opponent.getEnergy() + " / 1000 Energy");
        opponentEnergyBar.setProgress(Math.min(1.0, opponent.getEnergy() / (double) Constants.WINNING_ENERGY));
        updateStatusBadges(opponentStatusBox, opponent);

        if (current == player) {
            setStatCardActive(playerStatsBox);
            setStatCardIdle(opponentStatsBox);
        } else {
            setStatCardActive(opponentStatsBox);
            setStatCardIdle(playerStatsBox);
        }

        updatePowerupButton();
        turnCountLabel.setText("TURN  #" + gameSession.getTurnCount());
        
        checkGameEndConditions();
    }

    private void updateStatusBadges(HBox statusBox, Monster monster) {
        if (statusBox == null || monster == null) return; 
        statusBox.getChildren().clear();
        statusBox.setSpacing(5);

        try {
            if (monster.getRole() != null) {
                Label roleBadge = new Label(monster.getRole().toString());
                roleBadge.setStyle(
                    "-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3px 7px; -fx-background-radius: 4px; -fx-text-fill: #FFFFFF; " +
                    "-fx-background-color: " + (monster.getRole().toString().equals("SCARER") ? "#D93838;" : "#009E8C;")
                );
                statusBox.getChildren().add(roleBadge);
            }
        } catch (Exception e) {}

        try {
            if (monster.isFrozen()) {
                Label freezeBadge = new Label("❄ FROZEN");
                freezeBadge.setStyle(
                    "-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 10px; -fx-font-weight: bold; " +
                    "-fx-padding: 3px 7px; -fx-background-radius: 4px; -fx-text-fill: #FFFFFF; " +
                    "-fx-background-color: #3A8FC7; " +
                    "-fx-effect: dropshadow(gaussian, rgba(58,143,199,0.4), 6, 0, 0, 1);"
                );
                statusBox.getChildren().add(freezeBadge);
            }
        } catch (Exception e) {}
    }

    private void setStatCardActive(VBox box) {
        box.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9); " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #009E8C; " +
            "-fx-border-width: 1.5px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,158,140,0.1), 8, 0, 0, 2);"
        );
    }

    private void setStatCardIdle(VBox box) {
        box.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.65); " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: rgba(0, 0, 0, 0.1); " +
            "-fx-border-width: 1px;"
        );
    }

    private void showToastMessage(String message) {
        Label toast = new Label(message);
        toast.setStyle(
            "-fx-font-family: 'Lucida Sans Unicode'; -fx-background-color: rgba(255, 255, 255, 0.98);" +
            "-fx-text-fill: #009E8C;" +
            "-fx-padding: 12px 24px;" +
            "-fx-background-radius: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-border-color: rgba(0,0,0,0.12); -fx-border-radius: 20px; -fx-border-width: 1px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 2);"
        );

        StackPane.setAlignment(toast, Pos.TOP_CENTER);
        StackPane.setMargin(toast, new Insets(60, 0, 0, 0));

        overlayContainer.getChildren().add(toast);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toast);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        PauseTransition stay = new PauseTransition(Duration.millis(1600));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(700), toast);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        SequentialTransition seq = new SequentialTransition(fadeIn, stay, fadeOut);
        seq.setOnFinished(e -> overlayContainer.getChildren().remove(toast));
        seq.play();
    }

    private HBox buildLegendBar() {
        HBox bar = new HBox(18);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(8, 16, 8, 16));
        bar.setStyle("-fx-background-color: rgba(255, 255, 255, 0.65); -fx-background-radius: 10px; -fx-border-color: rgba(0,0,0,0.1); -fx-border-width: 1px;");
        bar.setMaxWidth(Double.MAX_VALUE);

        bar.getChildren().addAll(
            makeLegendItem("#007ACC", "Monster"),
            makeLegendItem("#8B6508", "Card draw"),
            makeLegendItem("#6A0DAD", "Conveyor"),
            makeLegendItem("#A61C1C", "Sock hazard"),
            makeLegendItem("#007A6C", "Factory Door")
        );
        return bar;
    }

    private HBox makeLegendItem(String color, String text) {
        HBox item = new HBox(6);
        item.setAlignment(Pos.CENTER);

        Region dot = new Region();
        dot.setPrefSize(8, 8);
        dot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4px;");

        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-family: 'Lucida Sans Unicode'; -fx-font-size: 12px; -fx-text-fill: #2B2B36; -fx-font-weight: bold;");

        item.getChildren().addAll(dot, lbl);
        return item;
    }

    private void checkGameEndConditions() {
        Monster player = gameSession.getPlayer();
        Monster opponent = gameSession.getOpponent();
        
        boolean playerWon = (player.getPosition() >= 99 && player.getEnergy() >= 1000);
        boolean opponentWon = (opponent.getPosition() >= 99 && opponent.getEnergy() >= 1000);

        if (playerWon) {
            showGameOverScreen(player, opponent, "🏆 PLAYER VICTORY");
        } else if (opponentWon) {
            showGameOverScreen(opponent, player, "🤖 OPPONENT VICTORY");
        }
    }

    // ── GAME OVER BLUEPRINT MODAL ──
    private void showGameOverScreen(Monster winner, Monster loser, String headerText) {
        showStyledDialog(headerText, 
            "🏆 MATCH OVER REPORT 🏆\n\n" +
            "👑 Champion: " + winner.getName() + " (" + winner.getRole() + ")\n" +
            "• Final Grid Position: Cell " + (winner.getPosition() + 1) + "\n\n" +
            "📊 CELL DISPATCH RESERVES:\n" +
            "• " + winner.getName() + ": " + winner.getEnergy() + " Accumulated Energy\n" +
            "• " + loser.getName() + ": " + loser.getEnergy() + " Accumulated Energy", 
            () -> {
                if (sceneManager != null) {
                    sceneManager.showMainMenu();
                }
            }
        );
    }
    
    public void setupTestingCheatKeys() {
        this.setOnKeyPressed(event -> {
            Monster current = gameSession.getPlayer(); 
            
            if (event.getCode() == javafx.scene.input.KeyCode.W) {
                System.out.println("⚡ [Cheat]: Warping player to cell 99...");
                current.setPosition(99);
                if (current.getEnergy() < 1000) {
                    current.setEnergy(1050);
                }
                refreshBoardDisplay(); 
                checkGameEndConditions();
            } 
            else if (event.getCode() == javafx.scene.input.KeyCode.E) {
                System.out.println("🔋 [Cheat]: Boosting player energy!");
                current.setEnergy(current.getEnergy() + 400); 
                refreshBoardDisplay(); 
            }
        });
    }
}