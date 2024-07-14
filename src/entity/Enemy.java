package entity;

import level.Level;
import utils.Location;
import utils.Texture;

import java.awt.*;

public abstract class Enemy extends EntityLiving {

    private boolean isHostile;

    public Enemy(EntityType type, Level level, Location loc) {
        super(type, level, loc);
        setDamage(1);

        setHitboxColor(Color.red);
        setCollidable(true);
        this.isHostile = true;
    }

    public void update(double dt) {
        super.update(dt);

        if (!isActive()) {
            return;
        }

        if (isHostile()) {
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

    @Override
    public Texture getActiveFrame() {
        return getIdleFrame();
    }

    public boolean isHostile() {
        return isHostile;
    }

    public void setHostile(boolean hostile) {
        isHostile = hostile;
    }
}
