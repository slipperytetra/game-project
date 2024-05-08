package level;

import main.GameEngine;

public class LevelManager {

    GameEngine engine;

    public Level DEMO;
    public Level DEMO_2;

    public LevelManager(GameEngine engine) {
        this.engine = engine;
        loadLevels();
    }

    public void loadLevels() {
        //Format: *NAME* = new level.Level(id, spawn_location, key_location)
        DEMO = new Level(this, 0, "resources/levels/level_demo.txt");
        DEMO_2 = new Level(this, 1, "resources/levels/level_demo_2.txt");
    }

    public GameEngine getEngine() {
        return engine;
    }
}
