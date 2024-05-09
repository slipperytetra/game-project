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
import java.util.HashMap;

public class Game extends GameEngine {
    public static int BLOCK_SIZE = 32;
    public static int WIDTH = 600;
    public static int HEIGHT = 600;
    public static int FRAME_RATE = 60;
    public long timeSinceLastFrame;
    public long lastTime;
    public long currentTime;
    public HashMap<BlockTypes, Image> imageBank;

    Player player;
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

        player.update(dt);
    }

    public void paintComponent() {
        this.clearBackground(width(), height());
        camera.draw();
        translate(150, 150);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyChar() == ' ') {
            if (player.isOnGround()) {
                player.jump();
            }
        }

        if (event.getKeyChar() == 'a') {
            player.accelX = -1000;
        }

        if (event.getKeyChar() == 'd') {
            player.accelX = 1000;
        }

        if (event.getKeyChar() == 's') {
            //System.out.println("Down");
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (event.getKeyChar() == 'a' || event.getKeyChar() == 'd') {
            player.accelX = 0;
            player.velX = 0;
        }
    }

    public void loadBlockImages() {
        for (BlockTypes type : BlockTypes.values()) {
            if (type == BlockTypes.VOID) {
                continue;
            }

            System.out.println();
            imageBank.put(type, loadImage(type.getFilePath()));
        }
    }

    public Image getBlockTexture(BlockTypes type) {
        if (type == BlockTypes.VOID) {
            return null;
        }

        return imageBank.get(type);
    }
}
