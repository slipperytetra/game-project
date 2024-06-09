package entity;

import level.Level;
import main.GameEngine;
import main.Location;
import main.Texture;

public class EntityItem extends Entity {

    private GameEngine.AudioClip pickupSound;

    public EntityItem(EntityType type, Level level, Location loc) {
        super(type, level, loc);

        setScale(0.25);
        setCanMove(false);
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        if (getLevel().getPlayer().getCollisionBox().collidesWith(this.getCollisionBox())) {
            if (canPickup()) {
                onPickup();
            }
        }
    }

    public boolean canPickup() {
        return true;
    }

    public void onPickup() {
        if (hasPickupSound()) {
            getLevel().getManager().getEngine().playAudio(getPickupSound());
        }

        kill();
    }

    public GameEngine.AudioClip getPickupSound() {
        return pickupSound;
    }

    public void setPickupSound(GameEngine.AudioClip pickupSound) {
        this.pickupSound = pickupSound;
    }

    public boolean hasPickupSound() {
        return this.pickupSound != null;
    }

    @Override
    public void processMovement(double dt) {
        return;
    }
}
