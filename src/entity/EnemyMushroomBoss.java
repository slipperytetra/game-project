package entity;

import block.decorations.FakeLightSpot;
import level.Level;
import main.Camera;
import main.SoundType;
import utils.GameUtils;
import utils.Location;
import utils.Texture;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class EnemyMushroomBoss extends Enemy {


    public EnemyMushroomBoss(Level level, Location loc) {
        super(EntityType.MUSHROOM_MONSTER, level, loc);

        setDamage(5);
        setMaxHealth(200);
        setHealth(200);
        setScale(3);
        setHitboxWidth(32);
        setHitboxHeight(60);
        setHitboxOffsetX(15 * getScale());
        setHitboxOffsetY(4 * getScale());
        setAttackCooldown(2.0);

        setHitSound(SoundType.GENERIC_HIT);
        setAttackSound(null);


        if (getLevel().getPlayer() != null) {
            setTarget(getLevel().getPlayer());
        }
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

        getLevel().getManager().getEngine().drawImage(getActiveFrame().getImage(), offsetX , offsetY , getWidth() , getHeight() );

        if (cam.debugMode) {
            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() , getCollisionBox().getHeight() );
        }
    }

    @Override
    public Texture getActiveFrame() {
        Texture texture = getIdleFrame();

        /*
        if (isAttacking()) {
            texture = getAttackFrame();
        }*/

        texture.setFlipped(isFlipped());

        if (getHurtTicks() > 0) {
            return GameUtils.applyTint(texture, tint);
        }

        return texture;
    }

    @Override
    public boolean isFlipped() {
        if (getTarget() == null) {
            return false;
        }

        return getTarget().getLocation().getX() < getLocation().getX();
    }

    @Override
    public void kill() {
        super.kill();

        getLevel().getCamera().setFocusObject(getLevel().getPlayer());
        getLevel().getCamera().setFixed(false);
    }
}