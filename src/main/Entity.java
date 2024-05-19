package main;

import block.Block;
import block.BlockClimbable;
import block.BlockLiquid;
import level.Level;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {

    private final Level level;
    private EntityType type;
    private Location loc;
    private CollisionBox collisionBox;

    private int health;
    private int maxHealth;
    private double scale;
    private boolean isActive;

    private double directionX, directionY;
    public double moveX, moveY;
    private Block leftBlockBelowEntity;
    private Block rightBlockBelowEntity;

    double speed = 384; // pixels per second
    double fallSpeedMultiplier = 1.009; // pixels per second

    double fallAccel;

    private boolean isFlipped;
    private boolean canMove;
    private Color hitboxColor;

    public Entity(EntityType type, Level level, Location loc) {
        this.type = type;
        this.level = level;
        loc.setY(loc.getY() - getHeight());
        this.loc = loc;
        this.isActive = true;
        this.scale = 2;
        this.maxHealth = 100;
        this.health = 100;
        this.canMove = true;
        this.hitboxColor = Color.YELLOW;
        this.collisionBox = new CollisionBox((int)loc.getX(), (int)loc.getY(), getWidth(), getHeight());
    }

    public void render(Camera cam) {
        double offsetX = getLocation().getX() + cam.centerOffsetX;
        double offsetY = getLocation().getY() + cam.centerOffsetY;

        getLevel().getManager().getEngine().drawImage(getIdleFrame(), offsetX, offsetY, getWidth(), getHeight());

        if (cam.showHitboxes) {
            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(offsetX, offsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }
    }

    public void update(double dt) {
        if (canMove()) {
            processMovement(dt);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean canMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public abstract void processMovement(double dt);

    public double getDirectionX() {
        return directionX;
    }

    public void setDirectionX(double directionX) {
        this.directionX = directionX;

        if (directionX > 0) {
            this.setFlipped(true);
        }

        if (directionX < 0) {
            this.setFlipped(false);
        }
    }

    public double getDirectionY() {
        return directionY;
    }

    public void setDirectionY(double directionY) {
        this.directionY = directionY;
    }

    public void moveX(double x) {
        if (x < 0) { //left
            for (int i = 0; i < Math.abs(x); i++) {
                Block leftBlock = getBlockAtLocation(-1, 1);
                //System.out.println("left: " + leftBlock.getType().toString());
                if (getCollisionBox().collidesWith(leftBlock.getCollisionBox()) && leftBlock.isCollidable()) {
                    return;
                }

                this.setLocation(getLocation().getX() - 1, getLocation().getY());
            }
        } else if (x >= 0) { //right
            for (int i = 0; i < x; i++) {
                Block rightBlock = getBlockAtLocation(1, 1);
                //System.out.println("right: " + rightBlock.getType().toString());
                if (getCollisionBox().collidesWith(rightBlock.getCollisionBox()) && rightBlock.isCollidable()) {
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
                Block blockAbove = getLevel().getBlockGrid().getBlockAt(tileX, tileY - 1);
                if (blockAbove.getCollisionBox() != null) {
                    if (getCollisionBox().collidesWith(blockAbove.getCollisionBox()) && blockAbove.isCollidable()) {
                        setDirectionY(0);
                        if (this instanceof Player) {
                            ((Player) this).setJumping(false);
                        }
                        return;
                    }
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

    public Block getBlockAtLocation() {
        int tileX = (int)((getLocation().getX() + 16) / Game.BLOCK_SIZE);
        int tileY = (int)((getLocation().getY() + 16) / Game.BLOCK_SIZE);

        return getLevel().getBlockGrid().getBlockAt(tileX, tileY);
    }

    public Block getBlockAtLocation(int tileOffsetX, int tileOffsetY) {
        int tileX = (int)((getLocation().getX() + 16) / Game.BLOCK_SIZE) + tileOffsetX;
        int tileY = (int)((getLocation().getY() + 16) / Game.BLOCK_SIZE) + tileOffsetY;

        return getLevel().getBlockGrid().getBlockAt(tileX, tileY);
    }

    public boolean isFalling() {
        return !isOnGround();
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
    public boolean isOnGround() {
        int tileLeftX = (int)(getLocation().getX() / Game.BLOCK_SIZE);
        int tileLeftY = (int)((getLocation().getY() + getHeight() - 3) / Game.BLOCK_SIZE);

        int tileRightX = (int)((getLocation().getX() + getWidth() - 5) / Game.BLOCK_SIZE);
        int tileRightY = (int)((getLocation().getY() + getHeight() - 3) / Game.BLOCK_SIZE);

        leftBlockBelowEntity = getLevel().getBlockGrid().getBlockAt(tileLeftX, tileLeftY + 1);
        rightBlockBelowEntity = getLevel().getBlockGrid().getBlockAt(tileRightX, tileRightY + 1);

        if (leftBlockBelowEntity instanceof BlockLiquid || rightBlockBelowEntity instanceof BlockLiquid) {
            setHealth(0);
        }

        if (leftBlockBelowEntity.getCollisionBox() != null) {
            if (getCollisionBox().collidesWith(leftBlockBelowEntity.getCollisionBox()) && leftBlockBelowEntity.isCollidable()) {
                return true;
            }
        }

        if (rightBlockBelowEntity.getCollisionBox() != null) {
            if (getCollisionBox().collidesWith(rightBlockBelowEntity.getCollisionBox()) && rightBlockBelowEntity.isCollidable()) {
                return true;
            }
        }

        return false;
    }

    public boolean isClimbing() {
        return getBlockAtLocation() instanceof BlockClimbable;
    }

    public Block getLeftBlockBelowEntity() {
        return leftBlockBelowEntity;
    }

    public Block getRightBlockBelowEntity() {
        return rightBlockBelowEntity;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public Level getLevel() {
        return level;
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(double x, double y) {
        getLocation().setX(x);
        getLocation().setY(y);

        updateCollisionBox();
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;

        updateCollisionBox();
    }

    public double getWidth() {
        return ((BufferedImage) getIdleFrame()).getWidth() * scale;
    }

    public double getHeight() {
        return ((BufferedImage)getIdleFrame()).getHeight() * scale;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health < 0) {
            health = 0;
        }

        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public boolean isCollidable() {
        return collisionBox != null;
    }

    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    public void setCollisionBox(CollisionBox cBox) {
        this.collisionBox = cBox;
        updateCollisionBox();
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
    }

    public Image getIdleFrame() {
        if (!isFlipped()) {
            return getLevel().getManager().getEngine().flipImageHorizontal(level.getManager().getEngine().getTexture(getType().toString().toLowerCase()));
        }

        return getLevel().getManager().getEngine().getTexture(getType().toString().toLowerCase());
    }

    public Color getHitboxColor() {
        return hitboxColor;
    }

    public void setHitboxColor(Color color) {
        this.hitboxColor = color;
    }

    public void updateCollisionBox() {
        getCollisionBox().setLocation(getLocation().getX(), getLocation().getY());
        getCollisionBox().setSize(getWidth(), getHeight());
    }

    public void destroy() {
        setActive(false);
    }
}
