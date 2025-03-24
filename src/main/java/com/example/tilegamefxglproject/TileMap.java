package com.example.tilegamefxglproject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.tilegamefxglproject.Main.VIEWPORT_HEIGHT;
import static com.example.tilegamefxglproject.Main.VIEWPORT_WIDTH;

public class TileMap {
    private static final int TILE_SIZE = 32;
    private static final int MAP_WIDTH = 40;
    private static final int MAP_HEIGHT = 30;
    private static final int SCALE = 3; // Increase from 2 to 3 for a bigger screen

    private int[][][] tileLayers;
    private List<JsonNode> objectLayers = new ArrayList<>();
    private Map<Integer, Image> tileImages = new HashMap<>();
    private Canvas canvas;

    public TileMap(String mapPath, String tilesetPath) throws IOException {
        canvas = new Canvas(VIEWPORT_WIDTH * TILE_SIZE * SCALE, VIEWPORT_HEIGHT * TILE_SIZE * SCALE);

        loadMapData(mapPath);
        loadTileset(tilesetPath);
    }

    private void loadMapData(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(filePath));

        JsonNode layers = rootNode.get("layers");
        int tileLayerCount = 0;
        for (JsonNode layer : layers) {
            if (layer.get("type").asText().equals("tilelayer")) {
                tileLayerCount++;
            }
        }

        tileLayers = new int[tileLayerCount][MAP_HEIGHT][MAP_WIDTH];
        int tileIndex = 0;
        for (JsonNode layer : layers) {
            String type = layer.get("type").asText();
            if (type.equals("tilelayer")) {
                JsonNode layerData = layer.get("data");
                for (int i = 0; i < layerData.size(); i++) {
                    int row = i / MAP_WIDTH;
                    int col = i % MAP_WIDTH;
                    tileLayers[tileIndex][row][col] = layerData.get(i).asInt();
                }
                tileIndex++;
            } else if (type.equals("objectgroup")) {
                objectLayers.add(layer.get("objects"));
            }
        }
    }

    private void loadTileset(String tilesetPath) {
        Image tileset = new Image(new File(tilesetPath).toURI().toString());
        int tilesetColumns = (int) tileset.getWidth() / TILE_SIZE;
        int tilesetRows = (int) tileset.getHeight() / TILE_SIZE;
        PixelReader pixelReader = tileset.getPixelReader();

        int tileID = 1;
        for (int row = 0; row < tilesetRows; row++) {
            for (int col = 0; col < tilesetColumns; col++) {
                WritableImage tile = new WritableImage(pixelReader, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                tileImages.put(tileID++, tile);
            }
        }
    }

    public void drawMap(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int layer = 0; layer < tileLayers.length; layer++) {
            for (int row = 0; row < MAP_HEIGHT; row++) {
                for (int col = 0; col < MAP_WIDTH; col++) {
                    int tileID = tileLayers[layer][row][col];
                    if (tileID != 0 && tileImages.containsKey(tileID)) {
                        gc.drawImage(tileImages.get(tileID), col * TILE_SIZE, row * TILE_SIZE);
                    }
                }
            }
        }
    }

    public boolean isWalkable(double x, double y) {
        double hitboxPadding = 3; // Reduce hitbox size slightly for better movement
        double playerWidth = TILE_SIZE - hitboxPadding * 2;
        double playerHeight = TILE_SIZE - hitboxPadding * 2;

        if (x < 0 || x + playerWidth >= MAP_WIDTH * TILE_SIZE ||
                y < 0 || y + playerHeight >= MAP_HEIGHT * TILE_SIZE) {
            return false; // Out of bounds
        }

        for (JsonNode objectLayer : objectLayers) {
            for (JsonNode object : objectLayer) {
                double objectX = object.get("x").asDouble();
                double objectY = object.get("y").asDouble();
                double objectWidth = object.get("width").asDouble();
                double objectHeight = object.get("height").asDouble();

                if (x + hitboxPadding < objectX + objectWidth &&
                        x + playerWidth + hitboxPadding > objectX &&
                        y + hitboxPadding < objectY + objectHeight &&
                        y + playerHeight + hitboxPadding > objectY) {
                    return false; // Collision
                }
            }
        }

        return true; // No collision
    }


    public Canvas getCanvas() {
        return canvas;
    }
}
