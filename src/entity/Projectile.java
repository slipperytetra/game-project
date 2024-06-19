package entity;

import level.Level;
import level.ParticleTypes;
import main.Camera;
import main.Game;
import main.GameObject;
import main.SoundType;
import org.w3c.dom.Text;
import utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Projectile extends Entity {

    protected Entity shooter;
    private Vector velocity;
    private SoundType impactSound;

    private int damage;
    private boolean hasGravity;
    public final double GRAVITY = 9.8 * Game.BLOCK_SIZE; // gravity constant
    double angle;
    protected double rotationOffset;

    private ParticleTypes particleTrail;
    private double particleTimer;
    private double PARTICLE_FREQUENCY = 0.025;
    Random rand = new Random();

    public Projectile(Entity shooter, EntityType type, Level level, Location loc, double speed, double targetX, double targetY) {
        super(type, level, loc.clone());
        this.shooter = shooter;
        this.rotationOffset = 90;
        this.speed = speed;
        this.particleTrail = null;
        setMaxHealth(1);
        setHealth(1);
        setCanMove(true);
        setPersistent(true);

        setCollidable(false);
        setIsSolid(false);

        angle = -(Math.atan2((loc.getY() + (getHeight() / 2)) - targetY, targetX - (loc.getX() + (getWidth() / 2))));
        this.velocity = new Vector(speed * Math.cos(angle), speed * Math.sin(angle));
        this.hasGravity = true;
    }

    @Override
    public void update(double dt) {
        if (!isActive() || !canMove()) {
            return;
        }

        processMovement(dt);

        if (getHealth() <= 0 || !isInsideWorld()) {
            //System.out.println("Removed arrow");
            kill();
        }

        List<GameObject> collisions = getLevel().getQuadTree().query(this);
        for (GameObject gameObject : collisions) {
            if (!gameObject.isCollidable() || gameObject.equals(this) || gameObject.equals(getShooter())) {
                continue;
            }

            kill();
            if (hasImpactSound()) {
                getLevel().playSound(getImpactSound());
            }
            //setCanMove(false);

            if (gameObject instanceof EntityLiving living && living.getHealth() > 0) {
                living.damage(getDamage());
                getLevel().spawnParticle(ParticleTypes.IMPACT, getCenterX(), getCenterY());
            } else {
                for (int i = 0; i < rand.nextInt(5, 10); i++) {
                    getLevel().spawnParticle(ParticleTypes.DIRT, getCenterX() + rand.nextDouble(-4, 4) , getCenterY() + rand.nextDouble(-4, 4) - 2,
                            rand.nextDouble(-2, 2), rand.nextDouble(-1, 4));
                }
            }
        }

        if (hasTrail()) {
            if (particleTimer < PARTICLE_FREQUENCY) {
                particleTimer += 1 * dt;
            }

            if  (particleTimer >= PARTICLE_FREQUENCY) {
                Vector partVel = getVelocity().clone();
                partVel.normalise();
                partVel.multiply(-1);

                double partLocX = getParticleOffsetX(getLocation().getX() + (getWidth() / 2));
                double partLocY = getParticleOffsetY(getLocation().getY() + (getHeight() / 2));
                getLevel().spawnParticle(ParticleTypes.ARROW_TRAIL, partLocX, partLocY, partVel.getX(), partVel.getY());
                particleTimer = 0;
            }
        }
    }

    @Override
    public void render(Camera cam) {
        super.render(cam);

        if (cam.debugMode) {
            double x = cam.toScreenX(getLocation().getX() + (getWidth() / 2));
            double y = cam.toScreenY(getLocation().getY() + (getHeight() / 2));

            double x2 = cam.toScreenX(getLocation().getX() + getVelocity().getX());
            double y2 = cam.toScreenY(getLocation().getY() + getVelocity().getY());
            cam.game.drawLine(x, y, x2, y2);
        }
    }

    @Override
    public void processMovement(double dt) {
        if (!canMove()) {
            return;
        }

        getLocation().setX((getLocation().getX()) + getVelocity().getX() * dt);
        getLocation().setY((getLocation().getY()) + getVelocity().getY() * dt);
        updateCollisionBox();

        if (hasGravity) {
            getVelocity().setY(getVelocity().getY() + (GRAVITY * dt));
        } else {
            getVelocity().setY(getVelocity().getY() + (1 * dt));
        }

        angle = Math.atan2(getVelocity().getY(), getVelocity().getX());
    }

    @Override
    public boolean isFalling() {
        return false;
    }

    public Entity getShooter() {
        return shooter;
    }

    public void setShooter(Entity shooter) {
        this.shooter = shooter;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int amount) {
        this.damage = amount;
    }

    public boolean hasGravity() {
        return hasGravity;
    }

    public void setHasGravity(boolean hasGravity) {
        this.hasGravity = hasGravity;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector vec) {
        this.velocity = vec;
    }

    @Override
    public Texture getIdleFrame() {
        Texture texture = getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString().toLowerCase());
        texture.setFlipped(false);
        texture.setRotation(angle + Math.toRadians(rotationOffset));

        return texture;
    }

    public ParticleTypes getTrail() {
        return particleTrail;
    }

    public void setTrail(ParticleTypes particleTrail) {
        this.particleTrail = particleTrail;
    }

    public boolean hasTrail() {
        return particleTrail != null;
    }

    public void offsetTrajectory(double distance) {
        getLocation().setX(getLocation().getX() + distance * Math.cos(angle));
        getLocation().setY(getLocation().getY() + distance * Math.sin(angle));
    }

    private double getParticleOffsetX(double locX) {
        return locX - (getWidth() / 2) * Math.cos(angle);
    }

    private double getParticleOffsetY(double locY) {
        return locY - (getHeight() / 2) * Math.sin(angle);
    }

    private boolean isShooter(GameObject gameObject) {
        return gameObject instanceof EntityLiving && ((EntityLiving) gameObject).getType() != getShooter().getType();
    }

    public SoundType getImpactSound() {
        return impactSound;
    }

    public void setImpactSound(SoundType impactSound) {
        this.impactSound = impactSound;
    }

    public boolean hasImpactSound() {
        return impactSound != null;
    }
}
