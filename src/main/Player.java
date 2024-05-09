package main;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import block.Block;

import java.awt.*;

public class Player {
    Game game;
    private int width = 19;
    private int height = 29;
    private int scale = 2;

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
    public int movementX, movementY;
    private int moveCounter = 0;
    public double velLeft, velRight, velUp, velDown, velX, velY;

    double speed = 256; // pixels per second
    public int directionX, directionY;


    double maxSpeed = 350.0;
    public double accelX, accelY;
    double C = 0.5;

    double jumpTimer;

    Image gifImage;
    Image plantAttack;

    Image gifImage2;
    Image level1;

    public double pMoveX, pMoveY;

    public Player(Game game) {
        this.game = game;
        this.playerLoc = new Location(64.0, 64.0);
        this.width *= scale;
        this.height *= scale;
        this.collisionBox = new Rectangle((int)playerLoc.getX(), (int)playerLoc.getY(), width, height);

        init();
    }

    public void init() {
        gifImage = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
        plantAttack = Toolkit.getDefaultToolkit().createImage("resources/images/plantAttack.gif");

        gifImage2 = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
        level1 = Toolkit.getDefaultToolkit().createImage("resources/images/level1.gif");
    }

    public void update(double dt) {
        //pMoveX = getLocation().getX() + (directionX * (speed * dt));
        //System.out.println("MoveX: " + pMoveX);
        moveX(directionX * (speed * dt));
        //setLocation(pMoveX, getLocation().getY());
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
                if (collidesWith(leftBlock.getCollisionBox())) {
                    return;
                }

                this.setLocation(getLocation().getX() - 1, getLocation().getY());
            }
        } else if (x >= 0) { //right
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

    public void jump() {
        this.setJumping(true);
        this.jumpTimer = 0;
        accelY = -1000;
    }

    public void updateMovementX() {
        if (movementX != 0) {
            double moveX = movementX;
            if (!isOnGround()) {
                moveX = movementX * 1.5;
            }
            double locX = getLocation().getX();
            double locY = getLocation().getY();

            double moveAmount = (double) game.timeSinceLastFrame / moveX;
            locX += moveAmount;
            setLocation(locX, locY);
        }
    }

    public void updateMovementY() {
        if (movementY != 0) {
            double locX = getLocation().getX();
            double locY = getLocation().getY();

            double moveAmount = (double) game.timeSinceLastFrame / movementY;
            locY += moveAmount;
            setLocation(locX, locY);
        }
    }

    public void newMove(int direction, int steps) {
        if (direction == 0) { //left
            velX = velX - steps;
        } else if (direction == 1) { //right
            velX = velX + steps;
        } else if (direction == 2) { //up
            velY = velY - steps;
        } else if (direction == 3) { //down
            velY = velY + steps;
        }

        setLocation(getLocation().getX() + velX, getLocation().getY() + velY);
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

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isMoving() {
        return this.isMoving;
    }

    public void setMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public boolean isFlipped() {
        return this.isFlipped;
    }

    public void setFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
        this.jumpTimer = 0;
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
