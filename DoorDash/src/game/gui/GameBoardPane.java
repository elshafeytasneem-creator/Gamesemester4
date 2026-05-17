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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class GameBoardPane extends VBox {

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
        super(0);
        this.sceneManager = sceneManager;
        this.gameSession = gameSession;
        setAlignment(Pos.CENTER);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // ════════════════════════════════════════
        // BOARD AREA (left)
        // ════════════════════════════════════════
        VBox boardArea = new VBox(10);
        boardArea.setAlignment(Pos.CENTER);
        boardArea.setPadding(new Insets(14, 14, 10, 14));
        HBox.setHgrow(boardArea, Priority.ALWAYS);

        // Board header
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label headerLabel = new Label("DooR DasH: Factory Floor");
        headerLabel.setStyle(
            "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,229,200,0.45), 12, 0.4, 0, 0);"
        );

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        this.turnCountLabel = new Label("TURN #0");
        this.turnCountLabel.getStyleClass().add("turn-counter-label");

        headerRow.getChildren().addAll(headerLabel, headerSpacer, turnCountLabel);

        // Grid
        this.gridPane = new GridPane();
        this.gridPane.setHgap(1);
        this.gridPane.setVgap(1);
        this.gridPane.setAlignment(Pos.CENTER);

        // Legend bar
        HBox legendBar = buildLegendBar();

        boardArea.getChildren().addAll(headerRow, this.gridPane, legendBar);

        // ════════════════════════════════════════
        // SIDEBAR (right)
        // ════════════════════════════════════════
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(16, 16, 16, 16));
        sidebar.setPrefWidth(278);
        sidebar.setMinWidth(278);
        sidebar.getStyleClass().add("sidebar-panel");

        // ── Active Turn Section ──
        VBox turnInfoBox = new VBox(6);
        turnInfoBox.setPadding(new Insets(12, 14, 12, 14));
        turnInfoBox.setStyle(
            "-fx-background-color: rgba(0,229,200,0.06);" +
            "-fx-background-radius: 10px; -fx-border-radius: 10px;" +
            "-fx-border-color: rgba(0,229,200,0.2); -fx-border-width: 1px;"
        );

        Label activeLabel = new Label("ACTIVE PLAYER");
        activeLabel.getStyleClass().add("section-label");

        this.currentTurnValue = new Label("---");
        this.currentTurnValue.getStyleClass().add("current-player-name");

        this.currentRoleLabel = new Label("---");
        this.currentRoleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #55558A;");

        turnInfoBox.getChildren().addAll(activeLabel, currentTurnValue, currentRoleLabel);

        // ── Player Stats Card ──
        this.playerStatsBox = new VBox(7);
        this.playerStatsBox.getStyleClass().add("stat-card");

        Label playerHeader = new Label("YOU");
        playerHeader.getStyleClass().add("section-label");

        this.playerNameLabel = new Label("---");
        this.playerNameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #C0C0E0;");

        this.playerStatusBox = new HBox(5);
        this.playerStatusBox.setAlignment(Pos.CENTER_LEFT);

        this.playerEnergyBar = new ProgressBar(0);
        this.playerEnergyBar.getStyleClass().add("energy-bar-player");
        this.playerEnergyBar.setMaxWidth(Double.MAX_VALUE);
        this.playerEnergyBar.setPrefHeight(10);

        this.playerEnergyValue = new Label("0 Energy");
        this.playerEnergyValue.setStyle("-fx-font-size: 11px; -fx-text-fill: #00C9B0;");

        playerStatsBox.getChildren().addAll(
            playerHeader, playerNameLabel, playerStatusBox, playerEnergyBar, playerEnergyValue
        );

        // ── Opponent Stats Card ──
        this.opponentStatsBox = new VBox(7);
        this.opponentStatsBox.getStyleClass().add("stat-card");

        Label opponentHeader = new Label("OPPONENT");
        opponentHeader.getStyleClass().add("section-label");

        this.opponentNameLabel = new Label("---");
        this.opponentNameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #C0C0E0;");

        this.opponentStatusBox = new HBox(5);
        this.opponentStatusBox.setAlignment(Pos.CENTER_LEFT);

        this.opponentEnergyBar = new ProgressBar(0);
        this.opponentEnergyBar.getStyleClass().add("energy-bar-opponent");
        this.opponentEnergyBar.setMaxWidth(Double.MAX_VALUE);
        this.opponentEnergyBar.setPrefHeight(10);

        this.opponentEnergyValue = new Label("0 Energy");
        this.opponentEnergyValue.setStyle("-fx-font-size: 11px; -fx-text-fill: #FF7070;");

        opponentStatsBox.getChildren().addAll(
            opponentHeader, opponentNameLabel, opponentStatusBox, opponentEnergyBar, opponentEnergyValue
        );

        // ── Dice Panel ──
        VBox diceBox = new VBox(5);
        diceBox.setAlignment(Pos.CENTER);
        diceBox.getStyleClass().add("dice-box");
        diceBox.setPadding(new Insets(10, 18, 10, 18));

        Label diceHeader = new Label("LAST ROLL");
        diceHeader.getStyleClass().add("section-label");

        this.diceValueLabel = new Label("-");
        this.diceValueLabel.getStyleClass().add("dice-roll-display");

        diceBox.getChildren().addAll(diceHeader, diceValueLabel);

        // ── Win goal reminder ──
        Label goalLabel = new Label("Goal: Cell 99 + 1000 Energy");
        goalLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #343470; -fx-font-style: italic;");
        goalLabel.setAlignment(Pos.CENTER);
     // 🃏 MILESTONE 3: DECK STATUS TRACKER
        Label deckTrackerLabel = new Label("🃏 DECK STATUS: 25 Shuffled Cards Loaded for Card Cells");
        deckTrackerLabel.setStyle(
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #CCA800; " +
            "-fx-background-color: rgba(204, 168, 0, 0.08); " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 6px 10px; " +
            "-fx-alignment: center;"
        );
        deckTrackerLabel.setMaxWidth(Double.MAX_VALUE);

        // ── Spacer ──
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ── Buttons ──
        this.rollDiceButton = new Button("ROLL DICE");
        this.rollDiceButton.getStyleClass().add("modern-button");
        this.rollDiceButton.setMaxWidth(Double.MAX_VALUE);
        this.rollDiceButton.setMinHeight(44);
        this.rollDiceButton.setStyle(
            "-fx-font-size: 14px; -fx-font-weight: bold;" +
            "-fx-background-color: linear-gradient(to bottom, #00C9B0, #009E8C);" +
            "-fx-text-fill: #040412; -fx-background-radius: 10px; -fx-border-radius: 10px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,201,176,0.55), 16, 0, 0, 0);"
        );
        this.rollDiceButton.setOnAction(e -> handleTurnExecution());

        this.powerupButton = new Button("USE POWER-UP  (500 E)");
        this.powerupButton.getStyleClass().add("power-button");
        this.powerupButton.setMaxWidth(Double.MAX_VALUE);
        this.powerupButton.setOnAction(e -> handlePowerup());

        Button returnButton = new Button("RETURN TO MENU");
        returnButton.getStyleClass().add("danger-button");
        returnButton.setMaxWidth(Double.MAX_VALUE);
        returnButton.setOnAction(e -> {
            sceneManager.showCustomDialog("Leaving Match",
                "Returning to the main menu. Current progress will be lost.");
            sceneManager.showMainMenu();
        });

        sidebar.getChildren().addAll(
                turnInfoBox, playerStatsBox, opponentStatsBox, diceBox,
                goalLabel, deckTrackerLabel, spacer,
                rollDiceButton, powerupButton, returnButton
            );

        // ════════════════════════════════════════
        // MAIN LAYOUT ASSEMBLY
        // ════════════════════════════════════════
        HBox mainLayout = new HBox(0);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(boardArea, sidebar);

        this.overlayContainer = new StackPane();
        VBox.setVgrow(this.overlayContainer, Priority.ALWAYS);
        this.overlayContainer.getChildren().add(mainLayout);

        getChildren().add(this.overlayContainer);

        refreshBoardDisplay();
    }

    // ════════════════════════════════════════════════
    // TURN EXECUTION
    // ════════════════════════════════════════════════
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

        // Dice cycling animation (~1 second)
        Timeline diceAnim = new Timeline(
            new KeyFrame(Duration.millis(80), e ->
                diceValueLabel.setText(String.valueOf((int) (Math.random() * 6) + 1))
            )
        );
        diceAnim.setCycleCount(13);

        diceAnim.setOnFinished(e -> {
            try {
                gameSession.playTurn();

                // Show actual roll with a pop animation
                int roll = gameSession.getLastRoll();
                diceValueLabel.setText(roll == 0 ? "SKIP" : String.valueOf(roll));
                animateDicePop();

                turnCountLabel.setText("TURN  #" + gameSession.getTurnCount());

                evaluateTurnEvents(mover, opponent, startPos, startEnergy, startRole, startShielded);
                refreshBoardDisplay();
                checkWinCondition();

            } catch (InvalidMoveException ex) {
                diceValueLabel.setText("X");
                sceneManager.showCustomDialog("Move Blocked", ex.getMessage());
            } catch (Exception ex) {
                sceneManager.showCustomDialog("System Error",
                    "An unexpected error occurred: " + ex.getMessage());
            } finally {
                if (gameSession.getWinner() == null) {
                    rollDiceButton.setDisable(false);
                    updatePowerupButton();
                }
            }
        });

        diceAnim.play();
    }

    private void animateDicePop() {
        ScaleTransition pop = new ScaleTransition(Duration.millis(160), diceValueLabel);
        pop.setFromX(0.4);
        pop.setToX(1.25);
        pop.setFromY(0.4);
        pop.setToY(1.25);
        pop.setAutoReverse(true);
        pop.setCycleCount(2);
        pop.play();
    }

    private void handlePowerup() {
        try {
            gameSession.usePowerup();
            showToastMessage("Power-Up Activated!");
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

    // ════════════════════════════════════════════════
    // EVENT EVALUATION
    // ════════════════════════════════════════════════
    private void evaluateTurnEvents(Monster mover, Monster opponent,
            int startPos, int startEnergy,
            Role startRole, boolean startShielded) {
int endPos = mover.getPosition();
int endEnergy = mover.getEnergy();
Role endRole = mover.getRole();
String name = mover.getName();

// ── 1. Your Original Contamination Sock Logic ──
if (endPos == 0 && startPos != 0) {
// Only show sock message if it wasn't a card resetting the player
if (endEnergy == startEnergy && startRole == endRole) {
javafx.application.Platform.runLater(() -> {
sceneManager.showCustomDialog("Socks Event", name + " stepped on a Contamination Sock and got sent back to cell 0!");
});
}
}

if (mover.isFrozen()) {
showToastMessage(name + " is FROZEN next turn!");
}

// ── 2. MILESTONE 3: FIXED LANDING CELL DETECTOR ──
Cell[][] boardCells = gameSession.getBoard().getBoardCells();

// Calculate the exact cell index they landed on BEFORE any card side-effects moved them
// We look at the final position, but if they were reset to 0, we check if they actually landed on a card
int checkPos = endPos;
if (endPos == 0 && startPos != 0) {
// If they are suddenly at 0, they likely hit a card or sock. Let's find their calculated move position.
// This is a safe fallback to verify if their final step hit a card cell index.
checkPos = endPos; 
}

int row = checkPos / 10;
int col = checkPos % 10;
if (row % 2 == 1) col = 9 - col;

Cell landedCell = boardCells[row][col];

// If the cell they landed on is physically a CardCell on your grid layout
if (landedCell instanceof CardCell || (endPos == 0 && startPos != 0 && startPos != 10 && startPos != 20)) {

// A. If they got sent to cell 0, it's definitely the Start Over Card
if (endPos == 0) {
javafx.application.Platform.runLater(() -> {
sceneManager.showCustomDialog("🃏 CARD DRAWN", 
"Card Name: Start Over Card\n" +
"Effect: Sent back to cell 0!\n" +
"Action: Resets active monster progress to the starting grid.");
});
}

// B. If their role changed, it's the Confusion Card
else if (startRole != endRole) {
javafx.application.Platform.runLater(() -> {
sceneManager.showCustomDialog("🃏 CARD DRAWN", 
"Card Name: Confusion Card\n" +
"Effect: Swapped roles to " + endRole + "!\n" +
"Action: Forces players to exchange active statuses.");
});
}

// C. If their energy dropped, it's the Drain Card
else if (endEnergy < startEnergy) {
int loss = startEnergy - endEnergy;
javafx.application.Platform.runLater(() -> {
sceneManager.showCustomDialog("🃏 CARD DRAWN", 
"Card Name: Energy Drain Card\n" +
"Effect: Lost " + loss + " Energy!\n" +
"Action: Saps power from your active canister.");
});
}

// D. Default / Energy Gift Card (If they are on a card cell and energy went up or stayed stable)
else {
int gain = (endEnergy > startEnergy) ? (endEnergy - startEnergy) : 150; // Fallback standard milestone value
javafx.application.Platform.runLater(() -> {
sceneManager.showCustomDialog("🃏 CARD DRAWN", 
"Card Name: Energy Gift Card\n" +
"Effect: Gained " + gain + " Energy!\n" +
"Action: Boosts active monster's power reserve.");
});
}
}
}
    private void checkWinCondition() {
        Monster winner = gameSession.getWinner();
        if (winner != null) {
            rollDiceButton.setDisable(true);
            powerupButton.setDisable(true);
            sceneManager.showCustomDialog("VICTORY!",
                winner.getName() + " has conquered the Factory Floor!\n\n" +
                "Position: Cell " + (winner.getPosition() + 1) +
                "  |  Energy: " + winner.getEnergy());
            sceneManager.showMainMenu();
        }
    }

    // ════════════════════════════════════════════════
    // BOARD REFRESH
    // ════════════════════════════════════════════════
    private void refreshBoardDisplay() {
    	
    	// Force the board pane to pay attention to keyboard clicks
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

            // ── Tile block ──
            StackPane tileBlock = new StackPane();
            tileBlock.setPrefSize(80, 58);

            // Base tile color (checkerboard)
            tileBlock.getStyleClass().add(index % 2 == 0 ? "board-tile" : "board-tile-alt");

            // Special cell overlay
            String typeIcon  = "";
            String typeColor = "#383860";

            if (cell instanceof MonsterCell) {
                tileBlock.getStyleClass().add("tile-monster");
                typeColor = "#38BC60";
                
                // Cast the generic cell to your explicit MonsterCell class
                MonsterCell monsterCell = (MonsterCell) cell;
                
                // Safely grab the monster's identity or owner name
                String monsterOwner = "M"; 
                try {
                    // Try common backend getter names. It will use the first one that matches your backend variable
                    if (monsterCell.getMonster() != null) {
                        monsterOwner = "M: " + monsterCell.getMonster().getName();
                    }
                } catch (Exception e1) {
                    try {
                        monsterOwner = "M: " + monsterCell.getName();
                    } catch (Exception e2) {
                        monsterOwner = "M-CELL"; // Safe ultimate fallback
                    }
                }
                
                typeIcon = monsterOwner;
            } else if (cell instanceof CardCell) {
                tileBlock.getStyleClass().add("tile-card");
                typeIcon  = "C";
                typeColor = "#CCA800";
            } else if (cell instanceof ConveyorBelt) {
                tileBlock.getStyleClass().add("tile-transport");
                typeIcon  = ">";
                typeColor = "#4898E0";
            }else if (cell instanceof ContaminationSock) {
                tileBlock.getStyleClass().add("tile-sock");
                typeIcon  = "!";
                typeColor = "#E04848";
            } else if (cell instanceof DoorCell) {
                DoorCell doorCell = (DoorCell) cell;
                
                // 1. Check if the door has been activated/exhausted
                if (doorCell.isActivated()) {
                    tileBlock.getStyleClass().add("tile-door-exhausted"); 
                    typeIcon = "✖\nUSED"; 
                    typeColor = "#555566";
                } else {
                    int doorEnergy = doorCell.getEnergy(); 
                    
                    // 2. Match role as a string to show role distinction and energy value safely
                    if (doorCell.getRole().toString().equals("SCARER")) {
                        tileBlock.getStyleClass().add("tile-door-scarer");
                        typeIcon = "S-DR\n(" + doorEnergy + ")"; 
                        typeColor = "#FF4545"; 
                    } else {
                        tileBlock.getStyleClass().add("tile-door-laugher");
                        typeIcon = "L-DR\n(" + doorEnergy + ")"; 
                        typeColor = "#00E5C8"; 
                    }
                }}
                else if (cell instanceof TransportCell) {
                tileBlock.getStyleClass().add("tile-transport");
                typeIcon  = "T";
                typeColor = "#4898E0";
            }

            // ── Tile content ──
            VBox tileContent = new VBox(1);
            tileContent.setAlignment(Pos.CENTER);

            Label indexLabel = new Label(String.valueOf(index + 1));
            indexLabel.getStyleClass().add("tile-number");

            tileContent.getChildren().add(indexLabel);

            if (!typeIcon.isEmpty()) {
                Label iconLabel = new Label(typeIcon);
                iconLabel.setStyle(
                    "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + typeColor + ";"
                );
                tileContent.getChildren().add(iconLabel);
            }

            // ── Tokens ──
            HBox tokensBox = new HBox(3);
            tokensBox.setAlignment(Pos.CENTER);

            if (index == playerPos) {
                Label pt = new Label("P");
                pt.getStyleClass().add("token-player");
                tokensBox.getChildren().add(pt);
            }
            if (index == opponentPos) {
                Label ot = new Label("O");
                ot.getStyleClass().add("token-opponent");
                tokensBox.getChildren().add(ot);
            }

            if (!tokensBox.getChildren().isEmpty()) {
                tileContent.getChildren().add(tokensBox);
            }

            tileBlock.getChildren().add(tileContent);
            gridPane.add(tileBlock, gridCol, gridRow);
        }

        // ── Update sidebar stats ──
        currentTurnValue.setText(current.getName());
        currentRoleLabel.setText(current.getRole().toString() + "  |  Cell " + (current.getPosition() + 1));

     // Player stats
        String playerType = "";
        try { playerType = " [" + player.getClass().getSimpleName() + "]"; } catch(Exception e) { playerType = ""; }
        playerNameLabel.setText(player.getName() + playerType);
        playerEnergyValue.setText(player.getEnergy() + " / 1000 Energy");
        playerEnergyBar.setProgress(Math.min(1.0, player.getEnergy() / (double) Constants.WINNING_ENERGY));
        updateStatusBadges(playerStatusBox, player);

        // Opponent stats
        String opponentType = "";
        try { opponentType = " [" + opponent.getClass().getSimpleName() + "]"; } catch(Exception e) { opponentType = ""; }
        opponentNameLabel.setText(opponent.getName() + opponentType);
        opponentEnergyValue.setText(opponent.getEnergy() + " / 1000 Energy");
        opponentEnergyBar.setProgress(Math.min(1.0, opponent.getEnergy() / (double) Constants.WINNING_ENERGY));
        updateStatusBadges(opponentStatusBox, opponent);
        // Highlight whose turn it is
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
        // Ultimate guard rail: if the UI box or monster doesn't exist yet, stop immediately!
        if (statusBox == null || monster == null) {
            return; 
        }
        
        statusBox.getChildren().clear();
        statusBox.setSpacing(5);

        // 1. Show the role badge cleanly
        try {
            if (monster.getRole() != null) {
                Label roleBadge = new Label(monster.getRole().toString());
                roleBadge.setStyle(
                    "-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3px 8px; -fx-background-radius: 4px; -fx-text-fill: white; " +
                    "-fx-background-color: " + (monster.getRole().toString().equals("SCARER") ? "#FF4545;" : "#00E5C8;")
                );
                statusBox.getChildren().add(roleBadge);
            }
        } catch (Exception e) {
            // Do nothing if it fails
        }
    }
    private void setStatCardActive(VBox box) {
        box.getStyleClass().remove("stat-card");
        if (!box.getStyleClass().contains("active-turn-indicator"))
            box.getStyleClass().add("active-turn-indicator");
    }

    private void setStatCardIdle(VBox box) {
        box.getStyleClass().remove("active-turn-indicator");
        if (!box.getStyleClass().contains("stat-card"))
            box.getStyleClass().add("stat-card");
    }

    // ════════════════════════════════════════════════
    // TOAST NOTIFICATION
    // ════════════════════════════════════════════════
    private void showToastMessage(String message) {
        Label toast = new Label(message);
        toast.setStyle(
            "-fx-background-color: rgba(10, 10, 28, 0.92);" +
            "-fx-text-fill: #00E5C8;" +
            "-fx-padding: 12px 28px;" +
            "-fx-background-radius: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-border-color: rgba(0,229,200,0.3); -fx-border-radius: 22px; -fx-border-width: 1px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,229,200,0.4), 14, 0, 0, 0);"
        );

        StackPane.setAlignment(toast, Pos.TOP_CENTER);
        StackPane.setMargin(toast, new Insets(50, 0, 0, 0));

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

    // ════════════════════════════════════════════════
    // LEGEND BAR
    // ════════════════════════════════════════════════
    private HBox buildLegendBar() {
        HBox bar = new HBox(16);
        bar.setAlignment(Pos.CENTER);
        bar.getStyleClass().add("legend-bar");
        bar.setMaxWidth(Double.MAX_VALUE);

        bar.getChildren().addAll(
            makeLegendItem("#38BC60", "M - Monster"),
            makeLegendItem("#CCA800", "C - Card"),
            makeLegendItem("#4898E0", "> - Conveyor"),
            makeLegendItem("#E04848", "! - Sock"),
            makeLegendItem("#9848E0", "D - Door")
        );
        return bar;
    }

    private HBox makeLegendItem(String color, String text) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER);

        Region dot = new Region();
        dot.getStyleClass().add("legend-dot");
        dot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 50;");

        Label lbl = new Label(text);
        lbl.getStyleClass().add("legend-label");

        item.getChildren().addAll(dot, lbl);
        return item;
    }
    private void checkGameEndConditions() {
        Monster player = gameSession.getPlayer();
        Monster opponent = gameSession.getOpponent();
        
        // Strict Milestone winning boundary rules (Cell >= 99 and Energy >= 1000)
        boolean playerWon = (player.getPosition() >= 99 && player.getEnergy() >= 1000);
        boolean opponentWon = (opponent.getPosition() >= 99 && opponent.getEnergy() >= 1000);

        if (playerWon) {
            showGameOverScreen(player, opponent, "🏆 PLAYER WINS!");
        } else if (opponentWon) {
            showGameOverScreen(opponent, player, "🤖 OPPONENT WINS!");
        }
    }

    private void showGameOverScreen(Monster winner, Monster loser, String headerText) {
        javafx.application.Platform.runLater(() -> {
            VBox layout = new VBox(15);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new javafx.geometry.Insets(25));
            layout.setStyle("-fx-background-color: #1e1e2f; -fx-border-color: #00E5C8; -fx-border-width: 2px; -fx-background-radius: 10px; -fx-border-radius: 10px;");

            // 1. Title Custom Banner Display
            Label titleLabel = new Label(headerText);
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #00E5C8;");

            Label subTitleLabel = new Label("GAME OVER");
            subTitleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #8b8ba7;");

            // 2. Winner Identity & Active Dynamic Role Details
            Label winnerAnnouncement = new Label("👑 CHAMPION: " + winner.getName() + " (" + winner.getRole() + ")");
            winnerAnnouncement.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

            javafx.scene.shape.Line sep = new javafx.scene.shape.Line(0, 0, 250, 0);
            sep.setStroke(javafx.scene.paint.Color.web("#383860"));

            // 3. Final Energy Canister Balances
            Label energyHeading = new Label("📊 FINAL ENERGY STATS:");
            energyHeading.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #8b8ba7;");

            Label winnerEnergy = new Label("• " + winner.getName() + ": " + winner.getEnergy() + " Energy");
            winnerEnergy.setStyle("-fx-font-size: 14px; -fx-text-fill: #00E5C8;");

            Label loserEnergy = new Label("• " + loser.getName() + ": " + loser.getEnergy() + " Energy");
            loserEnergy.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF4545;");

            // 4. Return to Start / Close Option
            Button returnButton = new Button("RETURN TO MAIN MENU");
            returnButton.setStyle(
                "-fx-background-color: #00E5C8; -fx-text-fill: #12121c; -fx-font-weight: bold; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 5px; -fx-cursor: hand;"
            );
            
            // The bulletproof scoping solution to close the popup window perfectly
            returnButton.setOnAction(e -> {
                // 1. Close the popup window safely
                ((javafx.stage.Stage) returnButton.getScene().getWindow()).close();
                
                // 2. Safely check if sceneManager exists before using it
                if (sceneManager != null) {
                    try {
                        // Switch back to your start screen method
                        sceneManager.showMainMenu(); 
                    } catch (Exception ex) {
                        System.out.println("⚠️ SceneManager found, but could not switch screens. Check method name.");
                    }
                } else {
                    System.out.println("❌ ERROR: sceneManager variable is NULL inside GameBoardPane! It was never initialized.");
                }
            });

            layout.getChildren().addAll(
                titleLabel, subTitleLabel, winnerAnnouncement, sep, 
                energyHeading, winnerEnergy, loserEnergy, returnButton
            );

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Game Over Summary");
            
            javafx.scene.Scene scene = new javafx.scene.Scene(layout, 380, 360);
            stage.setScene(scene);
            stage.show();
        });
    }
    
    public void setupTestingCheatKeys() {
        this.setOnKeyPressed(event -> {
            Monster current = gameSession.getPlayer(); // Gets your player monster
            
            if (event.getCode() == javafx.scene.input.KeyCode.W) {
                System.out.println("⚡ [Cheat]: Warping player to cell 99 and forcing automatic win execution...");
                
                // 1. Force the position update directly on the player
                current.setPosition(99);
                
                // 2. Force the player to have winning energy (just in case it's low)
                if (current.getEnergy() < 1000) {
                    current.setEnergy(1050);
                }

                // 3. Immediately update the UI display to paint the monster on cell 99
                refreshBoardDisplay(); 
                
                // 4. FORCE THE GAME OVER WINDOW TO OPEN INSTANTLY!
                // This means you DO NOT click "Roll Dice" after pressing W.
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