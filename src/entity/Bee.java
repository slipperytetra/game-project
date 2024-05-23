package entity;

import level.Level;
import main.Camera;
import main.Location;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bee extends Enemy {
    private List<BufferedImage> idleFrames;
    private List<BufferedImage> attackFrames;
    private List<BufferedImage> deathFrames;
    private int currentFrame;
    private long lastFrameTime, frameDuration = 100;
    private boolean isAttacking, isDying;
    private Timer attackTimer, deathTimer;
    private Random random;

    public Bee(Level level, Location loc) {
        super(level, EntityType.BEE, loc, 25, 27);
        this.idleFrames = new ArrayList<>();
        this.attackFrames = new ArrayList<>();
        this.deathFrames = new ArrayList<>();
        this.currentFrame = 0;
        this.lastFrameTime = 0;
        this.isAttacking = false;
        this.isDying = false;
        this.random = new Random();
        setMaxHealth(10);

        loadFrames(); // Load idle frames
        loadAttackFrames(); // Load attack frames
        loadDeathFrames(); // Load death frames
        setupAttackTimer(); // Setup attack timer
    }

    // Setup attack timer
    private void setupAttackTimer() {
        attackTimer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attack();
                resetAttackTimer();
            }
        });
        attackTimer.setRepeats(false);
        resetAttackTimer();
    }

    // Reset attack timer with random interval
    private void resetAttackTimer() {
        int interval = 3000 + random.nextInt(2000); // Random interval between 3000ms (3s) and 5000ms (5s)
        attackTimer.setInitialDelay(interval);
        attackTimer.restart();
    }

    // Load idle animation frames
    private void loadFrames() {
        for (int i = 0; i < 4; i++) {
            String path = EntityType.BEE.getFilePath() + "_frame" + i + ".png";
            BufferedImage frame = loadImage(path);
            if (frame != null) {
                idleFrames.add(frame);
            } else {
                System.err.println("Error: Could not load frame from path: " + path);
            }
        }

        if (idleFrames.isEmpty()) {
            System.err.println("Error: No frames loaded for Bee.");
        }
    }

    // Load death animation frames
    private void loadDeathFrames() {
        for (int i = 0; i < 8; i++) {
            String path = "resources/images/characters/bee/bee_death_frame" + i + ".png";
            BufferedImage frame = loadImage(path);
            if (frame != null) {
                deathFrames.add(frame);
            } else {
                System.err.println("Error: Could not load death frame from path: " + path);
            }
        }

        if (deathFrames.isEmpty()) {
            System.err.println("Error: No death frames loaded for Bee.");
        }
    }

    // Load attack animation frames
    private void loadAttackFrames() {
        for (int i = 0; i < 12; i++) {
            String path = "resources/images/characters/bee/bee_attack_frame" + i + ".png";
            BufferedImage frame = loadImage(path);
            if (frame != null) {
                attackFrames.add(frame);
            } else {
                System.err.println("Error: Could not load attack frame from path: " + path);
            }
        }

        if (attackFrames.isEmpty()) {
            System.err.println("Error: No attack frames loaded for Bee.");
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

    // Perform attack
    @Override
    public void attack() {
        if (isDying) return; // Do not attack if dying
        isAttacking = true;
        currentFrame = 0;
        shootProjectile();
    }

    // Shoot projectile at the player
    private void shootProjectile() {
        double projectileX = getLocation().getX();
        double projectileY = getLocation().getY() + getHeight() - 10; // Adjust 10 as needed to position at the bottom

        Location projectileLoc = new Location(projectileX, projectileY);
        double speedX = -100; // Adjust projectile speed as needed
        double speedY = 0; // Adjust projectile speed as needed
        BeeStinger stinger = new BeeStinger(getLevel(), projectileLoc, speedX, speedY);
        getLevel().addEntity(stinger);
    }

    // Update enemy state
    @Override
    public void update(double dt) {
        if (isDying) {
            updateAnimation(); // Ensure death animation updates during dying state
        } else {
            super.update(dt);
        }
    }

    // Render the enemy
    @Override
    public void render(Camera cam) {
        if (idleFrames.isEmpty() && attackFrames.isEmpty() && deathFrames.isEmpty()) {
            return; // Avoid rendering if no frames are loaded
        }

        updateAnimation(); // Update animation frame
        double offsetX = getLocation().getX() + cam.centerOffsetX;
        double offsetY = getLocation().getY() + cam.centerOffsetY;

        BufferedImage currentFrameImage;
        if (isDying) {
            currentFrameImage = deathFrames.get(Math.min(this.currentFrame, deathFrames.size() - 1));
        } else if (isAttacking) {
            currentFrameImage = attackFrames.get(this.currentFrame);
        } else {
            currentFrameImage = idleFrames.get(this.currentFrame);
        }
        getLevel().getManager().getEngine().drawImage(currentFrameImage, offsetX, offsetY, getWidth(), getHeight());

        // Render hitbox if enabled
        if (cam.debugMode) {
            double hitBoxOffsetX = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
            double hitBoxOffsetY = getCollisionBox().getLocation().getY() + cam.centerOffsetY;

            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(hitBoxOffsetX, hitBoxOffsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }

        // Draw health bar if health is less than max health
        if (getHealth() < getMaxHealth()) {
            cam.drawHealthBar(this, offsetX, offsetY - 50);
        }
    }


    // Update animation frame
    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDuration) {
            if (isDying) {
                currentFrame++;
                if (currentFrame >= deathFrames.size()) {
                    super.destroy();
                    return;
                }
            } else if (isAttacking) {
                currentFrame = (currentFrame + 1) % attackFrames.size();
                if (currentFrame == attackFrames.size() - 1) {
                    isAttacking = false; // Reset to idle after attack animation finishes
                    currentFrame = 0; // Reset frame index for idle animation
                }
            } else {
                currentFrame = (currentFrame + 1) % idleFrames.size();
            }
            lastFrameTime = currentTime;
        }
    }

    // Get the current frame image
    @Override
    public Image getActiveFrame() {
        if (isDying) {
            return deathFrames.isEmpty() ? null : deathFrames.get(Math.min(currentFrame, deathFrames.size() - 1));
        } else if (isAttacking) {
            return attackFrames.isEmpty() ? null : attackFrames.get(currentFrame);
        } else {
            return idleFrames.isEmpty() ? null : idleFrames.get(currentFrame);
        }
    }

    // Get the width of the current frame
    @Override
    public double getWidth() {
        if (isDying) {
            return deathFrames.isEmpty() ? 0 : deathFrames.get(Math.min(currentFrame, deathFrames.size() - 1)).getWidth() * getScale();
        } else if (isAttacking) {
            return attackFrames.isEmpty() ? 0 : attackFrames.get(currentFrame).getWidth() * getScale();
        } else {
            return idleFrames.isEmpty() ? 0 : idleFrames.get(currentFrame).getWidth() * getScale();
        }
    }

    // Get the height of the current frame
    @Override
    public double getHeight() {
        if (isDying) {
            return deathFrames.isEmpty() ? 0 : deathFrames.get(Math.min(currentFrame, deathFrames.size() - 1)).getHeight() * getScale();
        } else if (isAttacking) {
            return attackFrames.isEmpty() ? 0 : attackFrames.get(currentFrame).getHeight() * getScale();
        } else {
            return idleFrames.isEmpty() ? 0 : idleFrames.get(currentFrame).getHeight() * getScale();
        }
    }

    // Destroy the enemy
    @Override
    public void destroy() {
        if (!isDying) {
            isDying = true;
            isAttacking = false; // Stop attacking if dying
            currentFrame = 0;
            lastFrameTime = System.currentTimeMillis();
            deathTimer = new Timer(Math.toIntExact(deathFrames.size() * frameDuration), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Bee.super.destroy();
                }
            });
            deathTimer.setRepeats(false);
            deathTimer.start();
        }
    }

    // Override setHealth to handle destruction when health reaches zero
    @Override
    public void setHealth(int health) {
        super.setHealth(health);
        if (health <= 0 && !isDying) {
            destroy();
        }
    }
}
