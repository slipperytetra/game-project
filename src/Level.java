//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Level {
    LevelManager manager;

    int id;
    String name;
    int size;
    ArrayList<String> lines;

    private final String levelDoc;
    private final double GRAVITY = 0.98;
    Image backgroundImage;
    Location spawnPoint;
    Location keyLoc;

    public Level(LevelManager manager, int id, String levelDoc) {
        this.manager = manager;
        this.id = id;
        this.levelDoc = levelDoc;
        this.lines = new ArrayList<>();
        init();
        //this.spawnPoint = spawnPoint;
        //this.keyLoc = keyLoc;
    }

    public void init() {
        System.out.println("init!");
        File file = new File(levelDoc);
        try {
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                lines.add(fileReader.nextLine());
            }

            for (String s : lines) {
                System.out.println(s);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't locate file!");
            return;
        }

        name = lines.get(0).substring("name: ".length());
        backgroundImage = manager.getEngine().loadImage(lines.get(1).substring("background: ".length()));
        size = Integer.parseInt(lines.get(2).substring("level_size: ".length()));
    }

    public Location getSpawnPoint() {
        return this.spawnPoint;
    }

    public Location getKeyLocation() {
        return this.keyLoc;
    }

    public double getGravity() {
        return 0.98;
    }
}
