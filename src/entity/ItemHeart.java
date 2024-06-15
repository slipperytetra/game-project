package entity;

import level.Level;
import utils.Location;
import main.SoundType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ItemHeart extends EntityItem {

    private Timer timer;

    public ItemHeart(Level level, Location loc) {
        super(EntityType.HEART, level, loc);

        setPickupSound(SoundType.COLLECT_HEALTH);
        setScale(0.75);
    }

    @Override
    public void onPickup() {
        super.onPickup();

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player player = getLevel().getPlayer();
                int newHealth = player.getHealth() + 10;

                if (newHealth >= player.getMaxHealth()) {
                    player.setHealth(player.getMaxHealth());
                    timer.stop();
                } else {
                    player.setHealth(newHealth);
                }
            }
        });
        timer.start();
    }
}

