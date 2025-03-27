package com.example.tilegamefxglproject;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Enemy {
    private double x, y;
    private Image[] walkFrames;
    private Image[] deathFrames;

    private boolean isDead = false;
    private int frameIndex = 0;
    private int frameCounter = 0;

    private static final int SPRITE_SIZE = 32;
    private static final int WALK_FRAMES = 6;   // 3 rows × 3 columns
    private static final int DEATH_FRAMES = 6;  // 3 rows × 2 columns

    public Enemy(String walkPath, String deathPath, double x, double y) {
        this.x = x;
        this.y = y;

        this.walkFrames = SpriteLoader.loadFrames(walkPath, SPRITE_SIZE, SPRITE_SIZE, 3, 2);
        this.deathFrames = SpriteLoader.loadFrames(deathPath, SPRITE_SIZE, SPRITE_SIZE, 3, 2);
    }

    public void update() {
        frameCounter++;
        if (frameCounter >= 10) {
            frameCounter = 0;

            // Use different frame logic for walk and death
            if (!isDead) {
                frameIndex = (frameIndex + 1) % walkFrames.length;
            } else if (frameIndex < deathFrames.length - 1) {
                frameIndex++; // Stop at the last death frame
            }
        }
    }

    public void render(GraphicsContext gc) {
        if (isDead) {
            gc.drawImage(deathFrames[frameIndex], x, y);
        } else {
            gc.drawImage(walkFrames[frameIndex], x, y);
        }
    }

    public void die() {
        isDead = true;
        frameIndex = 0;
        frameCounter = 0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}

