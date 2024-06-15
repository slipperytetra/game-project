package entity;

import level.Level;
import utils.Location;
import main.SoundType;

public class ItemKey extends EntityItem {

    public ItemKey(Level level, Location loc) {
        super(EntityType.KEY, level, loc);

        setPickupSound(SoundType.COLLECT_KEY);
    }

    public void onPickup() {
        super.onPickup();

        getLevel().getPlayer().setHasKey(true);
    }
}


