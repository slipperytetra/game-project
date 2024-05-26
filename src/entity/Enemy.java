package entity;

import level.Level;
import main.Game;
import main.Location;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Enemy extends EntityLiving {

    public Enemy(Level level, EntityType type, Location loc, int hitboxWidth, int hitboxHeight) {
        super(type, level, loc, hitboxWidth, hitboxHeight);
        setDamage(1);

        setHitboxColor(Color.red);
    }

    public void update(double dt) {
        super.update(dt);

        if (isTargetInRange() && canAttack()) {
            attack();
        }
    }


    public boolean isTargetInRange() {
        if (getTarget() == null) {
            return false;
        }

        return getTarget().getCollisionBox().collidesWith(getCollisionBox());
    }

    // Determine if the enemy should face left based on the player's position
    protected boolean shouldFaceLeft() {
        Player player = getLevel().getPlayer();
        return player != null && player.getLocation().getX() < getLocation().getX();
    }
}
