package entity;

import block.Block;
import block.BlockClimbable;
import block.BlockLiquid;
import entity.utils.AttributeTypes;
import level.Level;
import main.*;
import utils.CollisionBox;
import utils.Location;
import utils.Texture;
import utils.Vector;

import java.util.HashMap;
import java.util.List;

public abstract class Entity extends GameObject {

    private EntityType type;

    private final double GRAVITY = 32 * Game.BLOCK_SIZE;

    private boolean isDead;
    public CollisionBox tempBoxX;

    private boolean isOnGround;

    private Vector velocity;
    private List<GameObject> collisionsX;
    private List<GameObject> collisionsY;
    private HashMap<AttributeTypes, Double> attributes;

    private boolean isFlipped;
    private boolean canMove;
    private boolean shouldRespawn;
    private boolean hasGravity;

    public double ACCELERATION;
    public double[] locs = new double[4];

    public Entity(EntityType type, Level level, Location loc) {
        super(level, loc);
        this.type = type;
        this.hasGravity = true;
        this.attributes = new HashMap<>();
        this.setScale(1);
        this.velocity = new Vector(0, 0);
        this.shouldRespawn = false;
        this.canMove = true;

        setHitboxWidth(getIdleFrame().getWidth());
        setHitboxHeight(getIdleFrame().getHeight());

        initAttributes();
    }

    public void initAttributes() {
        attributes.put(AttributeTypes.FRICTION, AttributeTypes.FRICTION.getDefaultValue());
        attributes.put(AttributeTypes.HEALTH, AttributeTypes.HEALTH.getDefaultValue());
        attributes.put(AttributeTypes.MAX_HEALTH, AttributeTypes.MAX_HEALTH.getDefaultValue());
        attributes.put(AttributeTypes.MOVEMENT_SPEED, AttributeTypes.MOVEMENT_SPEED.getDefaultValue());

        ACCELERATION = getAttributeValue(AttributeTypes.MOVEMENT_SPEED) / 2;
    }

