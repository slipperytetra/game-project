package entity;

import level.Level;
import main.Game;
import main.Location;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Enemy extends EntityLiving {


    public Enemy(EntityType type, Level level, Location loc) {
        super(type, level, loc);
        setDamage(1);

        setHitboxColor(Color.red);
    }

    public void update(double dt) {
        super.update(dt);

        if (!isActive()) {
            return;
        }

        if (attackSearchTicks < ATTACK_SEARCH_COOLDOWN) {
            attackSearchTicks += 1 * dt;
        }

        if (attackSearchTicks >= ATTACK_SEARCH_COOLDOWN) { //Check if target is in range
            findTarget();

            if (getTarget() != null) { //If found
                getAttackTimer().start();
            }

            attackSearchTicks = 0;
        }
    }
}
