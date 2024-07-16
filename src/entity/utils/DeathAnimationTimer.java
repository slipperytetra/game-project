package entity.utils;

import entity.EntityLiving;
import main.Game;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeathAnimationTimer extends Timer {

    private EntityLiving entity;
    private double originalScale;

    public DeathAnimationTimer(EntityLiving entity) {
        super(15, null);
        this.entity = entity;
        this.originalScale = entity.getScale();
        this.setInitialDelay(0);

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (entity.getLevel().isEditMode() || Game.isPaused) {
                    return;
                }

                entity.setRotation(entity.getRotation() + (Math.PI) * ((double) 1 / 25));
                double scale = entity.getScale() - (1.0 / 25);
                if (scale <= 0) {
                    scale = 0.05;
                }
                entity.setScale(scale);

                if (entity.getRotation() > Math.PI * 2 || scale <= 0.05) {
                    entity.setScale(originalScale);
                    entity.setRotation(0);
                    stop();
                }
            }
        });
    }

    @Override
    public void stop() {
        super.stop();
        System.out.println("Stopping rotation for " + entity.getType());
    }
}
