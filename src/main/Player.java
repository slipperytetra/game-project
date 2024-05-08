package main;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import block.Block;
import block.BlockVoid;

import java.awt.*;

public class Player {
    Game game;
    private int width = 32;
    private int height = 64;

    private Location playerLoc;
    private boolean isMoving;
    private boolean isFlipped;
    private boolean isJumping;
    private boolean isAttacking;
    private boolean keyObtained;
    private boolean doorTouched;
    private boolean attackRegistered = false;
    private final double JUMP_VELOCITY = -15.0;
    int jumpSpeed = 15;
    private Rectangle collisionBox;
    private int moveCooldown;
    private int moveSpeed = 8;
    private int moveCounter = 0;
    private double velLeft, velRight, velUp, velDown, velX, velY;

    int jumpTimer;

    Image gifImage;
    Image plantAttack;

    Image gifImage2;
    Image level1;

    public Player(Game game) {
        this.game = game;
        this.playerLoc = new Location(64.0, 64.0);
        this.collisionBox = new Rectangle((int)playerLoc.getX(), (int)playerLoc.getY(), width, height);

        init();
    }

    public void init() {
        gifImage = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
        plantAttack = Toolkit.getDefaultToolkit().createImage("resources/images/plantAttack.gif");

        gifImage2 = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
        level1 = Toolkit.getDefaultToolkit().createImage("resources/images/level1.gif");
    }

    public void update() {
        if (velLeft > 0) {
            velLeft -= 0.5;
            if (velLeft < 0) {
                velLeft = 0;
            }
        }

        if (velRight > 0) {
            velRight -= 0.5;
            if (velRight < 0) {
                velRight = 0;
            }
        }
        if (velUp > 0) {
            velUp -= 0.5;
            if (velUp < 0) {
                velUp = 0;
            }
        }
        /*if (velDown > 0) {
            velDown -= 0.75;
            if (velDown < 0) {
                velDown = 0;
            }
        }*/

        if (!isOnGround()) {
            if (velDown < 20) {
                velDown += 1;
            }
        } else {
            velDown = 0;
        }

        velX = -velLeft + velRight;
        velY = -velUp + velDown;
        moveX(velX);
        moveY(velY);
        //System.out.println("velLeft: " + -velLeft);
        //System.out.println("velRight: " + velRight);
        //System.out.println("velX: " + velX);
        System.out.println("velY: " + velY);
        //moveY(velY);
        /*for (int i = 0; i < 5; i++) {
            if (!isOnGround()) {
                move(PlayerAction.MOVE_DOWN, 1);
            }
        }

        if (isJumping) {
            move(PlayerAction.MOVE_UP, 16);
            jumpTimer++;
            if (jumpTimer > 10) {
                isJumping = false;
            }
        }*/

        //System.out.println(getLocation().toString());
        //System.out.println(getTileLocation().toString());
    }

    public void setVelocityLeft(double vel) {
        this.velLeft = vel;
    }

    public void setVelocityRight(double vel) {
        this.velRight = vel;
    }

    public void setVelocityUp(double vel) {
        this.velUp = vel;
    }

    public void setVelocityDown(double vel) {
        this.velDown = vel;
    }

    public boolean isOnGround() {
        int tileLeftX = (int)(getLocation().getX() / Game.BLOCK_SIZE);
        int tileLeftY = (int)((getLocation().getY()) / Game.BLOCK_SIZE);

        int tileRightX = (int)((getLocation().getX() + 24) / Game.BLOCK_SIZE);
        int tileRightY = (int)((getLocation().getY()) / Game.BLOCK_SIZE);

        Block leftBlockBelowPlayer = game.activeLevel.getBlockGrid().getBlockAt(tileLeftX, tileLeftY + 1);
        Block rightBlockBelowPlayer = game.activeLevel.getBlockGrid().getBlockAt(tileRightX, tileRightY + 1);
        if (leftBlockBelowPlayer == null || rightBlockBelowPlayer == null) {
            return false;
        }

        if (leftBlockBelowPlayer.getCollisionBox() != null && collidesWith(leftBlockBelowPlayer.getCollisionBox())) {
            return true;
        }

        if (rightBlockBelowPlayer.getCollisionBox() != null && collidesWith(rightBlockBelowPlayer.getCollisionBox())) {
            return true;
        }

        return false;
    }

