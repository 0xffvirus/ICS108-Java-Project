package com.example.project;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StarApp extends Application {

    private Circle ball;
    private int notSuccessfulCount = 0;
    private int successfulCount = 0;
    private int PlayerHealth = 100;
    private double totalSeconds = 0.0;
    private List<Star> stars;
    private Canvas canvas;
    private GraphicsContext gc;
    private Text scoreText;
    private Text reactionText;
    private Text healthText;
    private Stage primaryStage;
    private static final double INITIAL_STAR_SIZE = 20;
    private static final double INNER_RADIUS_RATIO = 0.5;
    private static final double UPDATE_INTERVAL = 0.016;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showMainMenu();
    }

    // Ahmad Al-Abbas
    // Main menu class
    private void showMainMenu() {
        // Create a VBox for vertical layout
        VBox menuBox = new VBox(20); // 20 pixels spacing between elements
        menuBox.setAlignment(Pos.CENTER);

        // Create title
        Text titleText = new Text("Marhabambo Star");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleText.setFill(Color.WHITE);
        titleText.setStroke(Color.BLACK);
        titleText.setStrokeWidth(2);

        // Create buttons with custom styling
        Button startButton = createStyledButton("Start Game");
        Button exitButton = createStyledButton("Exit");

        // Add button actions
        startButton.setOnAction(e -> startGame());
        exitButton.setOnAction(e -> Platform.exit());

        // Add elements to VBox
        menuBox.getChildren().addAll(titleText, startButton, exitButton);

        // Create background with gradient
        Rectangle background = new Rectangle(400, 400);

        background.setFill(Color.DARKBLUE);

        // Create root pane and add background and menu
        Pane root = new Pane();
        root.getChildren().addAll(background, menuBox);

        // Position the menu in the center
        menuBox.layoutXProperty().bind(root.widthProperty().subtract(menuBox.widthProperty()).divide(2));
        menuBox.layoutYProperty().bind(root.heightProperty().subtract(menuBox.heightProperty()).divide(2));

        // Create and show the scene
        Scene menuScene = new Scene(root, 400, 400);
        primaryStage.setScene(menuScene);
        primaryStage.setTitle("Star Color Match");
        primaryStage.show();
    }

    // Ahmad Al-Abbas
    private void showGameOver() {
        // Create a VBox for vertical layout
        VBox menuBox = new VBox(20); // 20 pixels spacing between elements
        menuBox.setAlignment(Pos.CENTER);

        // Create title
        Text titleText = new Text("Game Over");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleText.setFill(Color.WHITE);
        titleText.setStroke(Color.BLACK);
        titleText.setStrokeWidth(2);

        // Create buttons with custom styling
        Button exitButton = createStyledButton("Exit");

        // Add button actions
        exitButton.setOnAction(e -> Platform.exit());

        // Add elements to VBox
        menuBox.getChildren().addAll(titleText, exitButton);

        // Create background with gradient
        Rectangle background = new Rectangle(400, 400);

        background.setFill(Color.DARKBLUE);

        // Create root pane and add background and menu
        Pane root = new Pane();
        root.getChildren().addAll(background, menuBox);

        // Position the menu in the center
        menuBox.layoutXProperty().bind(root.widthProperty().subtract(menuBox.widthProperty()).divide(2));
        menuBox.layoutYProperty().bind(root.heightProperty().subtract(menuBox.heightProperty()).divide(2));

        // Create and show the scene
        Scene overScene = new Scene(root, 400, 400);
        primaryStage.setScene(overScene);
        primaryStage.setTitle("Star Color Match");
        primaryStage.show();
    }

    // Create Button Style for the main menu
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        return button;
    }

    // Bahaa Najjar
    private void startGame() {
        // Reset game statistics
        notSuccessfulCount = 0;
        successfulCount = 0;
        totalSeconds = 0.0;

        double canvasWidth = 800;
        double canvasHeight = 800;

        // Creating canvas and add a new star
        canvas = new Canvas(canvasWidth, canvasHeight);
        gc = canvas.getGraphicsContext2D();
        stars = new ArrayList<>();
        addNewStar();

        // Player (Ball)
        ball = new Circle(200, 50, 15);
        ball.setFill(Color.BLUE);

        // Create score Text
        scoreText = new Text(10, 30, "Score: 0");
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        scoreText.setFill(Color.WHITE);
        scoreText.setStroke(Color.BLACK);
        scoreText.setStrokeWidth(1);

        // Create reaction time Text
        reactionText = new Text(10, 55, "Avg. Reaction: 0.00s");
        reactionText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        reactionText.setFill(Color.WHITE);
        reactionText.setStroke(Color.BLACK);
        reactionText.setStrokeWidth(1);

        // Health Text
        healthText = new Text(10, 80, "Health: 3");
        healthText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        healthText.setFill(Color.WHITE);
        healthText.setStroke(Color.BLACK);
        healthText.setStrokeWidth(1);

        // Timer for total seconds
        Timeline timerTimeline = new Timeline(
                new KeyFrame(Duration.seconds(UPDATE_INTERVAL), event -> {
                    totalSeconds += UPDATE_INTERVAL;
                    updateReactionTime();
                }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();

        // Timer for adding new stars
        Timeline newStarTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1.25), event -> addNewStar()));
        newStarTimeline.setCycleCount(Timeline.INDEFINITE);
        newStarTimeline.play();

        // Timer for star growth
        Timeline growthTimeline = new Timeline(
                new KeyFrame(Duration.seconds(UPDATE_INTERVAL), event -> updateStars()));
        growthTimeline.setCycleCount(Timeline.INDEFINITE);
        growthTimeline.play();

        Pane root = new Pane();
        setupBallDragEvents(root);
        root.getChildren().addAll(canvas, ball, scoreText, reactionText, healthText);

        // Add a dark semi-transparent background to both texts
        Rectangle textBg = new Rectangle(
                scoreText.getX() - 5,
                scoreText.getY() - scoreText.getFont().getSize(),
                170,
                80);
        textBg.setFill(Color.rgb(0, 0, 0, 0.5));
        root.getChildren().add(0, textBg);

        Scene gameScene = new Scene(root, canvasWidth, canvasHeight);
        primaryStage.setScene(gameScene);

    }

    // Bahaa Najjar
    private void updateScore() {
        int totalAttempts = successfulCount + notSuccessfulCount;
        double Score = ((double) successfulCount / (double) totalAttempts + 0.0) * 100.0;
        scoreText.setText(String.format("Score: %.2f %%", Score));
        updateReactionTime();
        updateHealth();
    }

    // Bahaa Najjar
    private void updateReactionTime() {
        int totalAttempts = successfulCount + notSuccessfulCount;
        if (totalAttempts > 0) {
            double avgReactionTime = totalSeconds / totalAttempts;
            reactionText.setText(String.format("Avg. Reaction: %.2fs", avgReactionTime));

        }
    }

    // Bahaa Najjar
    private void updateHealth() {
        healthText.setText(String.format("Health: %d", PlayerHealth));
        if (PlayerHealth == 0) {
            showGameOver();
            return;
        }
    }

    // Bahaa Najjar
    private void updateStars() {
        Iterator<Star> iterator = stars.iterator();
        while (iterator.hasNext()) {
            Star star = iterator.next();
            star.grow();

            if (star.touchesBorder(canvas.getWidth(), canvas.getHeight())) {
                iterator.remove();
                notSuccessfulCount += 1;

                updateScore();

                ball.setFill(Star.getRandomColor());
            }
        }
        redrawCanvas();
    }

    // Ahmad Al-Abbas
    // Adds a new star to the game
    private void addNewStar() {
        double centerX = canvas.getWidth() / 2; // Center of the canvas
        double centerY = canvas.getHeight() / 2; // Center of the canvas
        Star newStar = new Star(centerX, centerY, INITIAL_STAR_SIZE, INITIAL_STAR_SIZE * INNER_RADIUS_RATIO);
        stars.add(newStar);
        redrawCanvas();
    }

    // Ahmad Al-Abbas
    // Redraws the canvas with the updated stars
    private void redrawCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Star star : stars) {
            star.draw(gc);
        }
    }

    // Ahmad Al-Abbas
    // Controls the movement of the ball
    private void setupBallDragEvents(Pane root) {
        root.setOnMouseMoved(event -> {
            ball.setCenterX(event.getX());
            ball.setCenterY(event.getY());
            checkCollision();
        });
    }

    // Ahmad Al-Abbas
    private void checkCollision() {
        Bounds ballBounds = ball.getBoundsInParent(); // Get the bounds of the ball
        Color ballColor = (Color) ball.getFill();

        Iterator<Star> iterator = stars.iterator(); // Iterate over the stars

        while (iterator.hasNext()) {
            Star star = iterator.next(); // Get the next star
            if (star.checkCollisionWithBall(ballBounds, ballColor) == 1) { // if the ball collides with a star and the
                                                                           // right color
                iterator.remove();
                successfulCount++;
                updateScore();
                ball.setFill(Star.getRandomColor());
                break;
            } else if (star.checkCollisionWithBall(ballBounds, ballColor) == 0) { // the same thing but wrong color
                iterator.remove();
                notSuccessfulCount++;
                PlayerHealth -= 1;
                updateScore();
                ball.setFill(Star.getRandomColor());
                break;
            }
        }

        redrawCanvas();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
