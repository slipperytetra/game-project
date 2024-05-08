package main;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.*;

public class Player {
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
    private Rectangle collisionBox;

    public Player() {
        this.playerLoc = new Location(0.0, 0.0);
        this.collisionBox = new Rectangle((int)playerLoc.getX(), (int)playerLoc.getY(), width, height);
    }

    public void update() {

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

    public Location getLocation() {
        return this.playerLoc;
    }

    public void setLocation(double x, double y) {
        this.getLocation().setX(x);
        this.getLocation().setY(y);
        updateCollisionBox();
    }

    public void move(double x, double y) {
        this.setLocation(getLocation().getX() + x, getLocation().getY() + y);
    }

    public void moveX(double x) {
        this.setLocation(getLocation().getX() + x, getLocation().getY());
    }

    public void moveY(double y) {
        this.setLocation(getLocation().getX(), getLocation().getY() + y);
    }

    public double getJumpVelocity() {
        return -15.0;
    }

    public void updateCollisionBox() {
        this.collisionBox.setLocation((int) getLocation().getX(), (int)getLocation().getY());
    }

}
