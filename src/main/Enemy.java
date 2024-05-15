package main;

import level.Level;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Enemy extends Entity {

    private int damage;
    private int speed;
    private Player target;

    public Enemy(Level level, EntityType type, Location loc) {
        super(type, level, loc);
        this.damage = 1;
        this.speed = 1;

        setHitboxColor(Color.red);
    }

    @Override
    public void processMovement(double dt) {
        moveX = getDirectionX() * (speed * dt);
        moveY = getDirectionY() * (speed * dt);

        moveX(moveX);
        moveY(moveY);

        if (isFalling()) {
            if (fallAccel > 0) {
                fallAccel *= fallSpeedMultiplier;
                setDirectionY(1 * fallAccel);
            }
        } else {
            fallAccel = 1;
            setDirectionY(0);
        }
    }

    public void update(double dt) {
        super.update(dt);
    }

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player p) {
        this.target = p;
    }

    public abstract void attack();

    public abstract boolean hasAttackAnimation();

    public boolean canAttack() {
        return getTarget().getCollisionBox().collidesWith(getCollisionBox());
    }

    public int getDamage() {
        return damage;
    }

    public int getSpeed() {
        return speed;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}