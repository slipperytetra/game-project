package block;

import main.Camera;
import main.Game;
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
        setCollisionBox(new Rectangle((int)loc.getX(), (int)loc.getY(), size, size));
    }

    public abstract boolean isCollidable();

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public void setCollisionBox(Rectangle newBox) {
        this.collisionBox = newBox;
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

    public boolean isBetween(Location point1, Location point2) {
        double loc1X = point1.getX() - Game.BLOCK_SIZE;
        double loc1y = point1.getY() - Game.BLOCK_SIZE;

        double loc2X = point2.getX() + Game.BLOCK_SIZE;
        double loc2y = point2.getY() + Game.BLOCK_SIZE;
        return (getLocation().getX() > loc1X && getLocation().getX() < loc2X)
                && (getLocation().getY() > loc1y && getLocation().getY() < loc2y);
    }

    public void drawBlock(Camera cam, double xOffset, double yOffset) {
        Image texture = cam.game.getTexture(getType().toString());
        if (texture == null) {
            System.out.println("Null image: " + getType().getFilePath());
        }

        cam.game.drawImage(texture, cam.zoom *xOffset, cam.zoom *yOffset, cam.zoom *Game.BLOCK_SIZE, cam.zoom *Game.BLOCK_SIZE);
        if (cam.showHitboxes) {
            cam.game.changeColor(Color.GREEN);
            cam.game.drawRectangle(cam.zoom * xOffset, cam.zoom *yOffset, cam.zoom *Game.BLOCK_SIZE, cam.zoom *Game.BLOCK_SIZE);
        }
    }
}
