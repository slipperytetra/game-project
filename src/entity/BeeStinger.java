package entity;

import block.Block;
import level.Level;
import main.Game;
import main.Location;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class BeeStinger extends Entity {
    private BufferedImage image;
    private double speedX, speedY;
    private int damage;
    private Clip hitSound;

    // Constructor
    public BeeStinger(Level level, Location loc, double speedX, double speedY) {
        super(EntityType.BEE_STINGER, level, loc, 10, 10);
        Player player = level.getPlayer();
        if (player != null) {
            double dx = player.getLocation().getX() - loc.getX();
            double dy = player.getLocation().getY() - loc.getY();

            // Determine direction based on the result of subtraction
            double directionX = dx > 0 ? -1 : 1;

            double distance = Math.sqrt(dx * dx + dy * dy);
            this.speedX = speedX * directionX;
            this.speedY = speedY * (dy / distance);

            // Adjust the image based on the direction
            if (directionX < 0) {
                loadImage(true);
            } else {
                loadImage(false);
            }
        }
        this.damage = 10; // Set default damage value, adjust as needed
        loadSound();
    }

    // Load the image file and flip it if necessary
    private void loadImage(boolean flip) {
        try {
            File file = new File("resources/images/characters/bee/bee_stinger.png");
            if (!file.exists()) {
                System.err.println("Error: File not found at path " + file.getPath());
                return;
            }
            image = ImageIO.read(file);
            if (flip) {
                image = (BufferedImage) Game.flipImageHorizontal(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the sound file
    private void loadSound() {
        try {
            File soundFile = new File("resources/sounds/hitSound.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            hitSound = AudioSystem.getClip();
            hitSound.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Play sound if player is hit
    private void playHitSound() {
        if (hitSound != null) {
            hitSound.setFramePosition(0); // Rewind to the beginning
            hitSound.start();
        }
    }

    // Process movement of bee stinger
    @Override
    public void processMovement(double dt) {
        moveX = speedX * dt;
        moveY = speedY * dt;

        if (!moveXWithCheck(moveX) || !moveYWithCheck(moveY)) {
            destroy();
        } else {
            checkCollision();
        }
    }

    // Move horizontally with collision check
    private boolean moveXWithCheck(double x) {
        double deltaX = Math.signum(x); // Get the sign of the movement (left or right)
        double remainingMovement = Math.abs(x); // Remaining movement distance

        while (remainingMovement > 0) {
            double step = Math.min(1.0, remainingMovement); // Take a step of maximum 1 unit
            double newX = getLocation().getX() + step * deltaX; // Calculate the new X position after the step

            // Check if the new X position is within the grid bounds
            int gridX = (int) (newX / Game.BLOCK_SIZE);
            if (gridX < 0 || gridX >= getLevel().getBlockGrid().getWidth()) {
                System.out.println("Error: trying to move projectile outside of grid bounds");
                return false;
            }

            // Check for collision with block
            Block block = getBlockAtLocation((int) Math.signum(deltaX), 1);
            if (block != null && block.getCollisionBox() != null && getCollisionBox().collidesWith(block.getCollisionBox()) && block.isCollidable()) {
                System.out.println("Collision with block on the " + (deltaX < 0 ? "left" : "right"));
                return false; // Collision detected, stop movement
            }

            // Update position after the step
            this.setLocation(newX, getLocation().getY());
            remainingMovement -= step; // Reduce remaining movement
        }
        return true; // No collision detected for the entire movement
    }

    // Move vertically with collision check
    private boolean moveYWithCheck(double y) {
        double deltaY = Math.signum(y); // Get the sign of the movement (up or down)
        double remainingMovement = Math.abs(y); // Remaining movement distance

        while (remainingMovement > 0) {
            double step = Math.min(1.0, remainingMovement); // Take a step of maximum 1 unit
            double newY = getLocation().getY() + step * deltaY; // Calculate the new Y position after the step

            // Check if the new Y position is within the grid bounds
            int gridY = (int) (newY / Game.BLOCK_SIZE);
            if (gridY < 0 || gridY >= getLevel().getBlockGrid().getHeight()) {
                System.out.println("Error: trying to move projectile outside of grid bounds");
                return false;
            }

            // Check for collision with block
            Block block = getBlockAtLocation(1, (int) Math.signum(deltaY));
            if (block != null && block.getCollisionBox() != null && getCollisionBox().collidesWith(block.getCollisionBox()) && block.isCollidable()) {
                System.out.println("Collision with block " + (deltaY < 0 ? "above" : "below"));
                return false; // Collision detected, stop movement
            }

            // Update position after the step
            this.setLocation(getLocation().getX(), newY);
            remainingMovement -= step; // Reduce remaining movement
        }
        return true; // No collision detected for the entire movement
    }

    // Check collision with blocks and player
    private void checkCollision() {
        Block blockBelow = getLevel().getBlockGrid().getBlockAt((int) (getLocation().getX() / Game.BLOCK_SIZE), (int) ((getLocation().getY() + getHeight()) / Game.BLOCK_SIZE));
        if (blockBelow != null && blockBelow.isCollidable()) {
            System.out.println("Collision with block");
            destroy();
        } else {
            Player player = getLevel().getPlayer();
            if (player != null && getCollisionBox().collidesWith(player.getCollisionBox())) {
                System.out.println("Collision with player");
                player.setHealth(player.getHealth() - getDamage());
                playHitSound(); // Play sound when projectile hits the player
                destroy();
            }
        }
    }

    // Getter for damage
    public int getDamage() {
        return damage;
    }

    // Get active frame image
    @Override
    public Image getActiveFrame() {
        return image;
    }

    // Get width of the stinger
    @Override
    public double getWidth() {
        return image.getWidth() * getScale();
    }

    // Get height of the stinger
    @Override
    public double getHeight() {
        return image.getHeight() * getScale();
    }
}
