package entity.utils;

import entity.EntityLiving;
import level.Level;
import utils.Location;
import main.Camera;

import java.awt.*;

public class BossBar {

    private Location loc;
    private EntityLiving entity;

    private final int barWidth = 750;
    private final int barHeight = 20;
    private double barOffset;

    public BossBar(EntityLiving entity) {
        this.entity = entity;

        this.barOffset = (getLevel().getManager().getEngine().width() / 2.0) - (barWidth / 2.0);
    }

    public EntityLiving getEntity() {
        return entity;
    }

    public void setEntity(EntityLiving entity) {
        this.entity = entity;
    }

    public double getMax() {
        if (getEntity() != null) {
            return getEntity().getMaxHealth();
        }

        return 100;
    }

    public double getProgress() {
        if (getEntity() != null) {
            double scale = barWidth / getMax();
            return getEntity().getHealth() * scale;
        }

        return 0;
    }

    public Level getLevel() {
        return getEntity().getLevel();
    }

    public double getPositionX() {
        return barOffset;
    }

    public double getPositionY() {
        return 50;
    }

    public void render(Camera cam) {
        cam.game.changeColor(Color.DARK_GRAY);
        cam.game.drawSolidRectangle(getPositionX(), getPositionY(), barWidth, barHeight);

        cam.game.changeColor(Color.RED);
        cam.game.drawSolidRectangle(getPositionX(), getPositionY(), getProgress(), barHeight);

        cam.game.changeColor(Color.WHITE);
        cam.game.drawBoldText(cam.game.width() / 2.0, getPositionY() - 10, getEntity().getType().name(), 13);
    }
}
