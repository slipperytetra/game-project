package block.decorations;

import main.Camera;
import main.CollisionBox;
import main.Game;
import main.Location;

import java.awt.*;

public class Decoration {

    private DecorationTypes type;
    private double scale;
    private double width, height;
    private CollisionBox collisionBox;
    private Location loc;

    public Decoration(DecorationTypes type, Location loc, double width, double height) {
        this.type = type;
        this.loc = loc;
        setScale(type.getScale());
        this.width = width;
        this.height = height;

        this.collisionBox = new CollisionBox(getLocation().getX(), getLocation().getY() - getHeight() + Game.BLOCK_SIZE, getWidth(), getHeight());
    }

    public void update(double dt) {
        //
    }

    public void render(Camera cam) {
        double decoOffsetX = getLocation().getX() + cam.centerOffsetX;
        double decoOffsetY = getLocation().getY() + cam.centerOffsetY;

        Image texture = cam.game.getTexture(getType().toString());
        cam.game.drawImage(texture, decoOffsetX, decoOffsetY - getHeight() + Game.BLOCK_SIZE, getWidth(), getHeight());

        if (cam.debugMode) {
            double hitboxOffsetX = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
            double hitboxOffsetY = getCollisionBox().getLocation().getY() + cam.centerOffsetY;
            cam.game.changeColor(Color.GREEN);
            cam.game.drawRectangle(hitboxOffsetX, hitboxOffsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }
    }

    public DecorationTypes getType() {
        return type;
    }

    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    public void setCollisionBox(CollisionBox box) {
        this.collisionBox = box;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getWidth() {
        return width * getScale();
    }

    public double getHeight() {
        return height * getScale();
    }

    public Location getLocation() {
        return loc;
    }
}
