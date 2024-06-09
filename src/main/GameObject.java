package main;

import level.Level;

import java.awt.*;

public class GameObject {

    private boolean isActive;
    private boolean isCollidable;
    private double scale;
    private double width, height;
    private double hitboxWidth, hitboxHeight;

    private final Level level;
    private Location location;
    private CollisionBox collisionBox;
    private Color hitboxColor;

    public GameObject(Level level, Location loc) {
        this.level = level;
        this.location = loc;
        this.scale = 1.0;
        this.width = Game.BLOCK_SIZE;
        this.height = Game.BLOCK_SIZE;
        this.hitboxWidth = Game.BLOCK_SIZE;
        this.hitboxHeight = Game.BLOCK_SIZE;
        this.isActive = true;
        this.hitboxColor = Color.GREEN;
        this.setCollisionBox(new CollisionBox((int)loc.getX(), (int)loc.getY(), hitboxWidth, hitboxHeight));
    }

    public void update(double dt) {
    }

    public void render(Camera cam) {
        if (!isActive && !isOnScreen(cam)) {
            return;
        }
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        updateCollisionBox();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(double x, double y) {
        getLocation().setX(x);
        getLocation().setY(y);

        updateCollisionBox();
    }

    public boolean isCollidable() {
        return isCollidable && getCollisionBox() != null;
    }

    public void setCollidable(boolean collidable) {
        isCollidable = collidable;

        if (getCollisionBox() == null) {
            setCollisionBox(new CollisionBox((int) location.getX(), (int) location.getY(), Game.BLOCK_SIZE, Game.BLOCK_SIZE));
        }
    }

    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    public void setCollisionBox(CollisionBox collisionBox) {
        this.collisionBox = collisionBox;
    }

    public void updateCollisionBox() {
        if (getCollisionBox() == null) {
            return;
        }

        getCollisionBox().setLocation(getLocation().getX(), getLocation().getY());
        getCollisionBox().setSize(getHitboxWidth(), getHitboxHeight());
    }

    public double getWidth() {
        return width * getScale();
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height * getScale();
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHitboxWidth() {
        return hitboxWidth * getScale();
    }

    public void setHitboxWidth(double hitboxWidth) {
        this.hitboxWidth = hitboxWidth;
    }

    public double getHitboxHeight() {
        return hitboxHeight * getScale();
    }

    public void setHitboxHeight(double hitboxHeight) {
        this.hitboxHeight = hitboxHeight;
    }

    public Color getHitboxColor() {
        return hitboxColor;
    }

    public void setHitboxColor(Color hitboxColor) {
        this.hitboxColor = hitboxColor;
    }

    public boolean isOnScreen(Camera cam) {
        return getCollisionBox().collidesWith(cam.getCollisionBox());
    }

    public Level getLevel() {
        return level;
    }
}
