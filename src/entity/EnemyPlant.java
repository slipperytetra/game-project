package entity;

import level.Level;
import main.Camera;
import utils.GameUtils;
import utils.Location;
import main.SoundType;
import utils.Texture;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class EnemyPlant extends Enemy {

    public EnemyPlant(Level level, Location loc) {
        super(EntityType.PLANT_MONSTER, level, loc);

        setDamage(5);
        setMaxHealth(20);
        setHealth(20);
        setScale(1);
        setAttackCooldown(2.0);

        setHitSound(SoundType.GENERIC_HIT);
        setAttackSound(null);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
    }

    @Override
    public void render(Camera cam) {
        double offsetX = cam.toScreenX(getLocation().getX());
        double offsetY = cam.toScreenY(getLocation().getY());

        if (deathTimer.isRunning()) {
            Graphics2D g2d = cam.game.mGraphics;
            AffineTransform oldTrans = g2d.getTransform();
            g2d.translate(offsetX, offsetY);
            g2d.rotate(getRotation(), getWidth()/2, getHeight()/2);
            g2d.scale(getScale(), getScale());
            g2d.drawImage(GameUtils.applyTint(getIdleFrame(), tint).getImage(), 0, 0, null);
            g2d.setTransform(oldTrans);
            return;
        }

        if (isAttacking()) {
            int diffX = -70;
            int diffY = -17;

            if (isFlipped()) {
                diffX = 8;
            }
            offsetX = offsetX + diffX;
            offsetY = offsetY + diffY;
        }

        getLevel().getManager().getEngine().drawImage(getActiveFrame().getImage(), offsetX , offsetY , getWidth() , getHeight() );

        if (cam.debugMode) {
            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() , getCollisionBox().getHeight() );
        }
    }

    @Override
    public Texture getActiveFrame() {
        Texture texture = getIdleFrame();

        if (isAttacking()) {
            texture = getAttackFrame();
        }

        texture.setFlipped(isFlipped());

        if (getHurtTicks() > 0) {
            return GameUtils.applyTint(texture, tint);
        }

        return texture;
    }
}