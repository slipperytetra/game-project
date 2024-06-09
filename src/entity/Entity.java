package entity;

import block.Block;
import block.BlockClimbable;
import block.BlockLiquid;
import level.Level;
import main.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity extends GameObject {

    private EntityType type;

    protected int health;
    private int maxHealth;
    private Block blockBelowEntityLeft;
    private Block blockBelowEntityRight;

    private double directionX, directionY;
    public double moveX, moveY;

    double speed = 384; // pixels per second
    double fallSpeedMultiplier = 1.009; // pixels per second

    double fallAccel;

    private boolean isFlipped;
    private boolean canMove;
    private boolean shouldRespawn;

    public Entity(EntityType type, Level level, Location loc) {
        super(level, loc);
        this.type = type;
        this.setScale(2);

        this.shouldRespawn = false;
        this.health = 10;
        setMaxHealth(10);
        this.canMove = true;

        setHitboxWidth(getIdleFrame().getWidth());
        setHitboxHeight(getIdleFrame().getHeight());
    }

    public void render(Camera cam) {
        super.render(cam);
        getLevel().getManager().getEngine().drawImage(getActiveFrame().getImage(), cam.toScreenX(getLocation().getX()), cam.toScreenY(getLocation().getY()), getWidth() * cam.getZoom(), getHeight() * cam.getZoom());

        if (cam.debugMode) {
            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() * cam.getZoom(), getCollisionBox().getHeight() * cam.getZoom());
        }
    }

    public void update(double dt) {
        super.update(dt);

        if (!isActive()) {
            return;
        }
        //System.out.println(getType().toString() + " called update(dt)");
        if (canMove()) {
            processMovement(dt);
        }

        if (getHealth() <= 0) {
            kill();
        }
    }

    public void kill() {
        this.setActive(false);

        if (this instanceof EntityLiving) {
            ((EntityLiving) this).getAttackTimer().stop();
        }
    }

    public boolean canMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

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
                if (leftBlock == null || getCollisionBox().collidesWith(leftBlock.getCollisionBox()) && leftBlock.isCollidable()) {
                    return;
                }

                this.setLocation(getLocation().getX() - 1, getLocation().getY());
            }
        } else if (x >= 0) { //right
            for (int i = 0; i < x; i++) {
                Block rightBlock = getBlockAtLocation(1, 1);
                //System.out.println("right: " + rightBlock.getType().toString());
                if (rightBlock == null || getCollisionBox().collidesWith(rightBlock.getCollisionBox()) && rightBlock.isCollidable()) {
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
                if (blockAbove == null) {
                    return;
                }

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
            if (getBlockAtLocation(0, 2) == null) {
                setHealth(0);
                return;
            }

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
        int tileX = (int)((getCollisionBox().getLocation().getX()) / Game.BLOCK_SIZE);
        int tileY = (int)((getCollisionBox().getLocation().getY()) / Game.BLOCK_SIZE);

        int tileXCorner = (int)((getCollisionBox().getCorner().getX() - 1) / Game.BLOCK_SIZE);
        int tileYCorner = (int)((getCollisionBox().getCorner().getY() - getCollisionBox().getHeight()) / Game.BLOCK_SIZE);

        blockBelowEntityLeft = getLevel().getBlockGrid().getBlockAt(tileX, tileY + 2);
        blockBelowEntityRight = getLevel().getBlockGrid().getBlockAt(tileXCorner, tileYCorner + 2);
        Block directlyBelow = getLevel().getBlockGrid().getBlockAt((int) (getLocation().getX() + (getWidth() / 2)) / Game.BLOCK_SIZE, tileY + 2);



        if (blockBelowEntityLeft == null || blockBelowEntityRight == null) {
            return true;
        }

        if (directlyBelow instanceof BlockLiquid) {
            setHealth(0);
        }

        if (blockBelowEntityLeft.getCollisionBox() != null) {
            if (getCollisionBox().collidesWith(blockBelowEntityLeft.getCollisionBox()) && blockBelowEntityLeft.isCollidable()) {
                return true;
            }
        }

        if (blockBelowEntityRight.getCollisionBox() != null) {
            if (getCollisionBox().collidesWith(blockBelowEntityRight.getCollisionBox()) && blockBelowEntityRight.isCollidable()) {
                return true;
            }
        }

        return false;
    }

    public boolean isClimbing() {
        return getBlockAtLocation() instanceof BlockClimbable;
    }

    public Block getBlockBelowEntityLeft() {
        return blockBelowEntityLeft;
    }
    public Block getBlockBelowEntityRight() {
        return blockBelowEntityRight;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    @Override
    public double getWidth() {
        return getActiveFrame().getWidth() * getScale();
    }

    @Override
    public double getHeight() {
        return getActiveFrame().getHeight() * getScale();
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health < 0) {
            health = 0;
        }
        if (health >= maxHealth){
            health = maxHealth;
        }

        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        if(getHealth() > maxHealth) {
            setHealth(maxHealth);
        }

        this.maxHealth = maxHealth;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
    }

    public Texture getActiveFrame() {
        return getIdleFrame();
    }

    public Texture getIdleFrame() {
        return getLevel().getManager().getEngine().getTexture(getType().toString().toLowerCase());
    }

    public void reset(){
        if (isShouldRespawn()) {
            setActive(true);
            setHealth(maxHealth);
        }
    }

    public boolean isShouldRespawn() {
        return shouldRespawn;
    }

    public void setShouldRespawn(boolean shouldRespawn) {
        this.shouldRespawn = shouldRespawn;
    }

    public double getDistanceTo(Entity entity) {
        double x1 = getLocation().getX() + (getHitboxWidth() / 2);
        double y1 = getLocation().getY() + (getHitboxHeight() / 2);

        double x2 = entity.getLocation().getX() + (entity.getHitboxWidth() / 2);
        double y2 = entity.getLocation().getY() + (entity.getHitboxHeight() / 2);

        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }
    public double getDistanceToX(Entity entity) {
        double x1 = getLocation().getX() + (getHitboxWidth() / 2);
        double x2 = entity.getLocation().getX() + (entity.getHitboxWidth() / 2);
        return Math.abs(x1 - x2);
    }
    public double getDistanceToY(Entity entity) {
        double y1 = getLocation().getY() + (getHitboxHeight() / 2);
        double y2 = entity.getLocation().getY() + (entity.getHitboxHeight() / 2);
        return Math.abs(y1 - y2);
    }
}
