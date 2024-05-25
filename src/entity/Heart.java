package entity;

import level.Level;
import main.GameEngine;
import main.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Heart extends Entity{
    public Heart( Level level, Location loc) {
        super(EntityType.HEART, level, loc,28,28);
        health = getLevel().getManager().getEngine().loadAudio("resources/sounds/health.wav");

    }

    private Timer timer;
    private GameEngine.AudioClip health;



    @Override
    public void processMovement(double dt) {

    }
    @Override
    public void update(double dt) {
        if (getLevel().getPlayer().getCollisionBox().collidesWith(this.getCollisionBox())) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
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
            getLevel().getManager().getEngine().playAudio(health);
            timer.start();
            destroy();
        }
    }
    @Override
    public Image getActiveFrame() {
        return getLevel().getManager().getEngine().getTexture(getType().toString().toLowerCase());
    }

    public double getWidth() {
        return 50;
    }

    public double getHeight() {
        return 50;
    }
}

