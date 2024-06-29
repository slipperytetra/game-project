package block;

import level.Level;
import main.Camera;
import utils.Location;

import java.awt.*;

public class BlockSpawnPoint extends Block {
    public BlockSpawnPoint(Level level, Location loc) {
        super(level, loc, BlockTypes.PLAYER_SPAWN);
    }

    @Override
    public void render(Camera cam) {
        if (getTexture() == null) {
            System.out.println("Null block image: " + getType().getFilePath());
            return;
        }

        if (cam.debugMode || getLevel().isEditMode()) {
            cam.game.drawImage(getTexture().getImage(), cam.toScreenX(getLocation().getX()) , cam.toScreenY(getLocation().getY()) , getCollisionBox().getWidth() , getCollisionBox().getHeight() );

            if (cam.debugMode) {
                cam.game.changeColor(Color.GREEN);
                cam.game.drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()) , cam.toScreenY(getCollisionBox().getLocation().getY()) , getCollisionBox().getWidth() , getCollisionBox().getHeight() );
            }
        }
    }
}
