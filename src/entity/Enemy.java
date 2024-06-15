package entity;

import level.Level;
import utils.Location;

import java.awt.*;

public abstract class Enemy extends EntityLiving {


    public Enemy(EntityType type, Level level, Location loc) {
        super(type, level, loc);
        setDamage(1);

        setHitboxColor(Color.red);
        setCollidable(true);
    }

    public void update(double dt) {
        super.update(dt);

        if (!isActive()) {
            return;
        }

        if (attackSearchTicks < ATTACK_SEARCH_COOLDOWN) {
            //System.out.println("search ticks: " + attackSearchTicks + "/" + ATTACK_SEARCH_COOLDOWN);
            attackSearchTicks += 1 * dt;
        }

        if (attackSearchTicks >= ATTACK_SEARCH_COOLDOWN && !getAttackTimer().isRunning()) { //Check if target is in range
            //System.out.println(getType() + " is searching...");
            findTarget();

            if (getTarget() != null) { //If found
                getAttackTimer().start();
            }

            attackSearchTicks = 0;
        }
    }
}
