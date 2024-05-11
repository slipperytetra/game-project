package main;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import block.Block;
import block.BlockTypes;
import level.Level;
import level.LevelManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Game extends GameEngine {
    public static int BLOCK_SIZE = 32;
    public static int WIDTH = 600;
    public static int HEIGHT = 600;
    public static int FRAME_RATE = 60;
    public long timeSinceLastFrame;
    public long lastTime;
    public long currentTime;
    public HashMap<String, Image> imageBank;
    private Set<Integer> keysPressed = new HashSet();

    public Player player;
    LevelManager lvlManager;
    Level activeLevel;
    Camera camera;

    public Game() {
    }

    public static void main(String[] args) {
        createGame(new Game());
    }

    public void init() {
        this.imageBank = new HashMap<>();
        loadBlockImages();
        loadCharacterImages();

        this.setWindowSize(WIDTH, HEIGHT);
        this.player = new Player(this);
        this.camera = new Camera(this, player);
        this.lvlManager = new LevelManager(this);
        this.activeLevel = lvlManager.DEMO;
        this.player.setLocation(activeLevel.getSpawnPoint().getX(), activeLevel.getSpawnPoint().getY());


        System.out.println("Starting X position: " + this.player.getLocation().getX());
        System.out.println("Starting Y position: " + this.player.getLocation().getY());
    }

    public void update(double dt) {
        lastTime = currentTime;
        currentTime = System.currentTimeMillis();
        timeSinceLastFrame = currentTime - lastTime;
        //System.out.println("dt: " + dt);
        this.camera.update();
        for (int x = 0; x < activeLevel.getBlockGrid().getWidth(); x++) {
            for (int y = 0; y < activeLevel.getBlockGrid().getHeight(); y++) {
                Block b = activeLevel.getBlockGrid().getBlocks()[x][y];
                if (b.isCollidable()) {
                    if (b.getCollisionBox() == null) {
                        continue;
                    }
                }
            }
        }
        playerMovement();
        player.update(dt);
    }

    public void paintComponent() {
        this.clearBackground(width(), height());
        camera.draw();
        translate(150, 150);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        this.keysPressed.add(event.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent event) {
        this.keysPressed.remove(event.getKeyCode());
    }

    public void playerMovement() {
        if (this.keysPressed.contains(32)) {//SPACE
            if (player.isOnGround()) {
                player.jump();
            }
        }
        if (this.keysPressed.contains(87)) {//W
            player.directionY = -1;
        }
        if (this.keysPressed.contains(65)) {//A
            player.directionX = -1;
            player.setFlipped(false);
        }
        if (this.keysPressed.contains(83)) {//S
            Block b = player.getBlockAtLocation();
            player.directionY = 1;
        }
        if (this.keysPressed.contains(68)) {//D
            player.directionX = 1;
            player.setFlipped(true);
        }

        if (!this.keysPressed.contains(65) && !this.keysPressed.contains(68)) {
            player.directionX = 0;
        }

        if (!this.keysPressed.contains(83) && !this.keysPressed.contains(87)) {
            player.directionY = 0;
        }
    }

    public void loadBlockImages() {
        for (BlockTypes type : BlockTypes.values()) {
            if (type == BlockTypes.VOID) {
                continue;
            }

            System.out.println();
            imageBank.put(type.toString(), loadImage(type.getFilePath()));
        }
    }

    public void loadCharacterImages() {
        Image playerImg = loadImage("resources/images/characters/idle.png");
        imageBank.put("player", playerImg);
        imageBank.put("player_flipped", this.flipImageHorizontal(playerImg));
    }

    public Image getTexture(String textureName) {
        return imageBank.get(textureName);
    }

    private Image flipImageHorizontal(Image img) {
        BufferedImage bImg = (BufferedImage)img;
        AffineTransform tx = AffineTransform.getScaleInstance(-1.0, 1.0);
        tx.translate(-bImg.getWidth(null), 0.0);
        AffineTransformOp op = new AffineTransformOp(tx, 1);
        return op.filter(bImg, (BufferedImage)null);
    }
}
