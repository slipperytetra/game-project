package entity;

import level.Level;
import utils.Location;
import main.SoundType;

public class EntityItem extends Entity {

    private SoundType pickupSound;

    public EntityItem(EntityType type, Level level, Location loc) {
        super(type, level, loc);

        setScale(1);
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
        return !getLevel().isEditMode();
    }

    public void onPickup() {
        if (hasPickupSound()) {
            getLevel().getManager().getEngine().getAudioBank().playSound(getPickupSound());
        }

        kill();
    }

    public SoundType getPickupSound() {
        return pickupSound;
    }

    public void setPickupSound(SoundType type) {
        this.pickupSound = type;
    }

    public boolean hasPickupSound() {
        return this.pickupSound != null;
    }

    @Override
    public void processMovement(double dt) {
        return;
    }
}
