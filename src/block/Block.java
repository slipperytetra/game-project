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
        setIsSolid(type.isCollidable());
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
            //System.out.println("Null block image: " + getType().getFilePath());
            return;
        }

        cam.game.drawImage(getTexture().getImage(), cam.toScreenX(getLocation().getX()) , cam.toScreenY(getLocation().getY()) , Game.BLOCK_SIZE, Game.BLOCK_SIZE );

        if (cam.debugMode) {
            cam.game.changeColor(Color.GREEN);
            cam.game.drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()) , cam.toScreenY(getCollisionBox().getLocation().getY()) , getCollisionBox().getWidth() , getCollisionBox().getHeight() );
        }
    }

    public Texture getTexture() {
        return getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString());
    }
}
