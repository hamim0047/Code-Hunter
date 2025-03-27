package com.example.tilegamefxglproject;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpriteLoader {

    // Load image from resources
    public static Image loadImage(String imagePath) {
        try {

            Image image = new Image(Objects.requireNonNull(SpriteLoader.class.getResourceAsStream(imagePath)));
            if (image.isError()) {
                System.err.println("Error loading image: " + imagePath);
            }
            return image;
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
            return null; // Return null if the image cannot be loaded
        }
    }


    // Load frames from sprite sheet
    public static Image[] loadFrames(String imagePath, int frameWidth, int frameHeight, int rows, int columns) {
        List<Image> frames = new ArrayList<>();
        try {
            Image image = new Image(imagePath);
            if (image.isError()) {
                System.err.println("Error loading image: " + imagePath);
                return new Image[0];  // Return empty array if there is an error
            }

            int sheetWidth = (int) image.getWidth();
            int sheetHeight = (int) image.getHeight();

            // Ensure the number of frames fits in the sprite sheet's width and height
            if (columns * frameWidth > sheetWidth || rows * frameHeight > sheetHeight) {
                System.err.println("Error: The number of frames exceeds the image dimensions.");
                return new Image[0];
            }

            // Extract frames from the sprite sheet
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    int x = col * frameWidth;
                    int y = row * frameHeight;

                    // Ensure we are within bounds
                    if (x + frameWidth > sheetWidth || y + frameHeight > sheetHeight) {
                        System.err.println("Error: Frame exceeds sheet dimensions.");
                        return new Image[0];
                    }

                    WritableImage frame = new WritableImage(image.getPixelReader(), x, y, frameWidth, frameHeight);
                    frames.add(frame);
                    System.out.println("Loaded frame at row " + row + ", column " + col);
                }
            }

        } catch (Exception e) {
            System.err.println("Exception while loading frames from image: " + imagePath);
            e.printStackTrace();
        }

        return frames.toArray(new Image[0]);
    }

}
