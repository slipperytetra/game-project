package main;

import block.BlockTypes;
import level.Level;
import level.LevelManager;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Game extends GameEngine {
    public static int BLOCK_SIZE = 32;

    public long timeSinceLastFrame;
    public long lastTime;
    public long currentTime;
    public HashMap<String, BufferedImage> imageBank;
    private Set<Integer> keysPressed = new HashSet();

    LevelManager lvlManager;
    private Level activeLevel;
    Camera camera;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameMenu::new);
    }

    public void startGame(){
        createGame(this);
    }

    public void init() {
        this.imageBank = new HashMap<>();
        loadBlockImages();
        loadCharacterImages();

        this.setWindowSize(1280, 720);
        this.lvlManager = new LevelManager(this);
        setActiveLevel(lvlManager.DEMO);

        System.out.println("Starting X position: " + this.activeLevel.getPlayer().getLocation().getX());
        System.out.println("Starting Y position: " + this.activeLevel.getPlayer().getLocation().getY());
    }

    public Level getActiveLevel() {
        return activeLevel;
    }

    public void setActiveLevel(Level level) {
        this.activeLevel = level;
        level.load();
        this.camera = new Camera(this, level.getPlayer());
    }

    public void update(double dt) {
        lastTime = currentTime;
        currentTime = System.currentTimeMillis();
        timeSinceLastFrame = currentTime - lastTime;
        camera.update();

        activeLevel.getPlayer().playerMovement(keysPressed);
        activeLevel.getPlayer().update(dt);
        for (Entity entity : activeLevel.getEntities()) {
            if (entity.isActive()) {
                entity.update(dt);
            }
        }
    }

    public void paintComponent() {
        this.clearBackground(width(), height());
        if (imageBank.containsKey("background")) {
            this.drawImage(imageBank.get("background"), 0, 0, this.width(), this.height());
        }
        camera.draw();
    }

    @Override
    public void keyPressed(KeyEvent event) {
        this.keysPressed.add(event.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent event) {
        this.keysPressed.remove(event.getKeyCode());
        if (event.getKeyCode() == 72) {
            camera.showHitboxes = !camera.showHitboxes;
            //setActiveLevel(lvlManager.DEMO_2);
        }

        if (event.getKeyCode() == 65 || event.getKeyCode() == 68) {
            activeLevel.getPlayer().setDirectionX(0);
        }

        if (event.getKeyCode() == 83 || event.getKeyCode() == 87) {
            activeLevel.getPlayer().setDirectionY(0);
        }
    }

    public void loadBlockImages() {
        for (BlockTypes type : BlockTypes.values()) {
            if (type == BlockTypes.VOID) {
                continue;
            }

            System.out.println();
            imageBank.put(type.toString(), (BufferedImage) loadImage(type.getFilePath()));
        }
    }

    public void loadCharacterImages() {
        imageBank.put("player_run_0", (BufferedImage) loadImage("resources/images/characters/run0.png"));
        imageBank.put("player_run_1", (BufferedImage) loadImage("resources/images/characters/run1.png"));
        imageBank.put("player_run_2", (BufferedImage) loadImage("resources/images/characters/run2.png"));
        imageBank.put("player_run_3", (BufferedImage) loadImage("resources/images/characters/run3.png"));
        imageBank.put("player_jump_0", (BufferedImage) loadImage("resources/images/characters/jump0.png"));
        imageBank.put("player_jump_1", (BufferedImage) loadImage("resources/images/characters/jump1.png"));
        imageBank.put("player_jump_2", (BufferedImage) loadImage("resources/images/characters/jump2.png"));
        imageBank.put("player_jump_3", (BufferedImage) loadImage("resources/images/characters/jump3.png"));

        for (EntityType type : EntityType.values()) {
            imageBank.put(type.toString().toLowerCase(), (BufferedImage) loadImage(type.getFilePath()));
        }
    }

    public BufferedImage getTexture(String textureName) {
        return imageBank.get(textureName);
    }

    public BufferedImage flipImageHorizontal(BufferedImage img) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1.0, 1.0);
        tx.translate(-img.getWidth(null), 0.0);
        AffineTransformOp op = new AffineTransformOp(tx, 1);
        return op.filter(img, null);
    }
}
