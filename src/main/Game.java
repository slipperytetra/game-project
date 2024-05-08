package main;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import block.Block;
import block.BlockTypes;
import level.Level;
import level.LevelManager;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.Timer;

public class Game extends GameEngine {
    public static int BLOCK_SIZE = 32;
    public static int WIDTH = 600;
    public static int HEIGHT = 600;

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

        player.update();
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
                player.setVelocityUp(20);
            }
        }

        if (event.getKeyChar() == 'a') {
            player.setVelocityLeft(10);
            System.out.println("Left");
        }

        if (event.getKeyChar() == 'd') {
            player.setVelocityRight(10);
            System.out.println("Right");
        }

        if (event.getKeyChar() == 's') {
            //System.out.println("Down");
        }
    }

    public void paintComponent() {
        camera.draw();
    }
}