    public void render(Camera cam) {
        super.render(cam);
        cam.game.drawImage(getActiveFrame().getImage(), cam.toScreenX(getLocation().getX()), cam.toScreenY(getLocation().getY()), getWidth(), getHeight());

        if (cam.debugMode) {
            cam.game.changeColor(getHitboxColor());
            cam.game.drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() , getCollisionBox().getHeight() );
        }
    }

    public void update(double dt) {
        super.update(dt);

        if (!isActive()) {
            return;
        }

        if (canMove()) {
            move(dt);
        }

        if (getHealth() <= 0 && !isDead) {
            kill();
        }
    }

    public void move(double dt) {
        //System.out.println(dt);
        if (hasGravity()) {
            getVelocity().setY(getVelocity().getY() + (GRAVITY * dt));
        }

        if (canClimb()) {
            if (getVelocity().getY() > 0) {
                getVelocity().setY(getVelocity().getY() - (getFriction() * 1.5 * dt));
                if (getVelocity().getY() < 0){
                    getVelocity().setY(0);
                }
            } else if (getVelocity().getY() < 1) {
                getVelocity().setY(getVelocity().getY() + (getFriction() * 1.5 * dt));
                if (getVelocity().getY() > 0)  getVelocity().setY(0);
            }
        }

        if (getVelocity().getX() > 0) {
            getVelocity().setX(getVelocity().getX() - (getFriction() * dt));
            if (getVelocity().getX() < 0){
                getVelocity().setX(0);
            }
        } else if (getVelocity().getX() < 1) {
            getVelocity().setX(getVelocity().getX() + (getFriction() * dt));
            if (getVelocity().getX() > 0)  getVelocity().setX(0);
        }

        if (getVelocity().getX() < 0) {
            setFlipped(false);
        } else if (getVelocity().getX() > 0) {
            setFlipped(true);
        }

        Camera cam = getLevel().getManager().getEngine().getCamera();
        locs[0] = cam.toScreenX(cam.getFocusPoint().getX() - 32);
        locs[1] = cam.toScreenY(cam.getFocusPoint().getY() - 32);

        locs[2] = cam.toScreenX(cam.getFocusPoint().getX() + 32);
        locs[3] = cam.toScreenY(cam.getFocusPoint().getY() + 32);

        moveEntX(getVelocity().getX() * dt);
        moveEntY(getVelocity().getY() * dt);
    }

    public void moveEntX(double distX) {
        if (this instanceof Player p && p.isAttacking()) {
            distX = 0;
        }
        double moveIncre = distX / (int) distX;
        for (int i = 0; i < Math.abs((int) distX); i++) {
            int num = 1;
            if (distX < 0) {
                num = -1;
            }

            double nextLocX = getLocation().getX() + (moveIncre * num);
            CollisionBox cBox = new CollisionBox(getCollisionBox().getLocation().getX() + num + (num), getCollisionBox().getLocation().getY() + num, getCollisionBox().getWidth(), getCollisionBox().getHeight());
            if (nextLocX <= 0 || nextLocX >= getLevel().getActualWidth() - getHitboxWidth()) {
                return;
            }

            collisionsX = getLevel().getQuadTree().retrieve(cBox);
            if (!collisionsX.isEmpty()) {
                for (GameObject gameObject : collisionsX) {
                    if (!gameObject.isSolid()) {
                        continue;
                    }

                    double gObjX1 = gameObject.getCollisionBox().getLocation().getX() - 2;
                    double gObjX2 = gameObject.getCollisionBox().getCorner().getX() + 2;

                    double eObjX1 = cBox.getLocation().getX();
                    double eObjX2 = cBox.getCorner().getX();

                    boolean intersects = !((gObjX2 < eObjX1) || (eObjX2 < gObjX1));
                    if (intersects) {
                        getVelocity().setX(0);
                        return;
                    }
                }
            }

            getLocation().setX(Math.round(nextLocX * 2) / 2.0f);
            updateCollisionBox();
        }
    }

    public void moveEntY(double distY) {
        double moveIncre = distY / (int) distY;
        for (int i = 0; i < Math.abs((int) distY); i++) {
            int num = 1;
            if (distY < 0) {
                num = -1;
            }

            float nextLocY = Math.round((getLocation().getY() + (moveIncre * num)) * 2) / 2.0f;
            CollisionBox cBox = new CollisionBox(getCollisionBox().getLocation().getX(), getCollisionBox().getLocation().getY() + num + (num), getCollisionBox().getWidth(), getCollisionBox().getHeight());
            if (nextLocY <= 0 || nextLocY >= getLevel().getActualHeight() - getHitboxHeight()) { //Make sure the object can't go outside the level
                return;
            }

            tempBoxX = cBox;

            this.collisionsY = getLevel().getQuadTree().retrieve(tempBoxX);
            if (!collisionsY.isEmpty()) {
                for (GameObject gameObject : collisionsY) {
                    if (!gameObject.isSolid()) {
                        continue;
                    }

                    double gObjY1 = gameObject.getCollisionBox().getLocation().getY() - 2;
                    double gObjY2 = gameObject.getCollisionBox().getCorner().getY() + 2;

                    double eObjY1 = tempBoxX.getLocation().getY();
                    double eObjY2 = tempBoxX.getCorner().getY();

                    boolean intersects = !((gObjY2 < eObjY1) || (eObjY2 < gObjY1));
                    if (intersects) {
                        getVelocity().setY(1);
                        if (gameObject.isSolid() && gameObject.getLocation().getY() > getLocation().getY()) {
                            isOnGround = true;
                        }
                        if (this instanceof Player player) {
                            player.setJumping(false);
                        }
                        if (gameObject instanceof BlockLiquid) {
                            this.setHealth(0);
                        }

                        return;
                    }
                }
            }

            isOnGround = false;

            getLocation().setY(nextLocY);
            updateCollisionBox();
        }
    }

    public double getFriction() {
        if (canClimb()) {
            return getAttributeValue(AttributeTypes.FRICTION) * 1.5;
        }

        return getAttributeValue(AttributeTypes.FRICTION);
    }


    public void kill() {
        this.setActive(false);
        this.setIsDead(true);

        if (this instanceof EntityLiving living && living.getAttackTimer().isRunning()) {
            living.getAttackTimer().stop();
        }
    }

    public boolean canMove() {
        return canMove && !isDead();
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }


    /*
    public void processMovement(double dt) {
         if (getVelocity().getX() > 0) {
            getVelocity().setX(getVelocity().getX() - FRICTION);
            if (getVelocity().getX() < 0){
                getVelocity().setX(0);
            }
        } else if (getVelocity().getX() < 0) {
            getVelocity().setX(getVelocity().getX() + FRICTION);
            if (getVelocity().getX() > 0)  getVelocity().setX(0);
        }

        getLocation().setX(getLocation().getX() + (getVelocity().getX() * dt));
        getLocation().setY(getLocation().getY() + (getVelocity().getY() * dt));
        updateCollisionBox();
    }*/

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
        return getVelocity().getX() != 0;
    }

    public boolean isMovingVertically() {
        return getVelocity().getY() != 0;
    }
    public boolean isOnGround() {
        return isOnGround;
    }

    public boolean canClimb() {
        return getBlockAtLocation() instanceof BlockClimbable;
    }

    public boolean hasGravity() {
        return hasGravity;
    }

    public void setHasGravity(boolean hasGravity) {
        this.hasGravity = hasGravity;
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
        return (int) getAttributeValue(AttributeTypes.HEALTH);
    }

    public void setHealth(double health) {
        if (health < 0) {
            health = 0;
        }

        if (health >= getMaxHealth()) {
            health = getMaxHealth();
        }

        setAttribute(AttributeTypes.HEALTH, health);
    }

    public int getMaxHealth() {
        return (int) getAttributeValue(AttributeTypes.MAX_HEALTH);
    }

    public void setMaxHealth(int maxHealth) {
        if (getHealth() > maxHealth) {
            setHealth(maxHealth);
        }

        setAttribute(AttributeTypes.MAX_HEALTH, maxHealth);
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
        return getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString().toLowerCase());
    }

    public void reset(){
        if (isShouldRespawn() && isDead()) {
            setActive(true);
            setHealth(getMaxHealth());
            isDead = false;
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

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector vector) {
        this.velocity = vector;
    }

    public void setVelocity(double velX, double velY) {
        getVelocity().setX(velX);
        getVelocity().setY(velY);
    }

    public boolean isDead() {
        return isDead || getHealth() <= 0;
    }

    public void setIsDead(boolean isDead) {
        this.isDead = isDead;
    }

    public List<GameObject> getCollisionsX() {
        return collisionsX;
    }

    public List<GameObject> getCollisionsY() {
        return collisionsY;
    }

    public HashMap<AttributeTypes, Double> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<AttributeTypes, Double> attributes) {
        this.attributes = attributes;
    }

    public double getAttributeValue(AttributeTypes attribute) {
        if (!hasAttribute(attribute)) {
            return 0;
        }

        return this.attributes.get(attribute);
    }

    public void setAttribute(AttributeTypes attribute, double value) {
        this.attributes.put(attribute, value);
    }

    public boolean hasAttribute(AttributeTypes attribute) {
        return this.attributes.containsKey(attribute);
    }
}
