package entity;

import level.Level;
import main.Camera;
import main.Location;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class goldCoin extends Entity {
    private List<BufferedImage> frames;
    private int currentFrame;
    private long lastFrameTime, frameDuration;
    private boolean collected;
    private Clip coinSound;

    // Constructor
    public goldCoin(Level level, Location loc) {
        super(EntityType.GOLD_COIN, level, loc, 18, 18);
        this.frames = new ArrayList<>();
        this.currentFrame = 0;
        this.lastFrameTime = 0;
        this.frameDuration = 100;
        this.collected = false;
        loadFrames(); // Load coin animation frames
        loadSound(); // Load sound effect
    }

    // Load coin animation frames
    private void loadFrames() {
        for (int i = 0; i < 9; i++) {
            String path = EntityType.GOLD_COIN.getFilePath() + "_frame" + i + ".png";
            BufferedImage frame = loadImage(path);
            if (frame != null) {
                frames.add(frame);
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

    // Load sound effect
    private void loadSound(){
        try{
            File soundFile = new File("resources/sounds/coin.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            coinSound = AudioSystem.getClip();
            coinSound.open(audioInputStream);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // Render the coin
    @Override
    public void render(Camera cam) {
        if (!collected) {
            updateAnimation(); // Update coin animation
            double offsetX = getLocation().getX() + cam.centerOffsetX;
            double offsetY = getLocation().getY() + cam.centerOffsetY;

            BufferedImage currentFrame = frames.get(this.currentFrame);
            getLevel().getManager().getEngine().drawImage(currentFrame, offsetX, offsetY, getWidth(), getHeight());

            if (cam.debugMode) {
                double hitBoxOffsetX = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
                double hitBoxOffsetY = getCollisionBox().getLocation().getY() + cam.centerOffsetY;

                getLevel().getManager().getEngine().changeColor(getHitboxColor());
                getLevel().getManager().getEngine().drawRectangle(hitBoxOffsetX, hitBoxOffsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
            }
        }
    }

    // Process movement (no movement for coins)
    @Override
    public void processMovement(double dt) {
        // No movement for coins
    }

    // Update coin animation
    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDuration) {
            currentFrame = (currentFrame + 1) % frames.size();
            lastFrameTime = currentTime;
        }
    }

    // Check if the coin is collected
    public boolean isCollected() {
        return collected;
    }

    // Collect the coin
    public void collect() {
        collected = true;
        if(coinSound != null){
            coinSound.setFramePosition(0);
            coinSound.start(); // Play coin sound
        }
    }
}