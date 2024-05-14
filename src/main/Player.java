package main;

import block.Block;
import block.BlockClimbable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Player {
    Game game;
    public int hitboxWidth = 16;
    public int hitboxHeight = 29;
    private int scale = 2;

    private Location playerLoc;
    private boolean isMoving;
    private boolean isFlipped;
    private boolean isJumping;
    private boolean isAttacking;
    private boolean keyObtained;
    private boolean doorTouched;
    private boolean attackRegistered = false;
    private Rectangle collisionBox;

    private Timer animationTimer;
    private Timer startAnimationTimer;
    private int currentFrameIndex;

    double speed = 256; // pixels per second
    double fallSpeedMultiplier = 1.1; // pixels per second
    public double directionX, directionY;
    private double moveX, moveY;

    public double testLeftX, testLeftY, testRightX, testRightY;


    double fallAccel;

    Image gifImage;
    Image plantAttack;

    Image gifImage2;
    Image level1;

    public Player(Game game) {
        this.game = game;
        this.playerLoc = new Location(64.0, 64.0);
        this.hitboxWidth *= scale;
        this.hitboxHeight *= scale;
        this.collisionBox = new Rectangle((int)playerLoc.getX(), (int)playerLoc.getY(), hitboxWidth, hitboxHeight);

        init();
    }

    public void init() {
        gifImage = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
        plantAttack = Toolkit.getDefaultToolkit().createImage("resources/images/plantAttack.gif");

        gifImage2 = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
        level1 = Toolkit.getDefaultToolkit().createImage("resources/images/level1.gif");

        this.animationTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentFrameIndex = (currentFrameIndex + 1) % 4;
                System.out.println("Run " + currentFrameIndex);
            }
        });
        this.startAnimationTimer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                animationTimer.start();
            }
        });
    }

    public void update(double dt) {
        moveX = directionX * (speed * dt);
        moveY = directionY * (speed * dt);

        moveX(moveX);
        moveY(moveY);

        if (isFalling()) {
            if (fallAccel > 0) {
                fallAccel *= fallSpeedMultiplier;
                directionY = 1 * fallAccel;
            }
        } else {
            fallAccel = 1;
            directionY = 0;
        }

        animateCharacter();

        if (isMovingHorizontally()) {
            if (!this.animationTimer.isRunning()) {
                this.animationTimer.start();
            }
        } else {
            this.animationTimer.stop();
        }
    }

    public boolean isFalling() {
        return !isOnGround() && !canClimb();
    }

    public boolean isMoving() {
        return isMovingHorizontally() || isMovingVertically();
    }

    public boolean isMovingHorizontally() {
        return moveX != 0;
    }

    public boolean isMovingVertically() {
        return moveY != 0;
    }

    private void animateCharacter() {

    }

    public int getScale() {
        return scale;
    }

    private void jumpAnimation() {
        this.animationTimer.start();
        this.animationTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentFrameIndex == 4) {
                    animationTimer.stop();
                }

            }
        });
    }

    public Block getBlockAtLocation() {
        int tileX = (int)((getLocation().getX() + 16) / Game.BLOCK_SIZE);
        int tileY = (int)((getLocation().getY() + 16) / Game.BLOCK_SIZE);

        return game.activeLevel.getBlockGrid().getBlockAt(tileX, tileY);
    }

    public void moveX(double x) {
        int tileX = (int)((getLocation().getX() + 16) / Game.BLOCK_SIZE);
        int tileY = (int)((getLocation().getY() + 16) / Game.BLOCK_SIZE);
        if (x < 0) { //left
            for (int i = 0; i < Math.abs(x); i++) {
                Block leftBlock = game.activeLevel.getBlockGrid().getBlockAt(tileX - 1, tileY);
                if (collidesWith(leftBlock.getCollisionBox()) && leftBlock.isCollidable()) {
                    return;
                }

                this.setLocation(getLocation().getX() - 1, getLocation().getY());
            }
        } else if (x >= 0) { //right
            for (int i = 0; i < x; i++) {
                Block rightBlock = game.activeLevel.getBlockGrid().getBlockAt(tileX + 1, tileY);
                if (collidesWith(rightBlock.getCollisionBox()) && rightBlock.isCollidable()) {
                    return;
                }

                this.setLocation(getLocation().getX() + 1, getLocation().getY());
            }
        }
    }

    public void moveY(double y) {
        int tileX = (int)((getLocation().getX() + 16) / Game.BLOCK_SIZE);
        int tileY = (int)((getLocation().getY() + 16) / Game.BLOCK_SIZE);
        if (y < 0) { //up
            for (int i = 0; i < Math.abs(y); i++) {
                Block blockAbove = game.activeLevel.getBlockGrid().getBlockAt(tileX, tileY - 1);
                if (collidesWith(blockAbove.getCollisionBox()) && blockAbove.isCollidable()) {
                    break;
                }

                this.setLocation(getLocation().getX(), getLocation().getY() - 1);
            }
        } else if (y > 0) { //down
            for (int i = 0; i < y; i++) {
                if (isFalling()) {
                    this.setLocation(getLocation().getX(), getLocation().getY() + 1);
                }
            }
        }
    }

    public BufferedImage getIdleFrame() {
        if (!isFlipped()) {
            return game.flipImageHorizontal(game.getTexture("player"));
        }

        return game.getTexture("player");
    }

    public BufferedImage getRunFrame() {
        if (!isFlipped()) {
            return game.flipImageHorizontal(game.getTexture("player_run_" + currentFrameIndex));
        }

        return game.getTexture("player_run_" + currentFrameIndex);
    }

    public boolean canClimb() {
        return getBlockAtLocation() instanceof BlockClimbable;
    }
    public boolean isOnGround() {
        int tileLeftX = (int)(getLocation().getX() / Game.BLOCK_SIZE);
        int tileLeftY = (int)((getLocation().getY() + hitboxHeight - 3) / Game.BLOCK_SIZE);

        int tileRightX = (int)((getLocation().getX() + hitboxWidth) / Game.BLOCK_SIZE);
        int tileRightY = (int)((getLocation().getY() + hitboxHeight - 3) / Game.BLOCK_SIZE);

        Block leftBlockBelowPlayer = game.activeLevel.getBlockGrid().getBlockAt(tileLeftX, tileLeftY + 1);
        Block rightBlockBelowPlayer = game.activeLevel.getBlockGrid().getBlockAt(tileRightX, tileRightY + 1);

        this.testLeftX = leftBlockBelowPlayer.getLocation().getX();
        this.testLeftY = leftBlockBelowPlayer.getLocation().getY();
        this.testRightX = rightBlockBelowPlayer.getLocation().getX();
        this.testRightY = rightBlockBelowPlayer.getLocation().getY();

        //System.out.println("Left block: " + leftBlockBelowPlayer.getType().toString() + " (" + tileLeftX + ", " + tileLeftY + ")");
        //System.out.println("Right block: " + rightBlockBelowPlayer.getType().toString() + " (" + tileRightX + ", " + tileRightY + ")");

        if (leftBlockBelowPlayer.getCollisionBox() != null) {
            if (collidesWith(leftBlockBelowPlayer.getCollisionBox()) && leftBlockBelowPlayer.isCollidable()) {
                return true;
            }
        }

        if (rightBlockBelowPlayer.getCollisionBox() != null) {
            if (collidesWith(rightBlockBelowPlayer.getCollisionBox()) && rightBlockBelowPlayer.isCollidable()) {
                return true;
            }
        }

        return false;
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public int getWidth() {
        return getIdleFrame().getWidth() * scale;
    }

    public int getHeight() {
        return getIdleFrame().getHeight() * scale;
    }

    public boolean isFlipped() {
        return this.isFlipped;
    }

    public void setFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
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

    public void performAction(PlayerAction action) {
        if (action == PlayerAction.JUMP) {

        }
    }

    public boolean collidesWith(Rectangle box) {
        if (box == null) {
            return false;
        }

        double playerP1x = getCollisionBox().getLocation().getX();
        double playerP1y = getCollisionBox().getLocation().getY();
        double playerP2x = getCollisionBox().getMaxX();
        double playerP2y = getCollisionBox().getMaxY();

        double boxP1x = box.getLocation().getX();
        double boxP1y = box.getLocation().getY();
        double boxP2x = box.getMaxX();
        double boxP2y = box.getMaxY();


        if (playerP1x < boxP2x && playerP2x > boxP1x &&
                playerP2y > boxP1y && playerP1y < boxP2y ) {
            return true;
        }

        return false;
    }

    public Location getLocation() {
        return this.playerLoc;
    }

    public void setLocation(double x, double y) {
        this.getLocation().setX(x);
        this.getLocation().setY(y);
        updateCollisionBox();
    }

    public void updateCollisionBox() {
        this.collisionBox.setLocation((int) getLocation().getX(), (int)getLocation().getY());
    }
}
