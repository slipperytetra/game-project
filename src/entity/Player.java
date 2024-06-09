package entity;

import block.BlockClimbable;
import block.BlockTypes;
import level.Level;
import level.ParticleTypes;
import main.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Set;

public class Player extends EntityLiving {
    private JProgressBar healthBar;
    private boolean isJumping;
    private boolean hasKey;

    private double keyPressTimer;
    private double KEY_PRESS_COOLDOWN = 0.05;

    private double timeJumping;
    private double maxJumpTime = 0.25;

    private double runParticleTimer;
    private double RUN_PARTICLE_FREQUENCY = 0.075;
    private int coins;

    public Player(Level level, Location loc) {
        super(EntityType.PLAYER, level, loc);

        setHitboxColor(Color.cyan);
        setAttackCooldown(0.5);
        setMaxHealth(100);
        setDamage(5);
        setHealth(getMaxHealth());
        setDirectionY(1);
        init();
    }

    public void init() {
        setHitSound(getLevel().getManager().getEngine().loadAudio("resources/sounds/hitSound.wav"));
        setAttackSound(getLevel().getManager().getEngine().loadAudio("resources/sounds/attackSound.wav"));

        this.healthBar = new JProgressBar(0, getMaxHealth());
        this.healthBar.setBounds(100, 25, 100, 10); // Adjust position and size as needed
        this.healthBar.setForeground(Color.RED); // Set the color
        this.healthBar.setValue(getMaxHealth()); // Set initial health
        this.healthBar.setStringPainted(true); // Show health value
    }

    public void update(double dt) {
        super.update(dt);
        if (keyPressTimer < KEY_PRESS_COOLDOWN) {
            keyPressTimer += 1 * dt;
        } else {
            //System.out.println("Test");
            keyPressTimer = 0;
        }

        if (runParticleTimer < RUN_PARTICLE_FREQUENCY) {
            runParticleTimer += 1 * dt;
        }

        if  (runParticleTimer >= RUN_PARTICLE_FREQUENCY) {
            if(isMovingHorizontally() && isOnGround()){
                double partVelX = 0.75;
                double partVelY = -0.5;
                if (isFlipped()) {
                    partVelX *= -1;
                }

                getLevel().spawnParticle(ParticleTypes.CLOUD, getLocation().getX(), getLocation().getY() + 48, partVelX, partVelY);
                runParticleTimer = 0;
            }
        }
    }

    public boolean hasKey() {
        return hasKey;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }


    @Override
    public void processMovement(double dt) {
        moveX = getDirectionX() * (speed * dt);
        moveY = getDirectionY() * (speed * dt);

        moveX(moveX);
        moveY(moveY);

        if (isJumping()) {
            setDirectionY(-1.5);
            timeJumping += 1 * dt;

            if (timeJumping > maxJumpTime) {
                this.setJumping(false);
                this.setDirectionY(0);
                this.timeJumping = 0;
            }
            return;
        }

        if (!getLevel().isEditMode()) {
            if (isFalling() && !canClimb()) {
                if (fallAccel > 0) {
                    fallAccel *= fallSpeedMultiplier;
                    setDirectionY(1 * fallAccel);
                }
            } else {
                fallAccel = 1;
                setDirectionY(0);
            }
        }
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    @Override
    public void render(Camera cam) {
        double playerOffsetX = cam.toScreenX(getLocation().getX());
        double playerOffsetY = cam.toScreenY(getLocation().getY());
        Game game = getLevel().getManager().getEngine();

        if (isAttacking()) {
            playerOffsetX = playerOffsetX - 31;
            playerOffsetY = playerOffsetY - 8;
        }

        game.drawImage(getActiveFrame().getImage(), playerOffsetX * cam.getZoom(), playerOffsetY * cam.getZoom(), getWidth() * cam.getZoom(), getHeight() * cam.getZoom());

        if (cam.debugMode) {
            game.changeColor(Color.magenta);

            if (getBlockBelowEntityLeft() != null) {
                game.drawRectangle((getBlockBelowEntityLeft().getLocation().getX() + cam.centerOffsetX) * cam.getZoom(), (getBlockBelowEntityLeft().getLocation().getY() + cam.centerOffsetY) * cam.getZoom(), Game.BLOCK_SIZE * cam.getZoom(), Game.BLOCK_SIZE * cam.getZoom());
            }

            if (getBlockBelowEntityRight() != null) {
                game.drawRectangle((getBlockBelowEntityRight().getLocation().getX() + cam.centerOffsetX) * cam.getZoom(), (getBlockBelowEntityRight().getLocation().getY() + cam.centerOffsetY) * cam.getZoom(), Game.BLOCK_SIZE * cam.getZoom(), Game.BLOCK_SIZE * cam.getZoom());
            }

            game.changeColor(getHitboxColor());
            game.drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() * cam.getZoom(), getCollisionBox().getHeight() * cam.getZoom());
        }
    }

