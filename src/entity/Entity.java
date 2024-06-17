package entity;

import block.Block;
import block.BlockClimbable;
import block.BlockLiquid;
import level.Level;
import main.*;
import utils.CollisionBox;
import utils.Location;
import utils.Texture;
import utils.Vector;

import java.awt.*;
import java.util.List;

public abstract class Entity extends GameObject {

    private EntityType type;

    private final double GRAVITY = 32 * Game.BLOCK_SIZE;
    private final double FRICTION = 32;
    protected int health;
    private int maxHealth;
    private boolean isDead;
    public CollisionBox tempBoxX;

    private boolean isOnGround;

    public double moveX, moveY;

    private Vector direction;
    private Vector velocity;
    private List<GameObject> collisionsX;
    private List<GameObject> collisionsY;

    double speed = 384; // pixels per second / 12 blocks per second

    private boolean isFlipped;
    private boolean canMove;
    private boolean shouldRespawn;

    double MAX_SPEED = Game.BLOCK_SIZE * 8;
    double ACCELERATION = MAX_SPEED / 3;
    public double[] locs = new double[4];

    public Entity(EntityType type, Level level, Location loc) {
        super(level, loc);
        this.type = type;
        this.setScale(2);
        this.velocity = new Vector(0, 0);
        this.direction = new Vector(0, 0);
        this.shouldRespawn = false;
        this.health = 10;
        setMaxHealth(10);
        this.canMove = true;

        setHitboxWidth(getIdleFrame().getWidth());
        setHitboxHeight(getIdleFrame().getHeight());
    }

