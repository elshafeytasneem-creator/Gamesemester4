package game.gui;

import game.engine.Board;
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
import game.engine.exceptions.InvalidTurnException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class GameBoardPane extends VBox {

    private final SceneManager sceneManager;
    private final Game gameSession;
    private final GridPane gridPane;
    private final StackPane overlayContainer;
    
    private final Label currentTurnValue;
    private final Label playerEnergyValue;
    private final Label opponentEnergyValue;
    private final Button rollDiceButton;

    public GameBoardPane(SceneManager sceneManager, Game gameSession) {
        super(0);
        this.sceneManager = sceneManager;
        this.gameSession = gameSession;
        setAlignment(Pos.CENTER);

        // --- Main Board Area ---
        VBox boardArea = new VBox(20);
        boardArea.setAlignment(Pos.CENTER);
        boardArea.setPadding(new Insets(20));
        HBox.setHgrow(boardArea, Priority.ALWAYS);

        Label headerLabel = new Label("DooR DasH: Factory Floor");
        headerLabel.getStyleClass().add("game-title");

        this.gridPane = new GridPane();
        this.gridPane.setHgap(2);
        this.gridPane.setVgap(2);
        this.gridPane.setAlignment(Pos.CENTER);

        boardArea.getChildren().addAll(headerLabel, this.gridPane);

        // --- Sidebar Panel Layout ---
        VBox sidebar = new VBox(25);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(280);
        sidebar.setStyle("-fx-background-color: #15151A; -fx-border-color: #3E4451; -fx-border-width: 0 0 0 1px;");
        
        VBox statsBox = new VBox(20);
        statsBox.setAlignment(Pos.TOP_LEFT);

        VBox turnEntry = createStatEntry("ACTIVE PLAYER", "#00E5FF");
        this.currentTurnValue = (Label) turnEntry.getChildren().get(1);

        VBox playerEnergyEntry = createStatEntry("YOUR ENERGY", "#FFC107");
        this.playerEnergyValue = (Label) playerEnergyEntry.getChildren().get(1);

        VBox opponentEnergyEntry = createStatEntry("OPPONENT ENERGY", "#FFC107");
        this.opponentEnergyValue = (Label) opponentEnergyEntry.getChildren().get(1);

        statsBox.getChildren().addAll(turnEntry, playerEnergyEntry, opponentEnergyEntry);

        this.rollDiceButton = new Button("Roll Dice");
        this.rollDiceButton.getStyleClass().add("modern-button");
        this.rollDiceButton.setMaxWidth(Double.MAX_VALUE);
        this.rollDiceButton.setOnAction(event -> handleTurnExecution());

        Button returnButton = new Button("Return to Menu");
        returnButton.getStyleClass().add("modern-button");
        returnButton.setMaxWidth(Double.MAX_VALUE);
        returnButton.setStyle("-fx-background-color: #3E4451;");
        returnButton.setOnAction(event -> {
            sceneManager.showCustomDialog("Leaving Match", "Leaving Match. Current game session records will be cleared.");
            sceneManager.showMainMenu();
        });

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(statsBox, spacer, this.rollDiceButton, returnButton);

        // --- Main Layout Assembly with Overlay Support ---
        HBox mainLayout = new HBox(0);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(boardArea, sidebar);

        this.overlayContainer = new StackPane();
        VBox.setVgrow(this.overlayContainer, Priority.ALWAYS);
        this.overlayContainer.getChildren().add(mainLayout);

        getChildren().add(this.overlayContainer);

        refreshBoardDisplay();
    }

    private VBox createStatEntry(String title, String accentColor) {
        VBox entry = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: " + accentColor + "; -fx-font-size: 11px; -fx-font-weight: bold; -fx-opacity: 0.8;");
        Label valueLabel = new Label("---");
        valueLabel.setStyle("-fx-text-fill: #F0F0F0; -fx-font-size: 16px; -fx-font-weight: bold;");
        entry.getChildren().addAll(titleLabel, valueLabel);
        return entry;
    }

    private void showToastMessage(String message) {
        Label toast = new Label(message);
        toast.setStyle("-fx-background-color: rgba(21, 21, 26, 0.85); " +
                       "-fx-text-fill: white; " +
                       "-fx-padding: 15px 30px; " +
                       "-fx-background-radius: 25px; " +
                       "-fx-font-weight: bold; " +
                       "-fx-font-size: 14px; " +
                       "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);");
        
        StackPane.setAlignment(toast, Pos.TOP_CENTER);
        StackPane.setMargin(toast, new Insets(50, 0, 0, 0));
        
        overlayContainer.getChildren().add(toast);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toast);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        PauseTransition stayVisible = new PauseTransition(Duration.millis(1500));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(800), toast);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        SequentialTransition sequence = new SequentialTransition(fadeIn, stayVisible, fadeOut);
        sequence.setOnFinished(e -> overlayContainer.getChildren().remove(toast));
        sequence.play();
    }

    private void handleTurnExecution() {
        // 1. Structural sequence wrapped in try-catch
        rollDiceButton.setDisable(true);

        Monster mover = gameSession.getCurrent();
        Monster opponent = (mover == gameSession.getPlayer()) ? gameSession.getOpponent() : gameSession.getPlayer();
        
        int startPos = mover.getPosition();
        int startEnergy = mover.getEnergy();
        Role startRole = mover.getRole();
        boolean startShielded = mover.isShielded();

        try {
            // Execute backend turn logic
            gameSession.playTurn();
            
            // Evaluate events and refresh UI
            evaluateTurnEvents(mover, opponent, startPos, startEnergy, startRole, startShielded);
            refreshBoardDisplay();
            
            // Check for win condition
            checkWinCondition();

        } 
        catch (InvalidMoveException ex) {
            // Use dialog for more severe movement errors
            sceneManager.showCustomDialog("Turn Warning", ex.getMessage());
        } catch (Exception e) {
            sceneManager.showCustomDialog("System Error", "An unexpected error occurred: " + e.getMessage());
        } finally {
            // 4. Recovery checkpoint: Ensure the button is re-enabled if match is still active
            if (gameSession.getWinner() == null) {
                rollDiceButton.setDisable(false);
            }
        }
    }

    private void evaluateTurnEvents(Monster mover, Monster opponent, int startPos, int startEnergy, Role startRole, boolean startShielded) {
        int endPos = mover.getPosition();
        int endEnergy = mover.getEnergy();
        Role endRole = mover.getRole();
        String moverName = mover.getName();

        if (startRole != endRole) {
            sceneManager.showCustomDialog("CONFUSION!", moverName + " roles have been swapped! Now a " + endRole);
        }

        if (endEnergy != startEnergy) {
            int diff = endEnergy - startEnergy;
            showToastMessage("Energy Changed: " + (diff > 0 ? "+" : "") + diff + " Units");
        } else if (startShielded && !mover.isShielded() && startPos != endPos) {
            showToastMessage("SHIELD BLOCK! Damage Neutralized");
        }

        if (endPos == 0 && startPos != 0) {
            sceneManager.showCustomDialog("START OVER!", moverName + " was sent back to the beginning!");
        }
    }

    private void checkWinCondition() {
        Monster winner = gameSession.getWinner();
        if (winner != null) {
            rollDiceButton.setDisable(true);
            sceneManager.showCustomDialog("Victory!", winner.getName() + " has won the game according to backend rules!");
            sceneManager.showMainMenu();
        }
    }

    private void refreshBoardDisplay() {
        gridPane.getChildren().clear();

        int playerPosition = gameSession.getPlayer().getPosition();
        int opponentPosition = gameSession.getOpponent().getPosition();

        Board board = gameSession.getBoard();
        Cell[][] boardCells = board.getBoardCells();

        for (int index = 0; index < 100; index++) {
            int trackRow = index / 10;
            int trackCol = index % 10;
            if (trackRow % 2 == 1) trackCol = 9 - trackCol;

            int gridRow = 9 - trackRow;
            int gridCol = trackCol;

            Cell cell = boardCells[trackRow][trackCol];
            String cellType = getCellTypeTag(cell);

            Label indexLabel = new Label(String.valueOf(index + 1));
            indexLabel.getStyleClass().add("tile-number");

            Label typeLabel = new Label(cellType);

            HBox tokensBox = new HBox(4);
            tokensBox.setAlignment(Pos.CENTER);

            if (index == playerPosition) {
                Label playerToken = new Label("P");
                playerToken.getStyleClass().add("token-player");
                tokensBox.getChildren().add(playerToken);
            }

            if (index == opponentPosition) {
                Label opponentToken = new Label("O");
                opponentToken.getStyleClass().add("token-opponent");
                tokensBox.getChildren().add(opponentToken);
            }

            VBox tileContent = new VBox(2);
            tileContent.setAlignment(Pos.CENTER);
            tileContent.getChildren().addAll(indexLabel, typeLabel, tokensBox);

            StackPane tileBlock = new StackPane();
            tileBlock.setPrefSize(70, 55);
            tileBlock.getStyleClass().add(index % 2 == 0 ? "board-tile" : "board-tile-alt");
            
            if (cell instanceof TransportCell) tileBlock.getStyleClass().add("tile-transport");
            else if (cell instanceof DoorCell) tileBlock.getStyleClass().add("tile-door");

            tileBlock.getChildren().add(tileContent);
            gridPane.add(tileBlock, gridCol, gridRow);
        }

        Monster current = gameSession.getCurrent();
        Monster player = gameSession.getPlayer();
        Monster opponent = gameSession.getOpponent();

        currentTurnValue.setText(current.getName() + " (" + current.getRole() + ")");
        playerEnergyValue.setText(player.getEnergy() + " Units");
        opponentEnergyValue.setText(opponent.getEnergy() + " Units");
    }

    private String getCellTypeTag(Cell cell) {
        if (cell instanceof MonsterCell) return "Monster";
        if (cell instanceof CardCell) return "Card";
        if (cell instanceof ConveyorBelt) return "Transport";
        if (cell instanceof ContaminationSock) return "Sock";
        if (cell instanceof DoorCell) return "Door";
        return "Standard";
    }
}