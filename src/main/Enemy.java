package main;

import level.Level;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy {

    private Level level;
    private EnemyType type;
    private Location loc;

    private CollisionBox collisionBox;
    //private Location collisionEndPoint;

    private int damage;
    private int health;
    private int speed;
    private boolean isFlipped;
    private int scale = 1;

    public Enemy(Level level, EnemyType type, Location loc, int damage, int health, int speed) {
        this.level = level;
        this.type = type;
        this.loc = loc;
        this.damage = damage;
        this.health = health;
        this.speed = speed;

        this.collisionBox = new CollisionBox((int)loc.getX(), (int)loc.getY(), getIdleFrame().getWidth(), getIdleFrame().getHeight());
        //this.collisionEndPoint = new Location(getCollisionBox().getX() + getCollisionBox().getWidth() * getScale(), getCollisionBox().getY() + getCollisionBox().getHeight() * getScale());
    }

    public Level getLevel() {
        return level;
    }

    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    public boolean canAttack(Player p) {
        return p.getCollisionBox().collidesWith(getCollisionBox());
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(double newX, double newY) {
        loc.setX(newX);
        loc.setY(newY);

        //this.collisionBox.setLocation((int)loc.getX(), (int)loc.getY());
        //this.collisionBox.setSize(getIdleFrame().getWidth(), getIdleFrame().getHeight());
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

    public void render(Camera cam) {
        double enemyOffsetX = getLocation().getX() + cam.centerOffsetX;
        double enemyOffsetY = getLocation().getY() + cam.centerOffsetY;

        getLevel().getManager().getEngine().drawImage(getIdleFrame(), enemyOffsetX, enemyOffsetY, getIdleFrame().getWidth() * getScale(), getIdleFrame().getHeight() * getScale());

        if (cam.showHitboxes) {
            getLevel().getManager().getEngine().changeColor(Color.cyan);
            getLevel().getManager().getEngine().drawRectangle(enemyOffsetX, enemyOffsetY, getCollisionBox().getWidth() * getScale(), getCollisionBox().getHeight() * getScale());
        }
    }
}