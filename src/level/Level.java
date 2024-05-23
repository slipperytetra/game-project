package level;

import block.*;
import block.decorations.Decoration;
import block.decorations.DecorationTree;
import block.decorations.DecorationTypes;
import block.decorations.FakeLightSpot;
import entity.*;
import main.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Level {
    private Player player;

    private int id;
    private int sizeWidth;
    private int sizeHeight;
    private int textCounter;
    private final String levelDoc;
    private int currentLine;

    private String name;
    private String nextLevel;
    private String backgroundImgFilePath;

    private BlockGrid grid;
    private LevelManager manager;

    private Location spawnPoint;
    private Location keyLoc;
    private Location doorLoc;

    private ArrayList<Decoration> decorations;
    private ArrayList<FakeLightSpot> spotLights;
    private ArrayList<Entity> entities;
    private ArrayList<Particle> particles;
    private ArrayList<String> lines;

    private HashMap<Integer, TextMessage> textMessages;
    private HashMap<Character, BlockTypes> blockKeyMap;
    private HashMap<Character, EntityType> entityKeyMap;
    private HashMap<Character, DecorationTypes> decorationKeyMap;

    /*
    *   The purpose of the level class is to extract and store data from level.txt files.
    *
    *   It reads each character after level_data and stores blocks into the level's BlockGrid
    *   based on the keycodes assigned in the txt file.
    *
    *   FOr entities and decorations, they are stored into the appropriate lists.
    * */

    public Level(LevelManager manager, int id, String levelDoc) {
        this.manager = manager;
        this.id = id;
        this.levelDoc = levelDoc;
        this.lines = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.decorations = new ArrayList<>();
        this.spotLights = new ArrayList<>();
        this.particles = new ArrayList<>();
        this.textMessages = new HashMap<>();

        this.blockKeyMap = new HashMap<>();
        this.entityKeyMap = new HashMap<>();
        this.decorationKeyMap = new HashMap<>();

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
        currentLine = 6;
    }

    public void load() {
        int relY = 0;
        for (int y = currentLine + sizeHeight + 1; y < lines.size(); y++) { // Assign character codes to types
            String line = lines.get(y).replaceAll(" ", "").replaceAll(":", "");
            char key = line.charAt(0);
            String type = line.substring(1);
            //System.out.println("assigning '" + key + "' to " + type);
            assignKeyToMap(key, type);
        }

        for (int y = currentLine; y < sizeHeight + currentLine; y++) { // Place objects into the world via BlockGrid and lists
            String line = lines.get(y).replaceAll(" ", "").replaceAll("\\[", "").replaceAll("]", "");
            //System.out.println(line);
            for (int x = 0; x < line.length(); x++) {
                char key = line.charAt(x);
                double spawnX = x * Game.BLOCK_SIZE;
                double spawnY = relY * Game.BLOCK_SIZE;
                Location spawnLoc = new Location(spawnX, spawnY);

                if (blockKeyMap.containsKey(key)) {
                    BlockTypes type = blockKeyMap.get(key);
                    Block block = new BlockSolid(blockKeyMap.get(key), spawnLoc);
                    if (type == BlockTypes.VOID) {
                        block = new BlockVoid(spawnLoc);
                    } else if (type == BlockTypes.LADDER) {
                        block = new BlockClimbable(type, spawnLoc);
                    }else if (type == BlockTypes.ROPE) {
                        block = new BlockClimbable(type, spawnLoc);
                    } else if (type == BlockTypes.WATER_TOP) {
                        block = new BlockLiquid(type, spawnLoc);
                    } else if (type == BlockTypes.WATER_BOTTOM) {
                        block = new BlockLiquid(type, spawnLoc);
                    } else if (type == BlockTypes.LAVA) {
                        block = new BlockLiquid(type, spawnLoc);
                    }

                    grid.setBlock(x, relY, block);
                }

                if (entityKeyMap.containsKey(key)) {
                    EntityType type = entityKeyMap.get(key);
                    Entity entity = null;
                    if (type == EntityType.PLAYER) {
                        player = new Player(this, spawnLoc);
                        double heightDiff = player.getLocation().getY() - (player.getHeight() - Game.BLOCK_SIZE);
                        spawnPoint = new Location(player.getLocation().getX(), heightDiff);
                        player.setLocation(player.getLocation().getX(), heightDiff);
                    } else if (type == EntityType.DOOR) {
                        doorLoc = new Location(spawnLoc.getX(), spawnLoc.getY());
                        entity = new Door(this, spawnLoc);
                    } else if (type == EntityType.HEART) {
                        entity = new Heart(this, spawnLoc);
                    } else if (type == EntityType.PLANT_MONSTER) {
                        entity = new EnemyPlant(this, spawnLoc);
                    } else if (type == EntityType.KEY) {
                        keyLoc = new Location(spawnLoc.getX(), spawnLoc.getY());
                        entity = new Key(this, spawnLoc);
                    } else if (type == EntityType.SKULL_HEAD) {
                        Location skullHeadLoc = new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE);
                        SkullHead skullHead = new SkullHead(this, skullHeadLoc);
                        addEntity(skullHead);
                    } else if (type == EntityType.GOLD_COIN) {
                        Location coinLoc = new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE);
                        goldCoin coin = new goldCoin(this, coinLoc);
                        addEntity(coin);
                    } else if (type == EntityType.BEE) {
                        Location beeLoc = new Location(x * Game.BLOCK_SIZE, relY * Game.BLOCK_SIZE);
                        Bee bee = new Bee(this, beeLoc);
                        addEntity(bee);
                    }

                    if (entity != null) {
                        double heightDiff = entity.getLocation().getY() - (entity.getCollisionBox().getHeight() - Game.BLOCK_SIZE);
                        entity.setLocation(entity.getLocation().getX(), heightDiff);
                        addEntity(entity);
                    }
                }

                if (decorationKeyMap.containsKey(key)) {
                    DecorationTypes type = decorationKeyMap.get(key);
                    addDecoration(type, spawnLoc);
                }
            }

            relY++;
        }
        if (!backgroundImgFilePath.isEmpty()) {
            getManager().getEngine().imageBank.put("background", Toolkit.getDefaultToolkit().createImage(backgroundImgFilePath));
        }
        if (player == null) {
            System.out.println("Warning: no player location specified.");
            return;
        }
        if (keyLoc == null) {
            System.out.println("Warning: no key location specified.");
            return;
        }
        if (doorLoc == null) {
            System.out.println("Warning: no door location specified.");
            return;
        }

        System.out.println("Player: " + player.getLocation().toString());
        System.out.println("Key: " + keyLoc.toString());
        System.out.println("Door: " + doorLoc.toString());
    }


    public void update(double dt) {
        getPlayer().playerMovement(getManager().getEngine().keysPressed);
        getPlayer().update(dt);

        // Using Iterators so that objects can be removed dynamically.
        Iterator<Entity> iter = getEntities().iterator();
        while (iter.hasNext()) {
            Entity entity = iter.next();
            if (entity.isActive()) {
                if (entity.getCollisionBox().collidesWith(getManager().getEngine().getCamera().getCollisionBox())) {
                    entity.update(dt);
                }
            } else {
                iter.remove();
            }
        }

        Iterator<Particle> iterPart = getParticles().iterator();
        while (iterPart.hasNext()) {
            Particle particle = iterPart.next();
            if (particle.isActive()) {
                particle.update(dt);
            } else {
                iterPart.remove();
            }
        }

        for (Decoration deco : getDecorations()) {
            if (deco.getCollisionBox().collidesWith(getManager().getEngine().getCamera().getCollisionBox())) {
                deco.update(dt);
            }
        }

        for (FakeLightSpot spotLight : getSpotLights()) {
            spotLight.update(dt);
        }
    }

    public void reset() {
        getPlayer().setLocation(spawnPoint.getX(), spawnPoint.getY());
        getPlayer().setHealth(getPlayer().getMaxHealth());
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
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

    public ArrayList<Particle> getParticles(){
        return particles;
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
        Decoration deco = null;
        loc.setY(loc.getY() + 1);
        if (type.hasFallingLeaves()) {
            deco = new DecorationTree(type, loc, texture.getWidth(), texture.getHeight(), this);
        } else {
            deco = new Decoration(type, loc, texture.getWidth(), texture.getHeight());
        }

        if (type == DecorationTypes.TALL_GRASS || type == DecorationTypes.FOREST_PLANT_0 || type == DecorationTypes.FOREST_PLANT_1) {
            Random rand = new Random();
            deco.getLocation().setY(loc.getY() + rand.nextDouble(0, 16));
            deco.setScale(rand.nextDouble(deco.getScale() * 0.75, deco.getScale() * 1.25));
        }

        decorations.add(deco);

        if (deco.getType().hasLightSpots()) {
            FakeLightSpot spotLight = new FakeLightSpot(deco);
            spotLights.add(spotLight);
        }
    }

    public void spawnParticle(ParticleTypes type, double x, double y) {
        Particle particle = new Particle(type, new Location(x, y), this);
        getParticles().add(particle);
    }

    public void spawnParticle(ParticleTypes type, double x, double y, double velX, double velY) {
        Particle particle = new Particle(type, new Location(x, y), this);
        particle.setVelX(velX);
        particle.setVelY(velY);
        getParticles().add(particle);
    }

    private void assignKeyToMap(char key, String input) {
        input = input.toUpperCase();
        for (BlockTypes type : BlockTypes.values()) {
            if (type.toString().equals(input)) {
                blockKeyMap.put(key, BlockTypes.valueOf(input));
                return;
            }
        }

        for (EntityType type : EntityType.values()) {
            if (type.toString().equals(input)) {
                entityKeyMap.put(key, EntityType.valueOf(input));
                return;
            }
        }

        for (DecorationTypes type : DecorationTypes.values()) {
            if (type.toString().equals(input)) {
                decorationKeyMap.put(key, DecorationTypes.valueOf(input));
                return;
            }
        }
    }
}
