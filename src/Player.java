//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.*;
import java.awt.image.ImageObserver;

public class Player {
    private Location playerLoc = new Location(0.0, 0.0);
    private boolean isMoving;
    private boolean isFlipped;
    private boolean isJumping;
    private boolean isAttacking;
    private boolean keyObtained;
    private boolean doorTouched;
    private boolean attackRegistered = false;
    private final double JUMP_VELOCITY = -15.0;

    public Player() {
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

    public void update() {
    }
    public int playerHealth() {
        // Return the player's current health
        return health;
    }


    int health = 100;

    public void playerHealth(int amount){
        // Update player's health by the specified amount
        health += amount;
        // Ensure health doesn't exceed maximum value (e.g., 100)
        if (health > 100) {
            health = 100;
        }
    }




    public Location getLocation() {
        return this.playerLoc;
    }

    public void setLocation(double x, double y) {
        this.getLocation().setX(x);
        this.getLocation().setY(y);
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
}
