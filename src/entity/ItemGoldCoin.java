package entity;

import level.Level;
import main.Location;

public class ItemGoldCoin extends EntityItem {

    public ItemGoldCoin(Level level, Location loc) {
        super(EntityType.GOLD_COIN, level, loc);

        setPickupSound(getLevel().getManager().getEngine().loadAudio("resources/sounds/coin_pickup.wav"));
        setScale(1.5);
    }

    @Override
    public void onPickup() {
        super.onPickup();

        getLevel().getPlayer().incrementCoins(1);
    }
}

