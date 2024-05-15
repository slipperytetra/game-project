package main;

import level.Level;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy {

    private Level level;
    private EnemyType type;
    private Location loc;
    private Rectangle collisionBox;

    private int damage;
    private int health;
    private int speed;
    private boolean isFlipped;
    private int scale = 1;

    public Enemy(Level level, EnemyType type, int damage, int health, int speed) {
        this.level = level;
        this.type = type;
        this.damage = damage;
        this.health = health;
        this.speed = speed;
        this.loc = new Location(0, 0);

        this.collisionBox = new Rectangle((int)loc.getX(), (int)loc.getY(), getIdleFrame().getWidth() * getScale(), getIdleFrame().getHeight() * getScale());
    }

    public Level getLevel() {
        return level;
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public boolean canAttack(Player p) {
        //if (p.getLocation().isBetween())
        return false;
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(double newX, double newY) {
        loc.setX(newX);
        loc.setY(newY);
    }

    public int getScale() {
        return scale;
    }

    public EnemyType getType() {
        return type;
    }

    public BufferedImage getIdleFrame() {
        if (!isFlipped()) {
            return getLevel().getManager().getEngine().flipImageHorizontal(level.getManager().getEngine().getTexture(getType().toString().toLowerCase()));
        }

        return getLevel().getManager().getEngine().getTexture(getType().toString().toLowerCase());
    }

    public int getHeight() {
        return getIdleFrame().getHeight();
    }

    public int getWidth() {
        return getIdleFrame().getWidth();
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
    }

    public int getDamage() {
        return damage;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}