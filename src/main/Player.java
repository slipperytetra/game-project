package main;

import block.Block;
import block.BlockClimbable;
import block.BlockTypes;
import level.Level;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Set;

public class Player extends Entity {
    private JProgressBar healthBar;

    private boolean isAttacking;
    private boolean keyObtained;
    private boolean doorTouched;
    private boolean attackRegistered = false;
    private boolean isJumping;
    //private Rectangle collisionBox;

    private Timer runAnimationTimer;
    private int runFrameIndex;

    public Timer jumpAnimationTimer;
    private int jumpFrameIndex;
    private double timeJumping;
    private double maxJumpTime = 0.35; //seconds

    Image gifImage;
    Image plantAttack;
    Image gifImage2;
    Image level1;

    public Player(Level level, Location loc) {
        super(EntityType.PLAYER, level, loc);

        setHitboxColor(Color.cyan);
        setMaxHealth(100);
        setHealth(getMaxHealth());
        setDirectionY(1);

        init();
    }

    public void init() {
        gifImage = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
        plantAttack = Toolkit.getDefaultToolkit().createImage("resources/images/plantAttack.gif");

        gifImage2 = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
        level1 = Toolkit.getDefaultToolkit().createImage("resources/images/level1.gif");

        this.healthBar = new JProgressBar(0, getMaxHealth());
        this.healthBar.setBounds(100, 25, 100, 10); // Adjust position and size as needed
        this.healthBar.setForeground(Color.RED); // Set the color
        this.healthBar.setValue(getMaxHealth()); // Set initial health
        this.healthBar.setStringPainted(true); // Show health value

        this.runAnimationTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runFrameIndex = (runFrameIndex + 1) % 4;
                //System.out.println("Run " + currentFrameIndex);
            }
        });
        this.jumpAnimationTimer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Jump: " + jumpFrameIndex);
                jumpFrameIndex = (jumpFrameIndex + 1) % 4;
            }
        });
    }

    public void update(double dt) {
        super.update(dt);
        animateCharacter();
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
        } else if (isFalling()) {
            if (fallAccel > 0) {
                fallAccel *= fallSpeedMultiplier;
                setDirectionY(1 * fallAccel);
            }
        } else {
            fallAccel = 1;
            setDirectionY(0);
        }
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    public void render(Camera cam) {
        double playerOffsetX = getLocation().getX() + cam.centerOffsetX;
        double playerOffsetY = getLocation().getY() + cam.centerOffsetY;
        Game game = getLevel().getManager().getEngine();
        if (isMovingVertically()) {
            game.drawImage(getFallFrame(), cam.zoom * playerOffsetX, cam.zoom * playerOffsetY, cam.zoom * getFallFrame().getWidth() * getScale(), cam.zoom * getFallFrame().getHeight() * getScale());
        } else if (isMovingHorizontally()) {
            game.drawImage(getRunFrame(), cam.zoom * playerOffsetX, cam.zoom * playerOffsetY, cam.zoom * getRunFrame().getWidth() * getScale(), cam.zoom * getRunFrame().getHeight() * getScale());
        } else {
            game.drawImage(getIdleFrame(), cam.zoom * playerOffsetX, cam.zoom * playerOffsetY, cam.zoom * getIdleFrame().getWidth() * getScale(), cam.zoom * getIdleFrame().getHeight() * getScale());
        }

        if (cam.showHitboxes) {
            game.changeColor(Color.magenta);

            double hitBoxOffsetX = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
            double hitBoxOffsetY = getCollisionBox().getLocation().getY() + cam.centerOffsetY;
            game.drawRectangle(getLeftBlockBelowEntity().getLocation().getX() + cam.centerOffsetX, getLeftBlockBelowEntity().getLocation().getY() + cam.centerOffsetY, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
            game.drawRectangle(getRightBlockBelowEntity().getLocation().getX() + cam.centerOffsetX, getRightBlockBelowEntity().getLocation().getY() + cam.centerOffsetY, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
            //game.drawRectangle(player.testLeftX + centerOffsetX, player.testLeftY + centerOffsetY, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
            //game.drawRectangle(player.testRightX + centerOffsetX, player.testRightY + centerOffsetY, Game.BLOCK_SIZE, Game.BLOCK_SIZE);

            game.changeColor(getHitboxColor());
            game.drawRectangle(cam.zoom * hitBoxOffsetX, cam.zoom * hitBoxOffsetY, cam.zoom * getWidth(), cam.zoom * getHeight());
            //game.drawRectangle(point1.getX() + centerOffsetX, point1.getY() + centerOffsetY, point2.getX() + centerOffsetX, point2.getY() + centerOffsetY);
            //game.drawRectangle(zoom * playerOffsetX, zoom * playerOffsetY, zoom * player.hitboxWidth, zoom * player.hitboxHeight);
            //game.drawRectangle(point1.getX() + centerOffsetX, point1.getY() + centerOffsetY, point2.getX() + centerOffsetX, point2.getY() + centerOffsetY);
        }
    }

    public void jump() {
        this.isJumping = true;
        this.jumpFrameIndex = 0;
        this.jumpAnimationTimer.start();
        this.timeJumping = 0;
    }

    @Override
    public boolean isFalling() {
        return !isOnGround() && !canClimb();
    }

    public void playerMovement(Set<Integer> keysPressed) {
        if (keysPressed.contains(32)) {//SPACE
            if (isOnGround()) {
                System.out.println("Jump!");
                jump();
            } else {
                System.out.println("Not on ground!");
            }
        }
        if (keysPressed.contains(87)) {//W
            Block b = getBlockAtLocation();
            if (b.getType() == BlockTypes.LADDER) {
                setDirectionY(-1);
            }
        }
        if (keysPressed.contains(65)) {//A
            setDirectionX(-calculateHorizontalMovement());
        }
        if (keysPressed.contains(83)) {//S
            Block b = getBlockAtLocation();
            setDirectionY(1);
        }
        if (keysPressed.contains(68)) {//D
            setDirectionX(calculateHorizontalMovement());
        }
    }

    public JProgressBar getHealthBar() {
        return healthBar;
    }

    public double calculateHorizontalMovement() {
        if (isMovingVertically()) {
            return 0.75;
        }

        return 1;
    }

    private void animateCharacter() {
        if (isMovingHorizontally() && !isMovingVertically()) {
            if (!this.runAnimationTimer.isRunning()) {
                this.runAnimationTimer.start();
            }
        } else {
            this.runAnimationTimer.stop();
        }
    }

    public Block getBlockAtLocation() {
        int tileX = (int)((getLocation().getX() + 16) / Game.BLOCK_SIZE);
        int tileY = (int)((getLocation().getY() + 16) / Game.BLOCK_SIZE);

        return getLevel().getBlockGrid().getBlockAt(tileX, tileY);
    }

    public BufferedImage getRunFrame() {
        if (!isFlipped()) {
            return getLevel().getManager().getEngine().flipImageHorizontal(getLevel().getManager().getEngine().getTexture("player_run_" + runFrameIndex));
        }

        return getLevel().getManager().getEngine().getTexture("player_run_" + runFrameIndex);
    }

    public BufferedImage getFallFrame() {
        if (!isFlipped()) {
            return getLevel().getManager().getEngine().flipImageHorizontal(getLevel().getManager().getEngine().getTexture("player_jump_" + runFrameIndex));
        }

        return getLevel().getManager().getEngine().getTexture("player_jump_" + runFrameIndex);
    }

    public boolean canClimb() {
        return getBlockAtLocation() instanceof BlockClimbable;
    }

    public boolean isAttacking() {
        return this.isAttacking;
    }

    public void setAttacking(boolean isAttacking) {
        this.isAttacking = isAttacking;
    }

    public boolean hasObtainedKey() {
        return this.keyObtained;
    }

    public void setKeyObtained(boolean keyObtained) {
        this.keyObtained = keyObtained;
    }

    public boolean isTouchingDoor() {
        return this.doorTouched;
    }

    public void setTouchingDoor(boolean doorTouched) {
        this.doorTouched = doorTouched;
    }

    public boolean hasRegisteredAttack() {
        return this.attackRegistered;
    }

    public void setAttackRegistered(boolean attackRegistered) {
        this.attackRegistered = attackRegistered;
    }
}
