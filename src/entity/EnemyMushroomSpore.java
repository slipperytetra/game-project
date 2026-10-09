package entity;

import level.Level;
import level.ParticleTypes;
import main.Game;
import utils.Location;
import utils.Vector;

public class EnemyMushroomSpore extends Projectile {

    private EntityLiving target;

    public EnemyMushroomSpore(EnemyMushroomBoss boss, Level level, Location loc) {
        super(boss, EntityType.MUSHROOM_SPORE, level, loc, Game.BLOCK_SIZE * 15, boss.getCenterX(), boss.getCenterY());

        setScale(0.4);
        setHasGravity(false);
        setTiltTowardsAngle(false);

        setTarget(getLevel().getPlayer());
    }



    @Override
    public void update(double dt) {
        super.update(dt);

        if (getTarget() == null || getTarget().isDead()) {
            kill();
            return;
        }

        double velX = getTarget().getLocation().getX() - getLocation().getX();
        double velY = getTarget().getLocation().getY() - getLocation().getY();
        Vector vec = new Vector(velX, velY);
        vec.normalise();
        vec.multiply(128);
        setVelocity(vec);
        //System.out.println(getLocation().getX() + ", " + getLocation().getY());
        //System.out.println(getVelocity().getX() + ", " + getVelocity().getY());


        /*
        if (getTimeAlive() > 15) {
            kill();
        }*/
    }

    @Override
    public void kill() {
        this.setActive(false);
        this.setIsDead(true);

        getLevel().spawnParticle(ParticleTypes.MUSHROOM_SPORES, getLocation().getX(), getLocation().getY());
    }


    public EntityLiving getTarget() {
        return target;
    }

    public void setTarget(EntityLiving target) {
        this.target = target;
    }
}
