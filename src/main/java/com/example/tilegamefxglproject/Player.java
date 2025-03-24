package com.example.tilegamefxglproject;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;

public class Player {
    private static final int SPRITE_SIZE = 16;   // Frame size (each frame is 48x48)
    private static final int FRAME_COUNT = 3;   // 3 frames per direction
    private static final double SPEED = 2.5;
    private static final int FRAME_DURATION = 200;// Movement speed

    private ImageView sprite;
    private Map<String, Image[]> animations;
    private String currentAnimation = "down";
    private int currentFrame = 0;
    private Timeline animationTimeline;
    private TileMap tileMap; // Reference to TileMap for collision detection

    public Player(double x, double y, TileMap tileMap) {
        this.tileMap = tileMap; // Store the reference to the TileMap
        sprite = new ImageView();
        sprite.setX(x);
        sprite.setY(y);
        sprite.setFitWidth(SPRITE_SIZE);
        sprite.setFitHeight(SPRITE_SIZE);

        loadAnimations();
        updateSprite();

        // Set up animation loop (switch frames every 150ms)
        animationTimeline = new Timeline(new KeyFrame(Duration.millis(150), e -> nextFrame()));
        animationTimeline.setCycleCount(Animation.INDEFINITE);
    }

    private void loadAnimations() {
        animations = new HashMap<>();
        animations.put("down", splitSpritesheet("/assets/player_sprites/down.png"));
        animations.put("up", splitSpritesheet("/assets/player_sprites/top.png"));
        animations.put("left", splitSpritesheet("/assets/player_sprites/left.png"));
        animations.put("right", splitSpritesheet("/assets/player_sprites/right.png"));
    }

    private Image[] splitSpritesheet(String path) {
        Image spritesheet = new Image(getClass().getResourceAsStream(path));

        // Extract row index from file name
        int row = 0;  // Default row (down)
        if (path.contains("up")) row = 1;
        else if (path.contains("left")) row = 2;
        else if (path.contains("right")) row = 3;

        Image[] frames = new Image[3]; // 3 frames per direction
        for (int i = 0; i < 3; i++) {
            frames[i] = new WritableImage(spritesheet.getPixelReader(),
                    i * SPRITE_SIZE,    // Column
                    row * SPRITE_SIZE,  // Row
                    SPRITE_SIZE, SPRITE_SIZE);
        }
        return frames;
    }

    private void updateSprite() {
        if (animations.containsKey(currentAnimation)) {
            sprite.setImage(animations.get(currentAnimation)[currentFrame]);
        }
    }

    private void nextFrame() {
        currentFrame = (currentFrame + 1) % FRAME_COUNT;
        updateSprite();
    }

    public void handleKeyPress(KeyCode key) {
        switch (key) {
            case W:
                move(0, -SPEED, "up");
                break;
            case S:
                move(0, SPEED, "down");
                break;
            case A:
                move(-SPEED, 0, "left");
                break;
            case D:
                move(SPEED, 0, "right");
                break;
        }
        animationTimeline.play(); // Start animation when moving
    }

    public void handleKeyRelease(KeyCode key) {
        animationTimeline.stop(); // Stop animation
        currentFrame = 0;         // Reset to first frame
        updateSprite();
    }

    private void move(double dx, double dy, String direction) {
        double newX = sprite.getX() + dx;
        double newY = sprite.getY() + dy;

        // Check for collisions before moving
        if (tileMap.isWalkable(newX, newY)) {
            sprite.setX(newX);
            sprite.setY(newY);
            if (!currentAnimation.equals(direction)) {
                currentAnimation = direction;
                currentFrame = 0;
            }
            updateSprite();
        }
    }

    // Getter for the sprite animation frames
    public Image[] getSpriteFrames(String direction) {
        return animations.getOrDefault(direction, new Image[0]);
    }

    // Getter for sprite
    public ImageView getSprite() {
        return sprite;
    }
}

