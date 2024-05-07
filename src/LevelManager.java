public class LevelManager {

    GameEngine engine;

    public Level DEMO;

    public LevelManager(GameEngine engine) {
        this.engine = engine;
        loadLevels();
    }

    public void loadLevels() {
        //Format: *NAME* = new Level(id, spawn_location, key_location)
        DEMO = new Level(this, 0, "resources/levels/level_demo.txt");
    }

    public GameEngine getEngine() {
        return engine;
    }
}
