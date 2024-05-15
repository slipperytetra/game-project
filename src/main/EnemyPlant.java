package main;

import level.Level;

public class EnemyPlant extends Enemy {
    public EnemyPlant(Level level, Location loc) {
        super(level, EntityType.PLANT_MONSTER, loc);

        setDamage(5);
        setMaxHealth(100);
        setScale(1);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        setTarget(getLevel().getPlayer());
        attack();
    }

    @Override
    public void attack() {
        //System.out.println("Looking to attack...");
        if (getTarget() != null) {
            if (canAttack()) {
                getTarget().setHealth(getTarget().getHealth() - getDamage());
            }
        }
    }

    @Override
    public boolean hasAttackAnimation() {
        return true;
    }
}
