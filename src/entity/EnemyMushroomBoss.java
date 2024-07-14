package entity;

import level.Level;
import main.Camera;
import main.Game;
import main.SoundType;
import utils.GameUtils;
import utils.Location;
import utils.Texture;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class EnemyMushroomBoss extends Enemy {

    private boolean isDormant;
    private long startTime;

    private double dormantScale;

    public EnemyMushroomBoss(Level level, Location loc) {
        super(EntityType.MUSHROOM_MONSTER, level, loc);

        this.startTime = System.currentTimeMillis();

        setTarget(getLevel().getPlayer());
        setAttackRange(Game.BLOCK_SIZE * 2);

        setHitSound(SoundType.GENERIC_HIT);
        setAttackSound(null);
        setAttackCooldown(2.0);
        setScale(1);
        setHostile(false);
        isDormant = true;
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        if (isDormant()) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;
            double cycleProgress = (double) (elapsedTime % 3000) / 3000;
            double angle = cycleProgress * 2 * Math.PI;
            dormantScale = 0.95 + (1.05 - 0.95) * (0.5 * (Math.sin(angle) + 1));

            if (getAttackTimer().isRunning()) {
                getAttackTimer().stop();
            }
        }
    }

    @Override
    public void render(Camera cam) {
        int diffX = (int) (getIdleFrame().getWidth() - (getIdleFrame().getWidth() * dormantScale));
        int diffY = (int) (getIdleFrame().getHeight() - (getIdleFrame().getHeight() * dormantScale));
        double offsetX = cam.toScreenX(getLocation().getX() + diffX);
        double offsetY = cam.toScreenY(getLocation().getY() + diffY);

        if (deathTimer.isRunning()) {
            Graphics2D g2d = cam.game.mGraphics;
            AffineTransform oldTrans = g2d.getTransform();
            g2d.translate(offsetX, offsetY);
            g2d.scale(getScale(), getScale());
            g2d.rotate(getRotation(), getWidth() / 2, getHeight() / 2);
            g2d.drawImage(GameUtils.applyTint(getIdleFrame(), tint).getImage(), 0, 0, null);
            g2d.setTransform(oldTrans);
            return;
        }

        getLevel().getManager().getEngine().drawImage(getActiveFrame().getImage(), offsetX, offsetY, getWidth(), getHeight());

        if (cam.debugMode) {
            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }
    }

    @Override
    public Texture getIdleFrame() {
        if (isDormant()) {
            return getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString());
        }

        return getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString());
    }

    public boolean isDormant() {
        return isDormant;
    }

    @Override
    public void attack() {
        if (getTarget() == null) {
            return;
        }

        if (getType() != EntityType.PLAYER) {
            double direction = getLocation().getX() - getTarget().getLocation().getX();
            setFlipped(direction < 0);
        }
    }

    public double getScale() {
        if (isDormant() && !deathTimer.isRunning()) {
            return 1 * dormantScale;
        }

        return super.getScale();
    }
}
