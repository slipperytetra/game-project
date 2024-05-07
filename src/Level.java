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
    ArrayList<Block> blocks;

    private final String levelDoc;
    private final double GRAVITY = 0.98;
    Image backgroundImage;
    Location playerLoc;
    Location keyLoc;
    Location doorLoc;

    public Level(LevelManager manager, int id, String levelDoc) {
        this.manager = manager;
        this.id = id;
        this.levelDoc = levelDoc;
        this.lines = new ArrayList<>();
        this.blocks = new ArrayList<>();
        init();
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


        int scale = Game.WIDTH / getSize();
        int relY = 0;
        for (int y = 4; y < lines.size(); y++) {
            String line = lines.get(y);
            line = line.substring(3);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == 'P') {
                    playerLoc = new Location(x * scale, relY * scale);
                } else if (line.charAt(x) == 'K') {
                    keyLoc = new Location(x * scale, relY * scale);
                } else if (line.charAt(x) == 'D') {
                    doorLoc = new Location(x * scale, relY * scale);
                } else if (line.charAt(x) == 'X') {
                    blocks.add(new BlockGround(new Location(x * scale, relY * scale)));
                }
            }

            relY++;
        }
        if (playerLoc == null) {
            System.out.println("Level error: no player location specified.");
            return;
        }
        if (keyLoc == null) {
            System.out.println("Level error: no key location specified.");
            return;
        }
        if (doorLoc == null) {
            System.out.println("Level error: no door location specified.");
            return;
        }

        System.out.println(playerLoc.toString());
        System.out.println(keyLoc.toString());
        System.out.println(doorLoc.toString());
    }

    public int getSize() {
        return size;
    }

    public Location getSpawnPoint() {
        return playerLoc;
    }

    public Location getKeyLocation() {
        return keyLoc;
    }

    public Location getDoorLocation() {
        return doorLoc;
    }

    public double getGravity() {
        return 0.98;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }
}
