package game.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class MainMenuPane extends StackPane {

    public MainMenuPane(SceneManager sceneManager) {
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setAlignment(Pos.CENTER);

        // ── Decorative ambient circles (background layer) ──
        Pane decorLayer = new Pane();
        decorLayer.setMouseTransparent(true);

        Circle c1 = new Circle(290, Color.color(0, 0.9, 0.78, 0.038));
        c1.setStroke(Color.color(0, 0.9, 0.78, 0.07));
        c1.setStrokeWidth(1.5);
        c1.setCenterX(110);
        c1.setCenterY(120);

        Circle c2 = new Circle(230, Color.color(0.42, 0.16, 0.85, 0.042));
        c2.setStroke(Color.color(0.42, 0.16, 0.85, 0.08));
        c2.setStrokeWidth(1.5);
        c2.setCenterX(1160);
        c2.setCenterY(670);

        Circle c3 = new Circle(160, Color.color(0.22, 0.74, 0.38, 0.038));
        c3.setStroke(Color.color(0.22, 0.74, 0.38, 0.07));
        c3.setStrokeWidth(1);
        c3.setCenterX(1110);
        c3.setCenterY(110);

        Circle c4 = new Circle(110, Color.color(1, 0.84, 0, 0.038));
        c4.setStroke(Color.color(1, 0.84, 0, 0.06));
        c4.setStrokeWidth(1);
        c4.setCenterX(190);
        c4.setCenterY(690);

        Circle c5 = new Circle(80, Color.color(0.85, 0.25, 0.25, 0.03));
        c5.setCenterX(640);
        c5.setCenterY(50);

        decorLayer.getChildren().addAll(c1, c2, c3, c4, c5);

        // Animate circles with subtle float
        floatNode(c1, 9000, 18);
        floatNode(c2, 11000, -14);
        floatNode(c3, 7500, 12);
        floatNode(c4, 8500, -10);

        // ── Icon row ──
        HBox iconRow = new HBox(18);
        iconRow.setAlignment(Pos.CENTER);
        iconRow.getChildren().addAll(
            makeIcon("★", "#CCA800", 20),
            makeIcon("◆", "#00C9B0", 24),
            makeIcon("★", "#7A35AA", 20)
        );

        // ── Title ──
        Label titleLabel = new Label("DooR DasH");
        titleLabel.getStyleClass().add("game-title");

        // ── Subtitle ──
        Label subtitleLabel = new Label("Scare vs Laugh Touchdown");
        subtitleLabel.getStyleClass().add("game-subtitle");

        // ── Divider ──
        Region divider = new Region();
        divider.setPrefHeight(2);
        divider.setMaxWidth(200);
        divider.setStyle(
            "-fx-background-color: linear-gradient(to right, transparent, #00C9B0, transparent);"
        );

        // ── Tagline ──
        Label tagline = new Label("Step onto the Factory Floor. Only one Monster wins.");
        tagline.setStyle("-fx-font-size: 13px; -fx-text-fill: #555588; -fx-font-style: italic;");
        tagline.setWrapText(true);
        tagline.setAlignment(Pos.CENTER);
        tagline.setMaxWidth(380);

        // ── Monster info chips ──
        HBox chipRow = new HBox(12);
        chipRow.setAlignment(Pos.CENTER);
        chipRow.getChildren().addAll(
            makeChip("SCARER", "#E05858"),
            makeChip("VS", "#888888"),
            makeChip("LAUGHER", "#40C870")
        );

        // ── Main action button ──
        Button playButton = new Button("ENTER FACTORY FLOOR");
        playButton.getStyleClass().add("modern-button");
        playButton.setMaxWidth(300);
        playButton.setMinHeight(50);
        playButton.setStyle(
            "-fx-font-size: 15px; -fx-font-weight: bold;" +
            "-fx-background-color: linear-gradient(to bottom, #00C9B0, #009E8C);" +
            "-fx-text-fill: #040412; -fx-background-radius: 12px; -fx-border-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,201,176,0.6), 22, 0, 0, 0);"
        );
        playButton.setOnAction(e -> sceneManager.showCharacterSelection());

        // ── How to play button ──
        Button howToBtn = new Button("HOW TO PLAY");
        howToBtn.getStyleClass().add("outline-button");
        howToBtn.setOnAction(e -> showHowToPlay(sceneManager));

        // ── Center glass panel ──
        VBox centerPanel = new VBox(22);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(52, 80, 52, 80));
        centerPanel.setMaxWidth(580);
        centerPanel.getStyleClass().add("glass-panel");
        centerPanel.getChildren().addAll(
            iconRow, titleLabel, subtitleLabel, divider, tagline,
            chipRow, playButton, howToBtn
        );

        // ── Entry animation ──
        centerPanel.setOpacity(0);
        centerPanel.setTranslateY(36);

        FadeTransition fade = new FadeTransition(Duration.millis(700), centerPanel);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(700), centerPanel);
        slide.setFromY(36);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition entrance = new ParallelTransition(fade, slide);
        entrance.setDelay(Duration.millis(80));
        entrance.play();

        // ── Title pulse ──
        ScaleTransition pulse = new ScaleTransition(Duration.millis(2600), titleLabel);
        pulse.setFromX(1.0);
        pulse.setToX(1.028);
        pulse.setFromY(1.0);
        pulse.setToY(1.028);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setInterpolator(Interpolator.EASE_BOTH);
        pulse.setDelay(Duration.millis(800));
        pulse.play();

        getChildren().addAll(decorLayer, centerPanel);
    }

    private Label makeIcon(String text, String color, double size) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: " + size + "px; -fx-text-fill: " + color + "; -fx-opacity: 0.55;");
        return lbl;
    }

    private Label makeChip(String text, String color) {
        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + color + ";" +
            "-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 8px;" +
            "-fx-border-radius: 8px; -fx-border-color: " + color + "44;" +
            "-fx-border-width: 1px; -fx-padding: 3px 10px; -fx-opacity: 0.85;"
        );
        return lbl;
    }

    private void floatNode(javafx.scene.Node node, double durationMs, double distance) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(durationMs), node);
        tt.setFromY(0);
        tt.setToY(distance);
        tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    private void showHowToPlay(SceneManager sceneManager) {
        sceneManager.showCustomDialog("How to Play",
            "OBJECTIVE: Reach Cell 99 with at least 1000 Energy to win!\n\n" +
            "On each turn, roll the dice to move across the Factory Floor.\n\n" +
            "SPECIAL CELLS:\n" +
            "  GREEN  (M) - Monster Cell: Battle the stationed monster!\n" +
            "  GOLD   (C) - Card Cell: Draw a special action card!\n" +
            "  BLUE   (>) - Conveyor Belt: Fast-track transport!\n" +
            "  RED    (!) - Contamination Sock: Lose energy!\n" +
            "  PURPLE (D) - Door Cell: Shortcut or penalty!\n\n" +
            "POWER-UP: Use 500 Energy to activate your monster's ability.\n\n" +
            "ROLES: SCARER = scares monsters. LAUGHER = spreads laughter."
        );
    }
}
