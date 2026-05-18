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
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

        // ── BACKGROUND IMAGE LAYER (With Low Opacity) ──
        ImageView backgroundView = null;
        try {
            Image backgroundImg = new Image(getClass().getResourceAsStream("image_c2df21.jpg"));
            backgroundView = new ImageView(backgroundImg);
            
            backgroundView.fitWidthProperty().bind(this.widthProperty());
            backgroundView.fitHeightProperty().bind(this.heightProperty());
            backgroundView.setPreserveRatio(false);
            backgroundView.setOpacity(0.25); 
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
        }

        // ── Decorative ambient circles ──
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

        decorLayer.getChildren().addAll(c1, c2, c3, c4);

        floatNode(c1, 9000, 18);
        floatNode(c2, 11000, -14);
        floatNode(c3, 7500, 12);
        floatNode(c4, 8500, -10);

        // ── Title ──
        Label titleLabel = new Label("DooR DasH");
        titleLabel.getStyleClass().add("game-title");
        
        titleLabel.setStyle(
            "-fx-font-family: 'Impact', 'Arial Black', sans-serif;" +
            "-fx-font-size: 80px;" + 
            "-fx-font-weight: 900;" +
            "-fx-text-transform: uppercase;" + 
            "-fx-text-fill: #0B7391;" + 
            "-fx-effect: dropshadow(one-pass-box, white, 1.5, 1.0, 1.5, 0) " +
                        "dropshadow(one-pass-box, white, 1.5, 1.0, -1.5, 0) " +
                        "dropshadow(one-pass-box, white, 1.5, 1.0, 0, 1.5) " +
                        "dropshadow(one-pass-box, white, 1.5, 1.0, 0, -1.5);"
        );

        // ── Subtitle ──
        Label subtitleLabel = new Label("Scare vs Laugh Touchdown");
        subtitleLabel.getStyleClass().add("game-subtitle");
        subtitleLabel.setStyle(
            "-fx-text-fill: #555588;" +
            "-fx-font-style: italic;" +
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(one-pass-box, white, 1, 1.0, 1, 0) " +
                        "dropshadow(one-pass-box, white, 1, 1.0, -1, 0) " +
                        "dropshadow(one-pass-box, white, 1, 1.0, 0, 1) " +
                        "dropshadow(one-pass-box, white, 1, 1.0, 0, -1);"
        );

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

        // ── Monster Rivalry Row ──
        HBox chipRow = new HBox(22); 
        chipRow.setAlignment(Pos.CENTER);
        
        VBox scarerGroup = new VBox(4); 
        scarerGroup.setAlignment(Pos.CENTER);
        
        ImageView sulleyView = null;
        try {
            Image sulleyImg = new Image(getClass().getResourceAsStream("Designbolts-Monsters-University-Monsters-James-P-Sullivan-2.256.png"));
            sulleyView = new ImageView(sulleyImg);
            sulleyView.setFitHeight(90); 
            sulleyView.setPreserveRatio(true);
            sulleyView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0, 196, 228, 0.4), 8, 0.5, 0, 0);"); 
        } catch (Exception e) {}

        Label scarerLabel = makeChip("SCARER", "#E05858"); 
        scarerLabel.setStyle(scarerLabel.getStyle() + 
            "-fx-font-size: 16px; -fx-font-weight: 900; -fx-padding: 6px 16px; " +
            "-fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 1, 0, 0, 2);");
        
        if (sulleyView != null) scarerGroup.getChildren().add(sulleyView);
        scarerGroup.getChildren().add(scarerLabel);

        Label vsLabel = new Label("VS");
        vsLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: 900; " +
            "-fx-text-fill: #FFFFFF; " + 
            "-fx-text-transform: uppercase;" +
            "-fx-effect: dropshadow(three-pass-box, #101432, 0, 0, 3, 3);"
        );
        vsLabel.setAlignment(Pos.CENTER);

        VBox laugherGroup = new VBox(4); 
        laugherGroup.setAlignment(Pos.CENTER);
        
        ImageView mikeView = null;
        try {
            Image mikeImg = new Image(getClass().getResourceAsStream("Designbolts-Monsters-University-Monsters-Character-Young-Mikes.256.png"));
            mikeView = new ImageView(mikeImg);
            mikeView.setFitHeight(90); 
            mikeView.setPreserveRatio(true);
            mikeView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(162, 219, 71, 0.4), 8, 0.5, 0, 0);"); 
        } catch (Exception e) {}

        Label laugherLabel = makeChip("LAUGHER", "#40C870"); 
        laugherLabel.setStyle(laugherLabel.getStyle() + 
            "-fx-font-size: 16px; -fx-font-weight: 900; -fx-padding: 6px 16px; " +
            "-fx-text-fill: #1C2D42; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 1, 0, 0, 1);");
        
        if (mikeView != null) laugherGroup.getChildren().add(mikeView);
        laugherGroup.getChildren().add(laugherLabel);
        
        chipRow.getChildren().addAll(scarerGroup, vsLabel, laugherGroup);
        
        ScaleTransition scarerPulse = new ScaleTransition(Duration.millis(900), scarerGroup);
        scarerPulse.setFromX(1.0); scarerPulse.setToX(1.1); scarerPulse.setFromY(1.0); scarerPulse.setToY(1.1);
        scarerPulse.setAutoReverse(true); scarerPulse.setCycleCount(Animation.INDEFINITE);
        scarerPulse.setInterpolator(Interpolator.EASE_BOTH); scarerPulse.play();

        ScaleTransition laugherPulse = new ScaleTransition(Duration.millis(900), laugherGroup);
        laugherPulse.setFromX(1.0); laugherPulse.setToX(1.1); laugherPulse.setFromY(1.0); laugherPulse.setToY(1.1);
        laugherPulse.setAutoReverse(true); laugherPulse.setCycleCount(Animation.INDEFINITE);
        laugherPulse.setInterpolator(Interpolator.EASE_BOTH); laugherPulse.setDelay(Duration.millis(450));
        laugherPulse.play();

        ScaleTransition vsPulse = new ScaleTransition(Duration.millis(600), vsLabel);
        vsPulse.setFromX(1.0); vsPulse.setToX(1.15); vsPulse.setFromY(1.0); vsPulse.setToY(1.15);
        vsPulse.setAutoReverse(true); vsPulse.setCycleCount(Animation.INDEFINITE);
        vsPulse.setInterpolator(Interpolator.EASE_BOTH); vsPulse.play();

        // ── Action Buttons ──
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

        Button howToBtn = new Button("HOW TO PLAY");
        howToBtn.getStyleClass().add("outline-button");
        howToBtn.setOnAction(e -> showHowToPlay(this)); // Target container explicitly to display internal dialog layer

        // ── Center Glass Panel (58% Translucency) ──
        VBox centerPanel = new VBox(22);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(52, 80, 52, 80));
        centerPanel.setMaxWidth(580);
        
        centerPanel.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.58);" + 
            "-fx-background-radius: 24px;" +
            "-fx-border-radius: 24px;" +
            "-fx-border-color: rgba(255, 255, 255, 0.5);" +
            "-fx-border-width: 1.5px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.18), 30, 0, 0, 10);"
        );
        
        centerPanel.getChildren().addAll(
            titleLabel, subtitleLabel, divider, tagline,
            chipRow, playButton, howToBtn
        );

        // ── Entry animation ──
        centerPanel.setOpacity(0);
        centerPanel.setTranslateY(36);

        FadeTransition fade = new FadeTransition(Duration.millis(700), centerPanel);
        fade.setFromValue(0); fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(700), centerPanel);
        slide.setFromY(36); slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition entrance = new ParallelTransition(fade, slide);
        entrance.setDelay(Duration.millis(80));
        entrance.play();

        ScaleTransition pulse = new ScaleTransition(Duration.millis(2600), titleLabel);
        pulse.setFromX(1.0); pulse.setToX(1.028); pulse.setFromY(1.0); pulse.setToY(1.028);
        pulse.setAutoReverse(true); pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setInterpolator(Interpolator.EASE_BOTH); pulse.setDelay(Duration.millis(800));
        pulse.play();

        if (backgroundView != null) {
            getChildren().add(backgroundView);
        }
        getChildren().addAll(decorLayer, centerPanel);
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
        tt.setFromY(0); tt.setToY(distance); tt.setAutoReverse(true); tt.setCycleCount(Animation.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH); tt.play();
    }

    // ── MODIFIED INTERNAL DIALOG METHOD FOR CRISP CUSTOM MATCHING THEMING ──
    private void showHowToPlay(StackPane container) {
        // Light dimmed background blocker layer
        StackPane modalBg = new StackPane();
        modalBg.setStyle("-fx-background-color: rgba(4, 4, 18, 0.45);");
        modalBg.setPrefSize(container.getWidth(), container.getHeight());

        // Custom Frosted Glass Box (58% matching layout)
        VBox dialogBox = new VBox(18);
        dialogBox.setAlignment(Pos.CENTER);
        dialogBox.setPadding(new Insets(32, 40, 32, 40));
        dialogBox.setMaxWidth(500);
        dialogBox.setMaxHeight(550);
        dialogBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.58);" +
            "-fx-background-radius: 20px;" +
            "-fx-border-radius: 20px;" +
            "-fx-border-color: rgba(255, 255, 255, 0.6);" +
            "-fx-border-width: 1.5px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 40, 0, 0, 15);"
        );

        // Title text using matching corporate steel blue and crisp white stroke boundary
        Label modalTitle = new Label("HOW TO PLAY");
        modalTitle.setStyle(
            "-fx-font-family: 'Impact', 'Arial Black', sans-serif;" +
            "-fx-font-size: 34px;" +
            "-fx-font-weight: 900;" +
            "-fx-text-fill: #0B7391;" +
            "-fx-effect: dropshadow(one-pass-box, white, 1, 1.0, 1, 0) " +
                        "dropshadow(one-pass-box, white, 1, 1.0, -1, 0) " +
                        "dropshadow(one-pass-box, white, 1, 1.0, 0, 1) " +
                        "dropshadow(one-pass-box, white, 1, 1.0, 0, -1);"
        );

        // Divider line
        Region modalDivider = new Region();
        modalDivider.setPrefHeight(2);
        modalDivider.setMaxWidth(160);
        modalDivider.setStyle("-fx-background-color: linear-gradient(to right, transparent, #00C9B0, transparent);");

        // Custom styled Text Area container
        Label bodyText = new Label(
            "OBJECTIVE:\nReach Cell 99 with at least 1,000 Energy to win!\n\n" +
            "TURNS:\nOn each turn, roll the dice to advance across the Factory Floor map grid.\n\n" +
            "SPECIAL CELLS:\n" +
            "  • GREEN (M) - Monster Cell: Battle a stationed monster rival.\n" +
            "  • GOLD (C) - Card Cell: Draw an unpredictable action modifier.\n" +
            "  • BLUE (>) - Conveyor Belt: Fast-track quick transport lane.\n" +
            "  • RED (!) - Contamination Sock: Hazardous energy loss cell.\n" +
            "  • PURPLE (D) - Door Cell: Sudden shortcut or surprise trap penalty.\n\n" +
            "CHARGED POWER-UP:\nSpend 500 Accumulated Energy anytime to deploy your unique character ability.\n\n" +
            "ROLES:\nSCARER teams deal direct shock points, while LAUGHER groups distribute tactical points."
        );
        bodyText.setWrapText(true);
        bodyText.setStyle("-fx-font-size: 13px; -fx-font-family: 'Segoe UI', Arial; -fx-text-fill: #222244; -fx-line-spacing: 4px;");

        ScrollPane scrollPane = new ScrollPane(bodyText);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(320);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-vbar-policy: as-needed; -fx-hbar-policy: never;");

        // Dismiss Action Button
        Button closeBtn = new Button("RETURN TO FACTORY");
        closeBtn.setMaxWidth(220);
        closeBtn.setMinHeight(40);
        closeBtn.setStyle(
            "-fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-background-color: linear-gradient(to bottom, #00C9B0, #009E8C);" +
            "-fx-text-fill: #040412; -fx-background-radius: 8px; -fx-border-radius: 8px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,201,176,0.3), 10, 0, 0, 0);"
        );
        closeBtn.setOnAction(e -> container.getChildren().remove(modalBg));

        // Connect nodes
        dialogBox.getChildren().addAll(modalTitle, modalDivider, scrollPane, closeBtn);
        modalBg.getChildren().add(dialogBox);
        container.getChildren().add(modalBg);

        // Quick overlay transitions
        modalBg.setOpacity(0);
        dialogBox.setScaleX(0.85);
        dialogBox.setScaleY(0.85);

        FadeTransition bgFade = new FadeTransition(Duration.millis(250), modalBg);
        bgFade.setToValue(1);

        ScaleTransition boxScale = new ScaleTransition(Duration.millis(250), dialogBox);
        boxScale.setToX(1);
        boxScale.setToY(1);
        boxScale.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(bgFade, boxScale).play();
    }
}