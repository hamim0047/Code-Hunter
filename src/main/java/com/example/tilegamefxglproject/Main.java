package com.example.tilegamefxglproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.transform.Affine;
import java.io.IOException;

public class Main extends Application {

    private static final int TILE_SIZE = 16;
    private static final int MAP_WIDTH = 40;
    private static final int MAP_HEIGHT = 30;
    static final int VIEWPORT_WIDTH = 20; // Number of tiles visible on screen
    static final int VIEWPORT_HEIGHT = 15;

    private Canvas canvas;
    private Player player;
    private TileMap tileMap;
    private Affine cameraTransform;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Pane root = new Pane();
        canvas = new Canvas(VIEWPORT_WIDTH * TILE_SIZE * 2, VIEWPORT_HEIGHT * TILE_SIZE * 2); // Adjusted for zoom
        root.getChildren().add(canvas);

        // Initialize TileMap
        tileMap = new TileMap("src/main/resources/assets/maps/map1.tmj",
                "src/main/resources/assets/maps/tileset.png");

        // Initialize player
        player = new Player(1 * TILE_SIZE, 3.5 * TILE_SIZE, tileMap); // Pass tileMap to Player

        // Initialize camera transform
        cameraTransform = new Affine();

        // Draw initial scene
        drawGame();

        // Handle player movement
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            player.handleKeyPress(event.getCode());
            drawGame();
        });

        scene.setOnKeyReleased(event -> {
            player.handleKeyRelease(event.getCode());
            drawGame();
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Tile Game Without FXGL");
        primaryStage.show();
    }

    private void drawGame() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setTransform(cameraTransform);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Adjust camera to follow player
        double playerCenterX = player.getSprite().getX() + TILE_SIZE / 2;
        double playerCenterY = player.getSprite().getY() + TILE_SIZE / 2;

        double offsetX = playerCenterX - (VIEWPORT_WIDTH * TILE_SIZE) / 2;
        double offsetY = playerCenterY - (VIEWPORT_HEIGHT * TILE_SIZE) / 2;

        // Ensure camera doesn't go out of bounds
        offsetX = Math.max(0, Math.min(offsetX, MAP_WIDTH * TILE_SIZE - VIEWPORT_WIDTH * TILE_SIZE));
        offsetY = Math.max(0, Math.min(offsetY, MAP_HEIGHT * TILE_SIZE - VIEWPORT_HEIGHT * TILE_SIZE));

        cameraTransform.setToIdentity();
        cameraTransform.appendScale(2, 2); // Zoom effect first
        cameraTransform.appendTranslation(-offsetX, -offsetY); // Then translate to keep player centered

        tileMap.drawMap(gc);
        gc.drawImage(player.getSprite().getImage(), player.getSprite().getX(), player.getSprite().getY());
    }

    public static void main(String[] args) {
        launch(args);
    }
}