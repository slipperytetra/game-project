package entity;

import level.Level;
import main.Location;

import java.awt.*;

public abstract class Enemy extends EntityLiving {

    public Enemy(Level level, EntityType type, Location loc, int hitboxWidth, int hitboxHeight) {
        super(type, level, loc, hitboxWidth, hitboxHeight);
        setDamage(1);

        setHitboxColor(Color.red);
    }

    public void update(double dt) {
        super.update(dt);

        if (isTargetInRange()) {
            attack();
        }
    }


    public boolean isTargetInRange() {
        if (getTarget() == null) {
            return false;
        }

        return getTarget().getCollisionBox().collidesWith(getCollisionBox());
    }
}