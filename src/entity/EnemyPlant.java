package entity;

import level.Level;
import main.Camera;
import main.Location;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EnemyPlant extends Enemy {

    public EnemyPlant(Level level, Location loc) {
        super(level, EntityType.PLANT_MONSTER, loc, 58, 79);

        setDamage(5);
        setMaxHealth(20);
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
            int diffX = -70;
            int diffY = -17;

            if (isFlipped()) {
                diffX = 8;
            }
            offsetX = offsetX + diffX;
            offsetY = offsetY + diffY;
        }

        getLevel().getManager().getEngine().drawImage(getActiveFrame(), offsetX, offsetY, getWidth(), getHeight());

        if (cam.debugMode) {
            double hitBoxOffsetX = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
            double hitBoxOffsetY = getCollisionBox().getLocation().getY() + cam.centerOffsetY;

            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(hitBoxOffsetX, hitBoxOffsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }
    }

    @Override
    public Image getActiveFrame() {
        if (isAttacking()){
            if (isFlipped()) {
                return getLevel().getManager().getEngine().getTexture("plant_monsterAttack_flipped");
            }

            return getLevel().getManager().getEngine().getTexture("plant_monsterAttack");
        }

        if (isFlipped()) {
            return getLevel().getManager().getEngine().flipImageHorizontal(getLevel().getManager().getEngine().getTexture(getType().toString().toLowerCase()));
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
}