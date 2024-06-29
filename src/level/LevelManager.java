package level;

import main.Game;
import utils.Location;

import java.awt.*;
import java.util.HashMap;

public class LevelManager {

    Game engine;

    HashMap<String, Level> levels;

    // Define the level variable name.
    public Level FOREST;
    public Level FOREST_2;

    public LevelManager(Game engine) {
        this.engine = engine;
        this.levels = new HashMap<>();
        loadLevels();
    }

    public void loadLevels() {
        // Assign the level name to your level. This points to your level's file under resources/levels.
        // Format: *NAME* = new level.Level(id, spawn_location, key_location)
        FOREST = new Level(this,4,"resources/levels/level_forest.txt");
        FOREST_2 = new Level(this,4,"resources/levels/level_forest_2.txt");


        levels.put("forest", FOREST);
        levels.put("forest_2", FOREST_2);

        /*
         *  This is for adding text to the level. You set the x and y coordinates where it should show.
         *  The boolean static means if the text should show at a fixed position in the world e.g. the player
         *  can walk past it or if static is set to true, then the text will show at a constant position on the screen
         *  and follow the player as they move.
         */

        /*
        DEMO.addTextMessage(new TextMessage(new Location(10, 100), "Welcome to our game!", 50, false, Color.black));
        DEMO.addTextMessage(new TextMessage(new Location(10, 200), "Press 'D' to move right and 'A' to move left.", 20, false, Color.black));
        DEMO.addTextMessage(new TextMessage(new Location(10, 330), "Hold 'Q' to attack with your sword.", 20, false, Color.black));
        DEMO.addTextMessage(new TextMessage(new Location(10, 360), "Press 'Space' to jump!", 20, false, Color.black));
        DEMO.addTextMessage(new TextMessage(new Location(10, 380), "Grab key to unlock door to proceed to next level!", 20, false, Color.black));
        DEMO.addTextMessage(new TextMessage(new Location(570, 670), "Press 'E' on door to enter!", 20, false, Color.black));

         */
    }

    public Game getEngine() {
        return engine;
    }

    public HashMap<String, Level> getLevels() {
        return levels;
    }
}
