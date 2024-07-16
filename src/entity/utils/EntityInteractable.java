package entity.utils;

import entity.EntityType;
import level.Level;
import main.Game;
import utils.Interactable;
import entity.Entity;
import utils.Location;

public abstract class EntityInteractable extends Entity implements Interactable {
    public EntityInteractable(EntityType type, Level level, Location loc) {
        super(type, level, loc);
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        interact();
    }

    @Override
    public void interact() {
        if (isInteractable()) {
            onInteract();
        }
    }

    @Override
    public abstract void onInteract();

    @Override
    public boolean isInteractable() {
        return getLevel().getPlayer() != null && getLevel().getPlayer().getLocation().distanceTo(this.getLocation()) <= getRange();
    }

    @Override
    public double getRange() {
        return getWidth() / 2;
    }
}
