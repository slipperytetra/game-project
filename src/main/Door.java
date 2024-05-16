package main;

import level.Level;

public class Door extends Entity {
    public Door(Level level, Location loc) {
        super(EntityType.DOOR, level, loc);

        setScale(1.5);
        setCanMove(false);
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        if (canEnter(getLevel().getPlayer())) {

           if(getLevel().getManager().getEngine().keysPressed.contains(69)){
            getLevel().getManager().getEngine().setActiveLevel(getLevel().getManager().getLevels().get(getLevel().getNextLevel()));
        }}
    }

    @Override
    public void processMovement(double dt) {
        return;
    }

    public boolean canEnter(Player p) {
        return p.hasKey() && getCollisionBox().collidesWith(p.getCollisionBox());
    }
}