    public void doAction(PlayerAction action) {
        if (action == PlayerAction.JUMP) {
            if (!isJumping && isOnGround()) {
                this.isJumping = true;
                jumpTimer = 0;
            }
        }
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public boolean isMoving() {
        return this.isMoving;
    }

    public void setMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public boolean isFlipping() {
        return this.isFlipped;
    }

    public void setFlipping(boolean isFlipped) {
        this.isFlipped = isFlipped;
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
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

    public void move(PlayerAction movement, int steps) {
        int tileLeftY = (int)((getLocation().getY() + 16) / Game.BLOCK_SIZE);
        if (movement == PlayerAction.MOVE_LEFT) {
            for (int i = 0; i < steps; i++) {
                int tileLeftX = (int)((getLocation().getX() + 16) / Game.BLOCK_SIZE);
                Block leftBlock = game.activeLevel.getBlockGrid().getBlockAt(tileLeftX - 1, tileLeftY);
                if (collidesWith(leftBlock.getCollisionBox())) {
                    break;
                }

                this.setLocation(getLocation().getX() - 1, getLocation().getY());
            }
        } else if (movement == PlayerAction.MOVE_RIGHT) {
            for (int i = 0; i < steps; i++) {
                int tileRightX = (int)((getLocation().getX() + 16) / Game.BLOCK_SIZE);
                Block rightBlock = game.activeLevel.getBlockGrid().getBlockAt(tileRightX + 1, tileLeftY);
                if (collidesWith(rightBlock.getCollisionBox())) {
                    break;
                }

                this.setLocation(getLocation().getX() + 1, getLocation().getY());
            }
        } else if (movement == PlayerAction.MOVE_UP) {
            for (int i = 0; i < steps; i++) {
                int tileAboveX = (int)((getLocation().getX() + (width / 2)) / Game.BLOCK_SIZE);
                int tileAboveY = (int)((getLocation().getY() + height) / Game.BLOCK_SIZE);
                Block aboveBlock = game.activeLevel.getBlockGrid().getBlockAt(tileAboveX, tileAboveY + 1);
                if (collidesWith(aboveBlock.getCollisionBox())) {
                    break;
                }

                this.setLocation(getLocation().getX(), getLocation().getY() - 1);
            }
        } else if (movement == PlayerAction.MOVE_DOWN) {
            for (int i = 0; i < steps; i++) {
                if (!isOnGround()) {
                    this.setLocation(getLocation().getX(), getLocation().getY() + 1);
                }
            }
        }
    }

    public void moveX(double x) {
        int tileX = (int)((getLocation().getX() + 16) / Game.BLOCK_SIZE);
        int tileY = (int)((getLocation().getY() + 16) / Game.BLOCK_SIZE);
        if (x < 0) { //left
            for (int i = 0; i < Math.abs(x); i++) {
                Block leftBlock = game.activeLevel.getBlockGrid().getBlockAt(tileX - 1, tileY);
                if (collidesWith(leftBlock.getCollisionBox())) {
                    return;
                }

                this.setLocation(getLocation().getX() - 1, getLocation().getY());
            }
        } else if (x > 0) { //right
            for (int i = 0; i < x; i++) {
                Block rightBlock = game.activeLevel.getBlockGrid().getBlockAt(tileX + 1, tileY);
                if (collidesWith(rightBlock.getCollisionBox())) {
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
                if (collidesWith(blockAbove.getCollisionBox())) {
                    break;
                }

                this.setLocation(getLocation().getX(), getLocation().getY() - 1);
            }
        } else if (y >= 0) { //down
            for (int i = 0; i < y; i++) {
                if (isOnGround()) {
                    break;
                }

                this.setLocation(getLocation().getX(), getLocation().getY() + 1);
            }
        }
    }

    /*
    public void moveY(double y) {
        int tileLeftY = (int)((getLocation().getY()) / Game.BLOCK_SIZE);

        if (y > 0) {
            int tileLeftX = (int)((getLocation().getX() / Game.BLOCK_SIZE) - 1);
            Block leftBlock = game.activeLevel.getBlockGrid().getBlockAt(tileLeftX, tileLeftY);
        }
        this.setLocation(getLocation().getX(), getLocation().getY() + y);
    }*/

    public double getJumpVelocity() {
        return -15.0;
    }

    public void updateCollisionBox() {
        this.collisionBox.setLocation((int) getLocation().getX(), (int)getLocation().getY());
    }

}
