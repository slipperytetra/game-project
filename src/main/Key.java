package main;

import level.Level;

public class Key extends Entity {
    public Key(Level level, Location loc) {
        super(EntityType.KEY, level, loc);

        setScale(0.5);
        setCanMove(false);
    }

    public void update(double dt) {
        if (getLevel().getPlayer().getCollisionBox().collidesWith(this.getCollisionBox())) {
            getLevel().getPlayer().setHasKey(true);
            destroy();
        }
    }

    @Override
    public void processMovement(double dt) {
        return;
    }
}
