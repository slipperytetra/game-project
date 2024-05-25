package entity;

import level.Level;
import main.Camera;
import main.Game;
import main.Location;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkullHead extends Enemy {
    private List<BufferedImage> frames;
    private int currentFrame;
    private long lastFrameTime, frameDuration, lastAttackTime, attackCooldown = 1500;
    private double upwardRange = 150, downwardRange = 1, speed = 0.5, startY;
    private Clip damageSound;
    private Random random;
    private double initialPhase;
    private double movementTime;

    public SkullHead(Level level, Location loc) {
        super(level, EntityType.SKULL_HEAD, loc, 30, 28);
        this.frames = new ArrayList<>();
        this.currentFrame = 0;
        this.lastFrameTime = 0;
        this.frameDuration = 100;

        this.random = new Random();
        this.startY = loc.getY();
        this.setDamage(25); // Set the damage amount

        this.lastAttackTime = 0;

        // Randomize initial phase for movement
        this.initialPhase = random.nextDouble() * 2 * Math.PI;

        loadFrames(); // Load animation frames
        loadSound(); // Load damage sound
    }

    // Load animation frames from files
    private void loadFrames() {
        for (int i = 0; i < 15; i++) {
            String path = EntityType.SKULL_HEAD.getFilePath() + "_frame" + i + ".png";
            BufferedImage frame = loadImage(path);
            if (frame != null) {
                frames.add((BufferedImage) main.Game.flipImageHorizontal(frame)); // Add flipped frames
            } else {
                System.err.println("Error: Could not load frame from path: " + path);
            }
        }
    }

    // Load image from file
    private BufferedImage loadImage(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Error: File not found at path " + path);
                return null;
            }
            return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Load damage sound
    private void loadSound() {
        try {
            File soundFile = new File("resources/sounds/hitSound.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            damageSound = AudioSystem.getClip();
            damageSound.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Process enemy movement
    @Override
    public void processMovement(double dt) {
        // Update movement time
        movementTime += dt * speed;

        // Calculate the Y position based on a sine wave for smooth up and down movement
        double moveY = Math.sin(movementTime + initialPhase) * (upwardRange - downwardRange) / 2 + (upwardRange - downwardRange) / 2;

        // Update the Y position
        setLocation(getLocation().getX(), startY - moveY);

        // Check for collision with player and attack if possible
        if (canAttack()) {
            attack();
        }
    }

    // Perform enemy attack
    @Override
    public void attack() {
        Player player = getLevel().getPlayer();
        if (player != null) {
            player.setHealth(player.getHealth() - getDamage());
            System.out.println("Player hit! Health remaining: " + player.getHealth());
            lastAttackTime = System.currentTimeMillis();
            playDamageSound(); // Play damage sound
        }
    }

    // Check if enemy can perform an attack
    @Override
    public boolean canAttack() {
        Player player = getLevel().getPlayer();
        long currentTime = System.currentTimeMillis();
        return player != null && player.getCollisionBox().collidesWith(this.getCollisionBox()) && (currentTime - lastAttackTime) >= attackCooldown;
    }

    // Play damage sound
    private void playDamageSound() {
        if (damageSound != null) {
            damageSound.setFramePosition(0); // Rewind to the beginning
            damageSound.start();
        }
    }

    // Render the enemy
    @Override
    public void render(Camera cam) {
        double offsetX = getLocation().getX() + cam.centerOffsetX;
        double offsetY = getLocation().getY() + cam.centerOffsetY;
        BufferedImage frame = (BufferedImage) getActiveFrame();

        // Flip the image if the enemy should face right
        if (!shouldFaceLeft()) {
            frame = (BufferedImage) Game.flipImageHorizontal(frame);
        }

        getLevel().getManager().getEngine().drawImage(frame, offsetX, offsetY, getWidth(), getHeight());

        if (cam.debugMode) {
            double hitBoxOffsetX = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
            double hitBoxOffsetY = getCollisionBox().getLocation().getY() + cam.centerOffsetY;

            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(hitBoxOffsetX, hitBoxOffsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }
    }



    // Get the current frame image
    @Override
    public Image getActiveFrame() {
        return frames.get(currentFrame);
    }

    // Get the width of the current frame
    @Override
    public double getWidth() {
        return frames.get(currentFrame).getWidth() * getScale();
    }

    // Get the height of the current frame
    @Override
    public double getHeight() {
        return frames.get(currentFrame).getHeight() * getScale();
    }

    // Destroy the enemy
    @Override
    public void destroy() {
        super.destroy();
    }
}
