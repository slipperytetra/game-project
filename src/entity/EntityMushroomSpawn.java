package entity;

import level.Level;
import level.ParticleTypes;
import main.Camera;
import main.SoundType;
import utils.CollisionBox;
import utils.Location;
import utils.Texture;

import java.util.Random;

public class EntityMushroomSpawn extends EntityLiving {

    private boolean isDormant;
    private long startTime;
    private double dormantScale;

    private double sporeTicks, sporeCount = 5;
    private double sporeSpawnTicks, sporeSpawnTimer = 0.25;

    private Random rand;

    public EntityMushroomSpawn(Level level, Location loc) {
        super(EntityType.MUSHROOM_SPAWN, level, loc);

        setHitSound(SoundType.GENERIC_HIT);
        setAttackSound(null);
        setScale(1);
        this.startTime = System.currentTimeMillis();
        this.rand = new Random();
        isDormant = true;
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        if (isDormant()) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;
            double cycleProgress = (double) (elapsedTime % 3000) / 3000;
            double angle = cycleProgress * 2 * Math.PI;
            dormantScale = 0.95 + (1.05 - 0.95) * (0.5 * (Math.sin(angle) + 1));
        } else {
            if (sporeTicks < sporeCount) {
                sporeTicks += 1 * dt;

                if (sporeSpawnTicks < sporeSpawnTimer) {
                    sporeSpawnTicks += 1 * dt;
                } else {
                    getLevel().spawnParticle(ParticleTypes.MUSHROOM_SPORES, getCenterX() + getWidth()/2, getCenterY() + getHeight()/2, rand.nextDouble(-1, 1), rand.nextDouble(-1.5, 0.25));
                }
            }
        }
    }

    @Override
    public void render(Camera cam) {
        int diffX = (int) (getIdleFrame().getWidth() - (getIdleFrame().getWidth() * dormantScale));
        int diffY = (int) (getIdleFrame().getHeight() - (getIdleFrame().getHeight() * dormantScale));
        double offsetX = cam.toScreenX(getLocation().getX() + diffX);
        double offsetY = cam.toScreenY(getLocation().getY() + diffY);


        getLevel().getManager().getEngine().drawImage(getActiveFrame().getImage(), offsetX, offsetY, getWidth(), getHeight());

        if (cam.debugMode) {
            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }
    }

    @Override
    public Texture getIdleFrame() {
        if (isDormant()) {
            return getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString());
        }

        return getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString() + "_dead");
    }

    public boolean isDormant() {
        return isDormant;
    }

    public void setDormant(boolean dormant) {
        isDormant = dormant;
    }

    public double getScale() {
        if (isDormant()) {
            return 1 * dormantScale;
        }

        return super.getScale();
    }

    @Override
    public Texture getActiveFrame() {
        return getIdleFrame();
    }

    @Override
    public void kill() {
        setDormant(false);
        setInvulnerable(true);
    }

    @Override
    public int getHealth() {
        if (!isDormant()) {
            return getMaxHealth();
        }

        return super.getHealth();
    }
}
