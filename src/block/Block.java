package block;

import main.Camera;
import main.CollisionBox;
import main.Game;
import main.Location;

import java.awt.*;

public abstract class Block {

    BlockTypes type;
    Location loc;
    //Rectangle collisionBox;
    CollisionBox collisionBox;

    public Block(BlockTypes type, Location loc) {
        this.type = type;
        this.loc = loc;
        setCollisionBox(new CollisionBox((int)loc.getX(), (int)loc.getY(), Game.BLOCK_SIZE, Game.BLOCK_SIZE));
    }

    public abstract boolean isCollidable();


    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    public void setCollisionBox(CollisionBox newBox) {
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

    public void drawBlock(Camera cam, double xOffset, double yOffset) {
        if(getType().equals(BlockTypes.BARRIER)){
            return;
        }
        Image texture = cam.game.getTexture(getType().toString());

        if (texture == null) {
            System.out.println("Null image: " + getType().getFilePath());
        }



        cam.game.drawImage(texture, xOffset, yOffset, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
        if (cam.showHitboxes) {
            cam.game.changeColor(Color.GREEN);
            cam.game.drawRectangle(xOffset, yOffset, getCollisionBox().getWidth(), getCollisionBox().getHeight());
            //cam.game.drawRectangle(cam.zoom * xOffset, cam.zoom *yOffset, cam.zoom *Game.BLOCK_SIZE, cam.zoom *Game.BLOCK_SIZE);
        }
    }
}
