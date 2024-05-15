package level;

import main.Game;
import main.GameEngine;
import main.Location;

import java.awt.*;
import java.util.HashMap;

public class LevelManager {

    Game engine;

    HashMap<String, Level> levels;

    public Level DEMO;
    public Level DEMO_2;

    public LevelManager(Game engine) {
        this.engine = engine;
        this.levels = new HashMap<>();
        loadLevels();
    }

    public void loadLevels() {
        //Format: *NAME* = new level.Level(id, spawn_location, key_location)
        DEMO = new Level(this, 0, "resources/levels/level_demo.txt");
        DEMO_2 = new Level(this, 1, "resources/levels/level_demo_2.txt");

        levels.put("level_demo", DEMO);
        levels.put("level_demo_2", DEMO_2);

        DEMO.addTextMessage(new TextMessage(new Location(50, 100), "Welcome to our game!", 50, false, Color.black));
        DEMO.addTextMessage(new TextMessage(new Location(50, 200), "Press 'D' to move right and 'A' to move left.", 20, false, Color.black));
        DEMO.addTextMessage(new TextMessage(new Location(50, 330), "Hold 'Q' to attack with your sword.", 20, false, Color.black));
        DEMO.addTextMessage(new TextMessage(new Location(50, 360), "Press 'Space' to jump!", 20, false, Color.black));
        DEMO.addTextMessage(new TextMessage(new Location(50, 400), "Grab key to unlock door to proceed to next level!", 20, false, Color.black));
        DEMO.addTextMessage(new TextMessage(new Location(530, 670), "Press 'E' on door to enter!", 20, false, Color.black));
    }

    public Game getEngine() {
        return engine;
    }

    public HashMap<String, Level> getLevels() {
        return levels;
    }
}
