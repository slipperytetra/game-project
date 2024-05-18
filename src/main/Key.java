package main;

import level.Level;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Key extends Entity {
    public Key(Level level, Location loc) {
        super(EntityType.KEY, level, loc);


        setScale(0.5);
        setCanMove(false);
        setFlipped(true);
    }

    public void update(double dt) {
        if (getLevel().getPlayer().getCollisionBox().collidesWith(this.getCollisionBox())) {
            getLevel().getPlayer().setHasKey(true);


            destroy();
        }
    }
    @Override
    public Image getIdleFrame() {


        return getLevel().getManager().getEngine().getTexture(getType().toString().toLowerCase());
    }

    public double getWidth() {
        return 50;
    }

    public double getHeight() {
        return 50;    }

    @Override
    public void processMovement(double dt) {
        return;
    }

    public boolean isFlipped(){
        return true;
    }
}


