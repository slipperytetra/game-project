package entity;

import level.Level;
import utils.Location;
import main.SoundType;

public class ItemGoldCoin extends EntityItem {

    public ItemGoldCoin(Level level, Location loc) {
        super(EntityType.GOLD_COIN, level, loc);

        setPickupSound(SoundType.COLLECT_COIN);
        setScale(1.5);
    }

    @Override
    public void onPickup() {
        super.onPickup();

        getLevel().getPlayer().incrementCoins(1);
    }
}

