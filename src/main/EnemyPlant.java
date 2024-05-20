package main;

import level.Level;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class EnemyPlant extends Enemy {

    public EnemyPlant(Level level, Location loc) {
        super(level, EntityType.PLANT_MONSTER, loc, 58, 79);

        setDamage(5);
        setMaxHealth(100);
        setScale(1);
        setAttackCooldown(1.0);

        setHitSound(getLevel().getManager().getEngine().loadAudio("resources/sounds/attackHit.wav"));
        setAttackSound(null);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if(!isActive()){
            return;
        }

        setTarget(getLevel().getPlayer());
    }

    @Override
    public void render(Camera cam) {
        double offsetX = getLocation().getX() + cam.centerOffsetX;
        double offsetY = getLocation().getY() + cam.centerOffsetY;

        if (isAttacking()) {
            offsetX = offsetX - 70;
            offsetY = offsetY - 17;
        }

        getLevel().getManager().getEngine().drawImage(getActiveFrame(), offsetX, offsetY, getWidth(), getHeight());

        if (cam.showHitboxes) {
            double hitBoxOffsetX = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
            double hitBoxOffsetY = getCollisionBox().getLocation().getY() + cam.centerOffsetY;

            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(hitBoxOffsetX, hitBoxOffsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }
    }

    @Override
    public Image getActiveFrame() {
        if (isAttacking()){
            return getLevel().getManager().getEngine().getTexture("plant_monsterAttack");
        }

        return getLevel().getManager().getEngine().getTexture(getType().toString().toLowerCase());
    }

    @Override
    public double getWidth() {
        if (isAttacking()) {
            return 128 * getScale();
        }

        return ((BufferedImage) getIdleFrame()).getWidth() * getScale();
    }

    @Override
    public double getHeight() {
        if (isAttacking()) {
            return 96 * getScale();
        }

        return ((BufferedImage) getIdleFrame()).getHeight() * getScale();
    }

    @Override
    public void destroy(){
        super.destroy();
    }
}