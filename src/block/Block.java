package block;

import level.Level;
import main.*;
import utils.Location;
import utils.Texture;

import java.awt.*;

public class Block extends GameObject {

    private BlockTypes type;

    public Block(Level level, Location loc, BlockTypes type) {
        super(level, loc);

        this.type = type;
        setCollidable(type.isCollidable());
    }

    public BlockTypes getType() {
        return type;
    }

    public void setType(BlockTypes type) {
        this.type = type;
    }

    public void render(Camera cam) {
        if(getType() == BlockTypes.BARRIER || getType() == BlockTypes.VOID){
            return;
        }


        if (getTexture() == null) {
            System.out.println("Null block image: " + getType().getFilePath());
            return;
        }

        cam.game.drawImage(getTexture().getImage(), cam.toScreenX(getLocation().getX()), cam.toScreenY(getLocation().getY()), getCollisionBox().getWidth() * cam.getZoom(), getCollisionBox().getHeight() * cam.getZoom());

        if (cam.debugMode) {
            cam.game.changeColor(Color.GREEN);
            cam.game.drawRectangle(cam.toScreenX(getLocation().getX()), cam.toScreenY(getLocation().getY()), getCollisionBox().getWidth() * cam.getZoom(), getCollisionBox().getHeight() * cam.getZoom());
        }
    }

    public Texture getTexture() {
        return getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString());
    }
}
