package block;

import main.Location;

import java.awt.*;

public abstract class Block {

    BlockTypes type;
    Location loc;
    Rectangle collisionBox;
    final int size = 32; //32x32 pixels

    public Block(BlockTypes type, Location loc) {
        this.type = type;
        this.loc = loc;
        collisionBox = new Rectangle((int)loc.getX(), (int)loc.getY(), size, size);
    }

    public String getFilePath() {
        return type.getFilePath();
    }
    public abstract boolean isCollidable();

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public void setCollisionBox(Rectangle newBox) {
        this.collisionBox.setRect(newBox);
    }

    public void setCollisionBox(int width, int height) {
        collisionBox.setRect(loc.getX(), loc.getY(), width, height);
    }

    public BlockTypes getType() {
        return type;
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(double x, double y) {
        this.loc.setX(x);
        this.loc.setY(y);
    }
}