    public void render(Camera cam) {
        super.render(cam);
        cam.game.drawImage(getActiveFrame().getImage(), cam.toScreenX(getLocation().getX()), cam.toScreenY(getLocation().getY()), getWidth() * cam.getZoom(), getHeight() * cam.getZoom());

        if (cam.debugMode) {
            cam.game.changeColor(getHitboxColor());
            cam.game.drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() * cam.getZoom(), getCollisionBox().getHeight() * cam.getZoom());
        }
    }

    public void update(double dt) {
        super.update(dt);

        if (!isActive()) {
            return;
        }
        //System.out.println(getType().toString() + " called update(dt)");
        if (canMove()) {
            //processMovement(dt);
            move(dt);
        }

        if (getHealth() <= 0) {
            kill();
        }
    }

    public void move(double dt) {
        if (getType() != EntityType.PLAYER) {
            return;
        }

        //if (!isOnGround) {
            getVelocity().setY(getVelocity().getY() + (GRAVITY * dt));
        //}

        //


        if (getVelocity().getX() > 0) {
            getVelocity().setX(getVelocity().getX() - FRICTION);
            if (getVelocity().getX() < 0){
                getVelocity().setX(0);
            }
        } else if (getVelocity().getX() < 1) {
            getVelocity().setX(getVelocity().getX() + FRICTION);
            if (getVelocity().getX() > 0)  getVelocity().setX(0);
        }

        if (getVelocity().getX() < 0) {
            setFlipped(false);
        } else if (getVelocity().getX() > 0) {
            setFlipped(true);
        }
        moveEntX(getVelocity().getX() * dt);
        moveEntY(getVelocity().getY() * dt);

    }

    public void moveEntX(double distX) {
        double nextLocX = getLocation().getX() + distX;
        CollisionBox cBox = new CollisionBox(getCollisionBox().getLocation().getX() + distX - 1, getCollisionBox().getLocation().getY() - 1, getCollisionBox().getWidth() + 2, getCollisionBox().getHeight() + 2);

        this.collisionsX = getLevel().getQuadTree().query(this, cBox);
        //System.out.println(collisionsX.size());
        if (!collisionsX.isEmpty()) {
            for (GameObject gameObject : collisionsX) {
                if (!gameObject.isSolid()) {
                    continue;
                }

                double gObjX1 = gameObject.getCollisionBox().getLocation().getX() - 1;
                double gObjX2 = gameObject.getCollisionBox().getCorner().getX() + 1;

                double eObjX1 = cBox.getLocation().getX();
                double eObjX2 = cBox.getCorner().getX();

                boolean intersects = !((gObjX2 < eObjX1) || (eObjX2 < gObjX1));
                if (intersects) {
                    getVelocity().setX(0);
                    return;
                }
                /*
                double diffXLeft = gameObject.getCollisionBox().getCorner().getX() - cBox.getLocation().getX();
                double diffXRight = gameObject.getCollisionBox().getLocation().getX() - cBox.getCorner().getX();
                if (diffXLeft >= 1) {
                    if (getVelocity().getX() < 0) {
                        getVelocity().setX(0);
                        //System.out.println("DiffLeft: " + diffXLeft);
                        //System.out.println("colliding with " + gameObject + " above you");
                        return;
                    }
                }

                if (diffXRight <= 1) {
                    if (getVelocity().getX() > 0) {
                        getVelocity().setX(0);
                        //System.out.println("GameObject: " + gameObject.getCollisionBox().getLocation().getX() + " - " + cBox.getCorner().getX());
                        //System.out.println("DiffRight: " + diffXRight);
                        //System.out.println("colliding with " + gameObject + " below you");
                        return;
                    }
                }*/
            }
        }

        getLocation().setX(nextLocX);
        updateCollisionBox();
    }

    public void moveEntY(double distY) {
        double nextLocY = getLocation().getY() + distY;
        CollisionBox cBox = new CollisionBox(getCollisionBox().getLocation().getX() - 1, getCollisionBox().getLocation().getY() + distY - 1, getCollisionBox().getWidth() + 2, getCollisionBox().getHeight() + 2);
        tempBoxX = cBox;

        this.collisionsY = getLevel().getQuadTree().query(this, cBox);
        //System.out.println(collisionsX.size());
        if (!collisionsY.isEmpty()) {
            for (GameObject gameObject : collisionsY) {
                if (!gameObject.isSolid()) {
                    continue;
                }

                double gObjY1 = gameObject.getCollisionBox().getLocation().getY();
                double gObjY2 = gameObject.getCollisionBox().getCorner().getY();

                double eObjY1 = cBox.getLocation().getY();
                double eObjY2 = cBox.getCorner().getY();

                boolean intersects = !((gObjY2 < eObjY1) || (eObjY2 < gObjY1));
                if (intersects) {
                    getVelocity().setY(0);
                    isOnGround = true;
                    if (this instanceof Player player) {
                        player.setJumping(false);
                    }
                    return;
                }
                /*
                double diffYTop = gameObject.getCollisionBox().getCorner().getY() - cBox.getLocation().getY();
                double diffYBot = gameObject.getCollisionBox().getLocation().getY() - cBox.getCorner().getY();
                Camera cam = getLevel().getManager().getEngine().getCamera();

                if (gameObject.getLocation().getY() >= cBox.getCorner().getY()) {
                    locs[0] = cam.toScreenX(gameObject.getCollisionBox().getLocation().getX());
                    locs[1] = cam.toScreenY(gameObject.getCollisionBox().getLocation().getY());

                    locs[2] = cam.toScreenX(cBox.getCorner().getX());
                    locs[3] = cam.toScreenY(cBox.getCorner().getY());
                }
               // System.out.println(diffYTop);
                //System.out.println(diffYBot);
                if (diffYTop > 0 && diffYTop < 16) {
                    if (getVelocity().getY() < 0) {
                        getVelocity().setY(0);
                        System.out.println("colliding top");
                        //System.out.println("DiffLeft: " + diffYTop);
                        //System.out.println("colliding with " + gameObject + " above you");
                        return;
                    }
                }

                if (diffYBot > -16 && diffYBot < 0) {
                    if (getVelocity().getY() > 0) {
                        getVelocity().setY(0);
                        // System.out.println("GameObject: " + gameObject.getCollisionBox().getLocation().getY() + " - " + cBox.getCorner().getY());
                        //System.out.println("DiffRight: " + diffYBot);
                        System.out.println("colliding bot");
                        return;
                    }
                }


                if (diffYTop <= 1) {
                    if (getVelocity().getY() < 0) {
                        getVelocity().setY(1);
                        System.out.println("colliding top");
                        //System.out.println("DiffLeft: " + diffYTop);
                        //System.out.println("colliding with " + gameObject + " above you");
                        return;
                    }
                }*/
            }
        }
        isOnGround = false;

        getLocation().setY(nextLocY);
        updateCollisionBox();
    }


    public void kill() {
        this.setActive(false);
        this.setIsDead(true);
        if (this instanceof EntityLiving living && living.getAttackTimer().isRunning()) {
            living.getAttackTimer().stop();
        }
    }

    public boolean canMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public void processMovement(double dt) {
        //getVelocity().setY(getVelocity().getY() + (GRAVITY * dt));
        //List<GameObject> collisions = getLevel().getQuadTree().query(this);

        //System.out.println("collisions: " + collisions.size());



        if (getVelocity().getX() > 0) {
            getVelocity().setX(getVelocity().getX() - FRICTION);
            if (getVelocity().getX() < 0){
                getVelocity().setX(0);
            }

            /*
            for (GameObject gameObject : collisions) {
                if (gameObject.isCollidable()) {
                    //System.out.println(gameObject.toString() + " < colliding with");
                    if (gameObject.getCollisionBox().getCorner().getX() >= getCollisionBox().getLocation().getX()) {
                        setVelocity(0, getVelocity().getY());
                    }
                }
            }*/
        } else if (getVelocity().getX() < 0) {
            getVelocity().setX(getVelocity().getX() + FRICTION);
            if (getVelocity().getX() > 0)  getVelocity().setX(0);
        }

        //move();
        getLocation().setX(getLocation().getX() + (getVelocity().getX() * dt));
        getLocation().setY(getLocation().getY() + (getVelocity().getY() * dt));
        updateCollisionBox();

       // translate(dt, getVelocity().getX(), getVelocity().getY());


        /*
        // Apply friction to velocityY
        if (getVelocity().getY() > 0) {
            getVelocity().setY(getVelocity().getY() - FRICTION);
            if (getVelocity().getY() < 0) getVelocity().setY(0);
        } else if (getVelocity().getY() < 0) {
            getVelocity().setY(getVelocity().getY() + FRICTION);
            if (getVelocity().getY() > 0) getVelocity().setY(0);
        }*/

        /*
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
        }*/
    }

    public void translate(double dt, double movX, double movY) {
        System.out.println(movX + ", " + movY);
        getLocation().setX((getLocation().getX()) + movX * dt);
        getLocation().setY((getLocation().getY()) + movY * dt);
        updateCollisionBox();
    }

    public Vector getDirection() {
        return direction;
    }

    public void setDirection(Vector dir) {
        this.direction = dir;
    }

    public void setDirectionX(double x) {
        getDirection().setX(x);
    }

    public void setDirectionY(double y) {
        getDirection().setY(y);
    }


    /*
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
                if (isFalling() && !(this instanceof Projectile)) {
                    this.setLocation(getLocation().getX(), getLocation().getY() + 1);
                } else {
                    this.setLocation(getLocation().getX(), getLocation().getY() + 1);
                }
            }
        }
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

    public boolean isClimbing() {
        return getBlockAtLocation() instanceof BlockClimbable;
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
        return getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString().toLowerCase());
    }

    public void reset(){
        if (isShouldRespawn() && isDead()) {
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
}
