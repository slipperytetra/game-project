package entity;

import level.Level;
import main.Location;

public class ItemKey extends EntityItem {

    public ItemKey(Level level, Location loc) {
        super(EntityType.KEY, level, loc);

        setPickupSound(level.getManager().getEngine().loadAudio("resources/sounds/keyObtained.wav"));
    }

    @Override
    public boolean canPickup() {
        return !getLevel().getPlayer().hasKey();
    }

    public void onPickup() {
        super.onPickup();
        getLevel().getPlayer().setHasKey(true);
    }
}


