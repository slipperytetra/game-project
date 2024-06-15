package block.decorations;

import level.Level;
import main.*;
import utils.CollisionBox;
import utils.Location;
import utils.Texture;

import java.awt.*;

public class Decoration extends GameObject {

    private DecorationTypes type;

    public Decoration(Level level, Location loc, DecorationTypes type) {
        super(level, loc);
        this.type = type;
        setScale(type.getScale());

        setCollisionBox(new CollisionBox(getLocation().getX(), getLocation().getY() - getHeight() + Game.BLOCK_SIZE, getWidth(), getHeight()));
    }

    public void render(Camera cam) {
        cam.game.drawImage(getFrame().getImage(), cam.toScreenX(getLocation().getX()), (cam.toScreenY(getLocation().getY()) - getHeight() + Game.BLOCK_SIZE) * cam.getZoom(), getWidth() * cam.getZoom(), getHeight() * cam.getZoom());

        if (cam.debugMode) {
            cam.game.changeColor(Color.GREEN);
            cam.game.drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() * cam.getZoom(), getCollisionBox().getHeight() * cam.getZoom());
        }
    }

    public Texture getFrame() {
        return getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString().toLowerCase());
    }

    public DecorationTypes getType() {
        return type;
    }

    @Override
    public double getWidth() {
        return getFrame().getWidth() * getScale();
    }

    @Override
    public double getHeight() {
        return getFrame().getHeight() * getScale();
    }

    @Override
    public void updateCollisionBox() {
        this.getCollisionBox().setLocation(getLocation().getX(), getLocation().getY() - getHeight() + Game.BLOCK_SIZE);
        this.getCollisionBox().setSize(getWidth(), getHeight());
    }

    @Override
    public void setActive(boolean isActive) {
        super.setActive(isActive);

        if (!isActive()) {
            for (FakeLightSpot light : getLevel().getSpotLights()) {
                if (light.getParent().equals(this)) {
                    light.setActive(false);
                }
            }
        }
    }
}
