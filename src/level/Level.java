package level;

import block.*;
import block.decorations.*;
import entity.*;
import level.item.Inventory;
import main.*;
import org.w3c.dom.Text;
import utils.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Level {
    private Player player;

    private int id;
    private int sizeWidth;
    private int sizeHeight;
    private int textCounter;
    private final String levelDoc;
    private int currentLine;
    public int maxLines;
    public int loadCompletion;
    private boolean isEditMode;

    private String name;
    private String nextLevel;
    private String overlay;
    private String backgroundImgFilePath;
    private String midgroundImgFilePath;
    private String foregroundImgFilePath;
    private String backgroundmusicFilePath;
    private GameEngine.AudioClip backgroundMusic;
    private CollisionBox bounds;

    public BlockGrid grid;
    private LevelManager manager;

    private Location spawnPoint;
    private Location keyLoc;
    private Location doorLoc;

    public HashSet<GameObject> gameObjects;
    private HashSet<Inventory> openInventories;
    private HashSet<Decoration> decorations;
    private HashSet<FakeLightSpot> spotLights;
    private HashSet<Entity> entities;
    private HashSet<Particle> particles;
    private ArrayList<String> lines;
    private ArrayList<String> levelData;

    private HashMap<Integer, TextMessage> textMessages;
    public HashMap<Character, BlockTypes> blockKeyMap;
    public HashMap<Character, EntityType> entityKeyMap;
    public HashMap<Character, DecorationTypes> decorationKeyMap;

    private QuadTree qtree;

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

        this.entities = new HashSet<>();
        this.decorations = new HashSet<>();
        this.spotLights = new HashSet<>();
        this.particles = new HashSet<>();
        this.gameObjects = new HashSet<>();
        this.openInventories = new HashSet<>();

        this.levelData = new ArrayList<>();
        this.textMessages = new HashMap<>();

        this.blockKeyMap = new HashMap<>();
        blockKeyMap.put('.', BlockTypes.VOID);

        this.entityKeyMap = new HashMap<>();
        this.decorationKeyMap = new HashMap<>();
    }

    public void init() {
        File file = new File(levelDoc);
        try {
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                lines.add(line.replaceAll(" ", "").replaceAll(":", "").replaceAll("\\[", "").replaceAll("\\]", ""));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't locate file!");
            return;
        }


        name = lines.get(0).substring("name".length());
        backgroundImgFilePath = lines.get(1).substring("background".length());
        midgroundImgFilePath = lines.get(2).substring("midground".length());
        foregroundImgFilePath = lines.get(3).substring("foreground".length());
        backgroundmusicFilePath = lines.get(4).substring("background_music".length());
        overlay = lines.get(5).substring("overlay".length());
        nextLevel = lines.get(6).substring("next_level".length());
        //+2 to skip over level_data

        currentLine = 8;
        sizeHeight = 0;
        while (!lines.get(sizeHeight + currentLine).contains("keymap")) {
            String line = lines.get(sizeHeight + currentLine);
            levelData.add(line);
            sizeHeight++;
        }

        for (int i = currentLine; i < sizeHeight; i++) {
            sizeWidth = Math.max(sizeWidth, lines.get(i).length());
        }

        //System.out.println("W/L: " + sizeWidth + ", " + sizeHeight);
        this.grid = new BlockGrid(this, sizeWidth, sizeHeight);
        this.bounds = new CollisionBox(0, 0, sizeWidth * Game.BLOCK_SIZE, sizeHeight * Game.BLOCK_SIZE);
        this.maxLines = lines.size();
    }

    public void load() {
        System.out.println("Loading level '" + getName() + "'");
        for (int y = currentLine + 1 + sizeHeight; y < lines.size(); y++) { // Assign character codes to types
            String line = lines.get(y);
            char key = line.charAt(0);
            String type = line.substring(1);
            assignKeyToMap(key, type);
        }

        int y = 0;
        for (String line : levelData) { // Place objects into the world via BlockGrid and lists
            for (int x = 0; x < line.length(); x++) {
                char key = line.charAt(x);
                double spawnX = x * Game.BLOCK_SIZE;
                double spawnY = y * Game.BLOCK_SIZE;
                Location spawnLoc = new Location(spawnX, spawnY);

                if (blockKeyMap.containsKey(key)) {
                    BlockTypes type = blockKeyMap.get(key);
                    Block block = new Block(this, spawnLoc, blockKeyMap.get(key));
                    if (type == BlockTypes.FOREST_GROUND) {
                        block = new BlockSet(this, spawnLoc, type);
                    } else if (type == BlockTypes.FOREST_GROUND_CRACKED) {
                        block = new BlockCracked(this, spawnLoc, type);
                    } else if (type == BlockTypes.LADDER) {
                        block = new BlockClimbable(this, spawnLoc, type);
                    } else if (type == BlockTypes.ROPE) {
                        block = new BlockClimbable(this, spawnLoc, type);
                    } else if (type == BlockTypes.WATER_TOP) {
                        block = new BlockLiquid(this, spawnLoc, type);
                    } else if (type == BlockTypes.WATER_BOTTOM) {
                        block = new BlockLiquid(this, spawnLoc, type);
                    } else if (type == BlockTypes.LAVA) {
                        block = new BlockLiquid(this, spawnLoc, type);
                    } else if (type == BlockTypes.PLAYER_SPAWN) {
                        block = new BlockSpawnPoint(this, spawnLoc);
                        spawnPoint = new Location(block.getLocation());
                    }

                    //System.out.println("Set block at " + x + ", " + y + " to " + block.getType().toString());
                    grid.setBlock(x, y, block);
                }

                if (entityKeyMap.containsKey(key)) {
                    EntityType type = entityKeyMap.get(key);
                    Entity entity = null;
                    //System.out.println("Checking for '" + type.toString() + "'");
                    if (type == EntityType.DOOR) {
                        System.out.println("Found");
                        doorLoc = new Location(spawnLoc.getX(), spawnLoc.getY());
                        System.out.println("Loc");
                        Door door = new Door(this, spawnLoc);
                        entity = door;
                        System.out.println("Assigned");
                    } else if (type == EntityType.HEART) {
                        entity = new ItemHeart(this, spawnLoc);
                    } else if (type == EntityType.GOLD_COIN) {
                        entity = new ItemGoldCoin(this, spawnLoc);
                    } else if (type == EntityType.PLANT_MONSTER) {
                        entity = new EnemyPlant(this, spawnLoc);
                    } else if (type == EntityType.BEE) {
                        entity = new EnemyBee(this, spawnLoc);
                    } else if (type == EntityType.ARROW_BUNDLE) {
                        entity = new ItemArrowBundle(this, spawnLoc);
                    } else if (type == EntityType.KEY) {
                        keyLoc = new Location(spawnLoc.getX(), spawnLoc.getY());
                        entity = new ItemKey(this, spawnLoc);
                    }

                    if (entity != null) {
                        //System.out.println("Spawning '" + type.toString() + "'");
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

            y++;
            loadCompletion++;
        }

        player = new Player(this, getSpawnPoint().clone());
        double heightDiff = getSpawnPoint().getY() - (player.getHeight() - Game.BLOCK_SIZE);
        getSpawnPoint().setY(heightDiff);
        player.setLocation(getSpawnPoint().getX(), getSpawnPoint().getY());

        grid.applySets();
        grid.applySets();

        if (!backgroundImgFilePath.isEmpty()) {
            getManager().getEngine().getTextureBank().addTexture("background", new Texture((BufferedImage) getManager().getEngine().loadImage(backgroundImgFilePath)));
        }
        if (!midgroundImgFilePath.isEmpty()) {
            getManager().getEngine().getTextureBank().addTexture("midground", new Texture((BufferedImage) getManager().getEngine().loadImage(midgroundImgFilePath)));
        }
        if (!foregroundImgFilePath.isEmpty()) {
            getManager().getEngine().getTextureBank().addTexture("foreground", new Texture((BufferedImage) getManager().getEngine().loadImage(foregroundImgFilePath)));
        }
        if (!overlay.isEmpty()) {
            getManager().getEngine().getTextureBank().addTexture("overlay", new Texture((BufferedImage) getManager().getEngine().loadImage(overlay)));
        }
        if (!backgroundmusicFilePath.isEmpty()) {
            backgroundMusic = getManager().getEngine().loadAudio(backgroundmusicFilePath);
        }
        if (player == null) {
            System.out.println("Warning: no player location specified.");
            return;
        }
        if (keyLoc == null) {
            System.out.println("Warning: no key location specified.");
        }
        if (doorLoc == null) {
            System.out.println("Warning: no door location specified.");
        }

        if (getBackgroundMusic() != null) {
            getManager().getEngine().startAudioLoop(getBackgroundMusic());
        }

        System.out.println(getActualWidth() + ", " + getActualHeight());
        gameObjects.addAll(getEntities());

        this.qtree = new QuadTree(new CollisionBox(0, 0, getActualWidth(), getActualHeight()), 6);
        this.qtree.insert(getPlayer());
        for (Entity entity : getEntities()) {
            this.qtree.insert(entity);
        }

        for (int bx = 0; bx < getBlockGrid().getWidth(); bx++) {
            for (int by = 0; by < getBlockGrid().getHeight(); by++) {
                Block blk = getBlockGrid().getBlockAt(bx, by);
                if (blk.isCollidable()) {
                    this.qtree.insert(blk);
                }
            }
        }

        this.qtree = new QuadTree(new CollisionBox(0, 0, getActualWidth(), getActualHeight()), 6);
        createImage();
    }


    public void update(double dt) {
        getPlayer().playerMovement(getManager().getEngine().keysPressed);
        getPlayer().update(dt);

        // Using Iterators so that objects can be removed dynamically.
        Iterator<Entity> iter = getEntities().iterator();
        while (iter.hasNext()) {
            Entity entity = iter.next();
            if (entity.isActive()) {
                if (entity.getCollisionBox().collidesWith(getManager().getEngine().getCamera().getCollisionBox()) && !entity.isPersistent()) {
                    //System.out.println("Running " + entity.getType().toString() + " update(dt)");
                    entity.update(dt);
                }

                if (entity.isPersistent()) {
                    entity.update(dt);
                }
            }
        }

        Iterator<Inventory> iterInv = getOpenInventories().iterator();
        while (iterInv.hasNext()) {
            Inventory inv = iterInv.next();
            if (inv.isOpen()) {
                inv.update(dt);
            } else {
                iterInv.remove();
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

        Iterator<Decoration> iterDeco = getDecorations().iterator();
        while (iterDeco.hasNext()) {
            Decoration deco = iterDeco.next();
            if (deco.getCollisionBox().collidesWith(getManager().getEngine().getCamera().getCollisionBox()) && deco.isActive()) {
                deco.update(dt);
            }

            if (!deco.isActive()) {
                iterDeco.remove();
            }
        }

        Iterator<FakeLightSpot> iterLight = getSpotLights().iterator();
        while (iterLight.hasNext()) {
            FakeLightSpot spotLight = iterLight.next();
            if (spotLight.isActive()) {
                spotLight.update(dt);
            } else {
                iterLight.remove();
            }
        }

        updateQuadTree(dt);
    }

    public void reset() {
        getPlayer().setLocation(getSpawnPoint().getX(), getSpawnPoint().getY());
        getPlayer().setHealth(getPlayer().getMaxHealth());
        for(Entity entity: getEntities()){
            if(!entity.isActive()){
                entity.reset();
            }
        }
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

    public void setSpawnPoint(double spawnX, double spawnY) {
        if (spawnPoint == null) {
            spawnPoint = new Location(spawnX, spawnY);
            return;
        }

        spawnPoint.setX(spawnX);
        spawnPoint.setY(spawnY);
    }
    public int getId(){
        return id;
    }

    public CollisionBox getBounds() {
        return bounds;
    }

    public void setBounds(double width, double height) {
        bounds.setSize(width, height);
    }

    public HashSet<Entity> getEntities() {
        return entities;
    }

    public HashSet<Particle> getParticles(){
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

    public HashSet<FakeLightSpot> getSpotLights() {
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

    public HashSet<Decoration> getDecorations() {
        return decorations;
    }

    public void addDecoration(DecorationTypes type, Location loc) {
        Decoration deco = null;
        loc.setY(loc.getY() + 1);
        if (type.hasFallingLeaves()) {
            deco = new DecorationTree(this, loc, type);
        } else {
            deco = new Decoration(this, loc, type);
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

    public void playSound(SoundType soundType) {
        getManager().getEngine().getAudioBank().playSound(soundType);
    }

    public void playSound(SoundType soundType, boolean loop) {
        getManager().getEngine().getAudioBank().playSound(soundType, loop);
    }

    public void playSound(SoundType soundType, boolean loop, float volume) {
        getManager().getEngine().getAudioBank().playSound(soundType, loop, volume);
    }

    public void spawnParticle(ParticleTypes type, double x, double y) {
        Particle particle = new Particle(type, new Location(x, y), this);
        getParticles().add(particle);
    }

    public void spawnParticle(ParticleTypes type, double x, double y, boolean flipped) {
        Particle particle = new Particle(type, new Location(x, y), this);
        particle.setFlipped(flipped);
        getParticles().add(particle);
    }

    public void spawnParticle(ParticleTypes type, double x, double y, double velX, double velY) {
        Particle particle = new Particle(type, new Location(x, y), this);
        particle.setVelX(velX);
        particle.setVelY(velY);
        getParticles().add(particle);
    }

    public void spawnParticle(ParticleTypes type, double x, double y, double velX, double velY, boolean flipped) {
        Particle particle = new Particle(type, new Location(x, y), this);
        particle.setFlipped(flipped);
        particle.setVelX(velX);
        particle.setVelY(velY);
        getParticles().add(particle);
    }

    public void spawnParticle(Particle particle) {
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

    public GameEngine.AudioClip getBackgroundMusic() {
        return backgroundMusic;
    }

    public void setBackgroundMusic(GameEngine.AudioClip backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
    }

    public double getActualWidth() {
        return getWidth() * Game.BLOCK_SIZE;
    }

    public double getActualHeight() {
        return getHeight() * Game.BLOCK_SIZE;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;

        if (getManager().getEngine().editingPanel != null) {
            getManager().getEngine().editingPanel.setVisible(isEditMode());
        }
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public String getBackgroundImgFilePath() {
        return backgroundImgFilePath;
    }

    public String getBackgroundmusicFilePath() {
        return backgroundmusicFilePath;
    }

    public String getOverlay() {
        return overlay;
    }

    public String getForegroundImgFilePath() {
        return foregroundImgFilePath;
    }

    public String getMidgroundImgFilePath() {
        return midgroundImgFilePath;
    }

    public QuadTree getQuadTree() {
        return qtree;
    }

    private void updateQuadTree(double dt) {
        this.qtree = new QuadTree(new CollisionBox(0, 0, getActualWidth(), getActualHeight()), 6);
        this.qtree.focus = getPlayer();
        for (int bx = 0; bx < getBlockGrid().getWidth(); bx++) {
            for (int by = 0; by < getBlockGrid().getHeight(); by++) {
                Block blk = getBlockGrid().getBlockAt(bx, by);

                if (blk.isCollidable()) {
                    if (blk.getType() == BlockTypes.FOREST_GROUND_CRACKED) {
                        blk.update(dt);
                    }

                    this.qtree.insert(blk);
                }
            }
        }

        for (Entity entity : getEntities()) {
            if (!entity.isDead()) {
                this.qtree.insert(entity);
            }
        }

        this.qtree.insert(getPlayer());
    }

    public HashSet<Inventory> getOpenInventories() {
        return openInventories;
    }

    private void createImage() {
        int width = 6;
        File file = new File("saves/levels/level_" + getName().toLowerCase() + "_icon.png");
        BufferedImage img = new BufferedImage(width * 2 * 32, width * 2 * 32, BufferedImage.TYPE_INT_ARGB);
        Texture bgText = getManager().getEngine().getTextureBank().getTexture("background");
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                //System.out.println(x + ", " + y);
                if (y >= bgText.getHeight()) {
                    break;
                }
                int rgb = bgText.getImage().getRGB(x, y);
                img.setRGB(x, y, rgb);
            }
        }

        int spLocX = getSpawnPoint().getTileX();
        int spLocY = getSpawnPoint().getTileY();

        int pLocX = 0;
        int pLocY = 0;

        for (int x = -width; x < width; x++) {
            for (int y = -width; y < width; y++) {
                Block block = getBlockGrid().getBlockAt(spLocX + x, spLocY + y);
                if (block == null || block.getType() == BlockTypes.VOID || block.getType() == BlockTypes.BARRIER) {
                    continue;
                }

                if (block.getType() == BlockTypes.PLAYER_SPAWN) {
                    pLocX = x;
                    pLocY = y;
                }

                Texture texture = block.getTexture();
                for (int imgX = 0; imgX < texture.getImage().getWidth(); imgX++) {
                    for (int imgY = 0; imgY < texture.getImage().getHeight(); imgY++) {
                        img.setRGB(imgX + ((x + width) * 32), imgY + ((y + width) * 32), new Color(texture.getImage().getRGB(imgX, imgY), true).getRGB());
                    }
                }
            }
        }

        Texture player = getManager().getEngine().getTextureBank().getTexture(EntityType.PLAYER.toString());
        AffineTransform tx = AffineTransform.getScaleInstance(getPlayer().getScale(), getPlayer().getScale());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage pImg = op.filter(player.getImage(), null);
        for (int pX = 0; pX < pImg.getWidth(); pX++) {
            for (int pY = 0; pY < pImg.getHeight(); pY++) {
                img.setRGB(pX + ((pLocX + width) * 32), pY + ((pLocY + width) * 32), new Color(pImg.getRGB(pX, pY), true).getRGB());
            }
        }


        try {
            AffineTransform afTx = AffineTransform.getScaleInstance((double) 250 / img.getWidth() , (double) 250 / img.getHeight());
            AffineTransformOp atOP = new AffineTransformOp(afTx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            ImageIO.write(atOP.filter(img, null), "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HashSet<GameObject> getGameObjects() {
        return gameObjects;
    }

    public void addGameObject(GameObject object) {
        gameObjects.add(object);
    }

    public void removeGameObject(GameObject object) {
        gameObjects.remove(object);
    }

    public void removeGameObject(UUID uuid) {
        for (GameObject obj : gameObjects) {
            if (obj.getUniqueID().compareTo(uuid) == 0) {
                gameObjects.remove(obj);
                break;
            }
        }
    }
}
