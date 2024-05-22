package level;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import block.*;
import block.decorations.Decoration;
import block.decorations.DecorationTypes;
import block.decorations.FakeLightSpot;
import main.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class Level {
    LevelManager manager;
    private Player player;
    private Door door;
    private Key key;
    private String nextLevel;

    int id;
    String name;
    int sizeWidth;
    int sizeHeight;
    ArrayList<String> lines;
    HashMap<Integer, TextMessage> textMessages;
    BlockGrid grid;
    private int textCounter;

    private final String levelDoc;
    public final double gravity = 9.8;
    public double scale = 2;
    String backgroundImgFilePath;
    Location spawnPoint;
    Location keyLoc;
    Location doorLoc;

    ArrayList<Decoration> decorations;
    ArrayList<FakeLightSpot> spotLights;
    ArrayList<Entity> entities;

    public Level(LevelManager manager, int id, String levelDoc) {
        this.manager = manager;
        this.id = id;
        this.levelDoc = levelDoc;
        this.lines = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.decorations = new ArrayList<>();
        this.spotLights = new ArrayList<>();
        this.textMessages = new HashMap<>();
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
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't locate file!");
            return;
        }

        name = lines.get(0).substring("name: ".length());
        backgroundImgFilePath = lines.get(1).substring("background: ".length());
        nextLevel = lines.get(2).substring("next_level: ".length());
        sizeWidth = Integer.parseInt(lines.get(3).substring("level_width: ".length()));
        sizeHeight = Integer.parseInt(lines.get(4).substring("level_height: ".length()));
        System.out.println(sizeWidth);
        System.out.println(sizeHeight);
        this.grid = new BlockGrid(sizeWidth, sizeHeight);
    }

    public void load() {
        int relY = 0;
        for (int y = 6; y < lines.size(); y++) {
            String line = lines.get(y);
            line = line.substring(3);
            for (int x = 0; x < line.length(); x++) {
                double spawnX = x * Game.BLOCK_SIZE;
                double spawnY = relY * Game.BLOCK_SIZE;
                Location spawnLoc = new Location(spawnX, spawnY);
                if (line.charAt(x) == 'P') {
                    player = new Player(this, spawnLoc);

                    spawnY = spawnY - (player.getHeight() - Game.BLOCK_SIZE);
                    player.setLocation(player.getLocation().getX(), spawnY);
                    spawnPoint = new Location(spawnX, spawnY);
                } else if (line.charAt(x) == 'K') {
                    keyLoc = spawnLoc;
                    key = new Key(this, spawnLoc);

                    double heightDiff = key.getLocation().getY() - (key.getHeight() - Game.BLOCK_SIZE);
                    key.setLocation(key.getLocation().getX(), heightDiff);
                    addEntity(key);
                } else if (line.charAt(x) == 'D' ||line.charAt(x) == 'd') {
                    doorLoc = spawnLoc;
                    door = new Door(this, spawnLoc);
                    if(line.charAt(x) == 'd'){
                        door.setType(EntityType.STONE_DOOR);
                    }

                    double heightDiff = door.getLocation().getY() - (door.getHeight() - Game.BLOCK_SIZE);
                    door.setLocation(door.getLocation().getX(), heightDiff);
                    addEntity(door);
                } else if (line.charAt(x) == 'X') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.DIRT, spawnLoc));
                } else if (line.charAt(x) == 'G') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.GRASS, spawnLoc));
                } else if (line.charAt(x) == 'L') {
                    grid.setBlock(x, relY, new BlockClimbable(BlockTypes.LADDER, spawnLoc));
                } else if (line.charAt(x) == 'E') {
                    EnemyPlant enemy = new EnemyPlant(this, spawnLoc);

                    double heightDiff = enemy.getLocation().getY() - Game.BLOCK_SIZE - 16;
                    enemy.setLocation(enemy.getLocation().getX(), heightDiff);
                    addEntity(enemy);
                } else if (line.charAt(x) == 'B') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.BARRIER, spawnLoc));
                } else if (line.charAt(x) == '@') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.FOREST_GRASS, spawnLoc));
                } else if (line.charAt(x) == '!') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.FOREST_GROUND, spawnLoc));
                } else if (line.charAt(x) == 'W') {
                    grid.setBlock(x, relY, new BlockLiquid(BlockTypes.WATER_TOP, spawnLoc));
                } else if (line.charAt(x) == 'O') {
                    grid.setBlock(x, relY, new BlockLiquid(BlockTypes.WATER_BOTTOM, spawnLoc));
                } else if (line.charAt(x) == 'S') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.STONE_FLOOR, spawnLoc));
                } else if (line.charAt(x) == 's') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.STONE_FILLER, spawnLoc));
                } else if (line.charAt(x) == 'l') {
                    grid.setBlock(x, relY, new BlockLiquid(BlockTypes.LAVA, spawnLoc));
                } else if (line.charAt(x) == 'p') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.BRIDGE, spawnLoc));
                } else if (line.charAt(x) == 'm') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.BL, spawnLoc));
                } else if (line.charAt(x) == 'r') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.BR, spawnLoc));
                } else if (line.charAt(x) == 'h') {
                    Heart heart = new Heart(this, spawnLoc);
                    addEntity(heart);
                } else if (line.charAt(x) == 'R') {
                    addDecoration(DecorationTypes.ROCK, spawnLoc);
                } else if (line.charAt(x) == 'b') {
                    addDecoration(DecorationTypes.BUSH, spawnLoc);
                } else if (line.charAt(x) == 't') {
                    addDecoration(DecorationTypes.TREE, spawnLoc);
                } else if (line.charAt(x) == 'z') {
                    addDecoration(DecorationTypes.LAMP_POST, spawnLoc);
                } else if (line.charAt(x) == '0') {
                    addDecoration(DecorationTypes.TREE_WILLOW_0, spawnLoc);
                } else if (line.charAt(x) == '1') {
                    addDecoration(DecorationTypes.TREE_WILLOW_1, spawnLoc);
                } else if (line.charAt(x) == '2') {
                    addDecoration(DecorationTypes.TREE_WILLOW_2, spawnLoc);
                } else if (line.charAt(x) == '3') {
                    addDecoration(DecorationTypes.HANGING_VINE, spawnLoc);
                } else if (line.charAt(x) == '4') {
                    addDecoration(DecorationTypes.HANGING_VINE_FLOWERS, spawnLoc);
                }
                else if (line.charAt(x) == '5') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.SNOWTOPR, spawnLoc));
                }else if (line.charAt(x) == '6') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.SNOWMID, spawnLoc));
                }else if (line.charAt(x) == '7') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.SNOWFILL, spawnLoc));
                }
                else if (line.charAt(x) == '8') {
                    grid.setBlock(x, relY, new BlockSolid(BlockTypes.SNOWDOWN, spawnLoc));
                }
                else if (line.charAt(x) == '9') {
                    addDecoration(DecorationTypes.SNOW_TREE, spawnLoc);
                }
                else if (line.charAt(x) == 'a') {
                    addDecoration(DecorationTypes.SNOW_BUSH, spawnLoc);
                }

            }

            relY++;
        }
        if (!backgroundImgFilePath.isEmpty()) {
            getManager().getEngine().imageBank.put("background", Toolkit.getDefaultToolkit().createImage(backgroundImgFilePath));
        }
        if (player == null) {
            System.out.println("level.Level error: no player location specified.");
            return;
        }
        if (keyLoc == null) {
            System.out.println("level.Level error: no key location specified.");
            return;
        }
        if (doorLoc == null) {
            System.out.println("level.Level error: no door location specified.");
            return;
        }

        System.out.println("Player: " + player.getLocation().toString());
        System.out.println("Key: " + keyLoc.toString());
        System.out.println("Door: " + doorLoc.toString());
    }


    public void update(double dt) {
        getPlayer().playerMovement(getManager().getEngine().keysPressed);
        getPlayer().update(dt);

        Iterator<Entity> iter = getEntities().iterator();
        while (iter.hasNext()) {
            Entity entity = iter.next();
            if (entity.isActive()) {
                entity.update(dt);
            } else {
                iter.remove();
            }
        }

        for (FakeLightSpot spotLight : getSpotLights()) {
            spotLight.update(dt);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void reset() {
        getPlayer().setLocation(spawnPoint.getX(), spawnPoint.getY());
        getPlayer().setHealth(getPlayer().getMaxHealth());
    }

    public Door getDoor() {
        return door;
    }

    public String getNextLevel() {
        return nextLevel;
    }

    public Location getSpawnPoint() {
        return spawnPoint;
    }
    public int getId(){
        return id;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public LevelManager getManager() {
        return manager;
    }

    public int getWidth() {
        return sizeWidth;
    }

    public int getHeight() {
        return sizeHeight;
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

    public BlockGrid getBlockGrid() {
        return grid;
    }

    public ArrayList<FakeLightSpot> getSpotLights() {
        return spotLights;
    }

    public void addTextMessage(TextMessage text) {
        textMessages.put(textCounter, text);
        textCounter++;
    }

    public HashMap<Integer, TextMessage> getTextMessages() {
        return textMessages;
    }

    public void clearTextMessages() {
        textMessages.clear();
    }

    public ArrayList<Decoration> getDecorations() {
        return decorations;
    }

    private void addDecoration(DecorationTypes type, Location loc) {
        BufferedImage texture = (BufferedImage) getManager().getEngine().getTexture(type.toString());
        Decoration decoTree = new Decoration(type, loc, texture.getWidth(), texture.getHeight());
        decorations.add(decoTree);

        if (decoTree.getType().hasLightSpots()) {
            FakeLightSpot spotLight = new FakeLightSpot(decoTree);
            spotLights.add(spotLight);
        }
    }
}
