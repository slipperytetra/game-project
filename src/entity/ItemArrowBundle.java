package entity;

import level.Level;
import level.item.InventoryItemSlot;
import level.item.ItemType;
import main.SoundType;
import utils.Location;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ItemArrowBundle extends EntityItem {

    public ItemArrowBundle(Level level, Location loc) {
        super(EntityType.ARROW_BUNDLE, level, loc);

        setPickupSound(SoundType.BACKPACK_CLOSE);
        setScale(1.25);
    }

    @Override
    public void onPickup() {
        super.onPickup();

        getLevel().getPlayer().getBackPack().addItem(ItemType.ARROW, 16);
    }
}