    public void jump() {
        this.isJumping = true;
        this.getJumpFrame().setFrameIndex(0);
        this.timeJumping = 0;
    }

    public void playerMovement(Set<Integer> keysPressed) {
        if (keyPressTimer >= KEY_PRESS_COOLDOWN) {
            if (keysPressed.contains(32)) {//SPACE
                if (!isJumping() && !isAttacking() && (isOnGround() || canClimb() || getLevel().isEditMode())) {
                    jump();
                }
            }
            if (keysPressed.contains(87)) {//W
                if (canClimb() && getBlockAtLocation(0, -1).getType() != BlockTypes.VOID) {
                    setDirectionY(-1);
                }
            }
            if (keysPressed.contains(65)) {//A
                setDirectionX(-calculateHorizontalMovement());
            }
            if (keysPressed.contains(83)) {//S
                if (canClimb() || getLevel().isEditMode()) {
                    setDirectionY(1);
                }
            }
            if (keysPressed.contains(68)) {//D
                setDirectionX(calculateHorizontalMovement());
            }
            if (keysPressed.contains(81)) {
                if (getAttackTicks() >= getAttackCooldown()) {
                    if (getAttackSound() != null) {
                        getLevel().getManager().getEngine().playAudio(getAttackSound());
                    }
                    getAttackFrame().setFrameIndex(0);
                    setAttackTicks(0);

                    findTarget();

                    if (getTarget() != null) {
                        attack();
                    }
                }
            }
            keyPressTimer = 0;
        }
    }

    @Override
    public void findTarget() {
        for (Entity entity : getLevel().getEntities()) {
            if (!entity.isActive()) {
                continue;
            }

            if (entity instanceof EntityLiving && entity.getType() != this.getType()) {
                if (getCollisionBox().collidesWith(entity.getCollisionBox())) {
                    setTarget((EntityLiving) entity);
                    return;
                }
            }
        }

        setTarget(null);
    }

    public JProgressBar getHealthBar() {
        return healthBar;
    }

    @Override
    public double getWidth() {
        if (isAttacking()) {
            return 50 * getScale();
        }

        return getIdleFrame().getWidth() * getScale();
    }

    @Override
    public double getHeight() {
        if (isAttacking()) {
            return 37 * getScale();
        }

        return getIdleFrame().getHeight() * getScale();
    }

    @Override
    public void kill() {
        getLevel().reset();
    }

    public double calculateHorizontalMovement() {
        if (isAttacking()) {
            return 0;
        }

        if (isMovingVertically()) {
            return 0.75;
        }

        return 1;
    }

    public Texture getRunFrame() {
        return getLevel().getManager().getEngine().getTexture("player_run");
    }

    public Texture getFallFrame() {
        return getLevel().getManager().getEngine().getTexture("player_fall");
    }

    public TextureAnimated getJumpFrame() {
        return (TextureAnimated) getLevel().getManager().getEngine().getTexture("player_jump");
    }

    @Override
    public Texture getActiveFrame() {
        Texture texture = getIdleFrame();

        if (isAttacking()) {
            texture = getAttackFrame();
        } else if (isJumping()) {
            texture = getJumpFrame();
        } else if (isFalling()) {
            texture = getFallFrame();
        } else if (isMovingHorizontally()) {
            texture = getRunFrame();
        }

        texture.setFlipped(!isFlipped());

        return texture;
    }

    public boolean canClimb() {
        return getBlockAtLocation() instanceof BlockClimbable;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void incrementCoins(int amount) {
        this.coins += amount;
    }

    @Override
    public int getHealth() {
        if (getLevel().isEditMode()) {
            return getMaxHealth();
        }

        return health;
    }

}
