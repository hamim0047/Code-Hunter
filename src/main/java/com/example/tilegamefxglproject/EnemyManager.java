package com.example.tilegamefxglproject;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager {
    private List<Enemy> enemies = new ArrayList<>();

    public EnemyManager(TileMap map) {
        loadEnemiesFromMap(map);
    }

    private void loadEnemiesFromMap(TileMap tileMap) {
        List<JsonNode> objectLayers = tileMap.getObjectLayers();

        for (JsonNode layer : objectLayers) {
            for (JsonNode obj : layer) {
                if (obj.has("name") && obj.get("name").asText().equalsIgnoreCase("enemy")) {
                    double x = obj.get("x").asDouble();
                    double y = obj.get("y").asDouble();

                    // Read custom properties
                    JsonNode properties = obj.get("properties");
                    String sprite = "";
                    String deadSprite = "";

                    if (properties != null) {
                        for (JsonNode prop : properties) {
                            String name = prop.get("name").asText();
                            String value = prop.get("value").asText();

                            if (name.equals("sprite")) sprite = value;
                            if (name.equals("deadSprite")) deadSprite = value;
                        }
                    }

                    // Default fallback for sprite paths
                    sprite = getClass().getResource("/assets/enemy/enemy_walk.png").toExternalForm();
                    deadSprite = getClass().getResource("/assets/enemy/enemy_walk.png").toExternalForm();


                    // Adding the enemy to the list
                    enemies.add(new Enemy(sprite, deadSprite, x, y));
                }
            }
        }
    }

    public void update() {
        for (Enemy e : enemies) {
            e.update();
        }
    }

    public void render(GraphicsContext gc) {
        for (Enemy e : enemies) {
            e.render(gc); // Call the render method of each enemy
        }
    }
}
