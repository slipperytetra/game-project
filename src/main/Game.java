package main;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import block.Block;
import level.Level;
import level.LevelManager;

import java.awt.event.KeyEvent;

public class Game extends GameEngine {
    public static int BLOCK_SIZE = 32;
    public static int WIDTH = 600;
    public static int HEIGHT = 600;
    public static int FRAME_RATE = 60;
    public long timeSinceLastFrame;
    public long lastTime;
    public long currentTime;

    Player player;
    LevelManager lvlManager;
    Level activeLevel;
    Camera camera;

    public Game() {
    }

    public static void main(String[] args) {
        createGame(new Game());
    }

    public void update(double dt) {
        lastTime = currentTime;
        currentTime = System.currentTimeMillis();
        timeSinceLastFrame = currentTime - lastTime;

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

        player.update(currentTime, lastTime, timeSinceLastFrame);
    }

    public void init() {
        this.setWindowSize(WIDTH, HEIGHT);
        this.player = new Player(this);
        this.camera = new Camera(this, player);
        this.lvlManager = new LevelManager(this);
        this.activeLevel = lvlManager.DEMO_2;
        this.player.setLocation(activeLevel.getSpawnPoint().getX(), activeLevel.getSpawnPoint().getY());
        System.out.println("Starting X position: " + this.player.getLocation().getX());
        System.out.println("Starting Y position: " + this.player.getLocation().getY());
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyChar() == ' ') {
            if (player.isOnGround()) {
                player.setJumping(true);
            }
        }

        if (event.getKeyChar() == 'a') {
            player.movementX = -4;
        }

        if (event.getKeyChar() == 'd') {
            player.movementX = 4;
        }

        if (event.getKeyChar() == 's') {
            //System.out.println("Down");
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (event.getKeyChar() == 'a' || event.getKeyChar() == 'd') {
            player.movementX = 0;
        }
    }

    public void paintComponent() {
        camera.draw();
    }
}
