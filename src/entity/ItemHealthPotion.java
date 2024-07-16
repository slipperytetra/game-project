package entity;

import level.Level;
import level.item.ItemType;
import main.SoundType;
import utils.Location;




public class ItemHealthPotion extends EntityItem {

    public ItemHealthPotion(Level level, Location loc) {
        super(EntityType.HEALTH_POTION, level, loc);

        setPickupSound(SoundType.BACKPACK_CLOSE);
        setScale(1.25);
    }

    @Override
    public void onPickup() {
        super.onPickup();

        getLevel().getPlayer().getBackPack().addItem(ItemType.POTION, 1);
    }
}

