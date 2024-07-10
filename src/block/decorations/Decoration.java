package block.decorations;

import level.Level;
import main.*;
import utils.CollisionBox;
import utils.Location;
import utils.Texture;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Decoration extends GameObject {

    private DecorationTypes type;
    protected BufferedImage texture;

    public Decoration(Level level, Location loc, DecorationTypes type) {
        super(level, loc);
        this.type = type;
        setScale(type.getScale());
        updateTexture();

        setCollisionBox(new CollisionBox((int)getLocation().getX(), (int)(getLocation().getY() - getHeight() + Game.BLOCK_SIZE), getWidth(), getHeight()));
    }

    public void render(Camera cam) {
        //BufferedImage texture = getFrame().getImage();
        Graphics2D g2d = cam.game.mGraphics;
        AffineTransform oldTrans = g2d.getTransform();
        g2d.translate((int)cam.toScreenX(getLocation().getX()), (int)cam.toScreenY(getLocation().getY() - getHeight() + Game.BLOCK_SIZE));
        if (getType().getFrames() > 0) {
            g2d.scale(getScale(), getScale());
            g2d.drawImage(getFrame().getImage(), 0, 0, null);
        } else {
            g2d.drawImage(texture, 0, 0, null);
        }
        g2d.setTransform(oldTrans);

        if (cam.debugMode) {
            cam.game.changeColor(Color.GREEN);
            cam.game.drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() , getCollisionBox().getHeight() );
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
        if (getCollisionBox() == null) {
            return;
        }

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

    public void updateTexture() {
        Texture text = getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString().toLowerCase());
        texture = text.getImage().getSubimage(0, 0, text.getImage().getWidth(), text.getImage().getHeight());

        AffineTransform tx = AffineTransform.getScaleInstance(getScale(), getScale());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        texture = op.filter(texture, null);
    }

    @Override
    public void setScale(double scale) {
        super.setScale(scale);
        updateTexture();
    }
}
