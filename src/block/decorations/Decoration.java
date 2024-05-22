package block.decorations;

import main.CollisionBox;
import main.Game;
import main.Location;

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
