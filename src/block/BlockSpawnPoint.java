package block;

import level.Level;
import main.Camera;
import main.Location;

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
            double xOffset = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
            double yOffset = getCollisionBox().getLocation().getY() + cam.centerOffsetY;
            cam.game.drawImage(getTexture().getImage(), xOffset * cam.getZoom(), yOffset * cam.getZoom(), getCollisionBox().getWidth() * cam.getZoom(), getCollisionBox().getHeight() * cam.getZoom());

            if (cam.debugMode) {
                cam.game.changeColor(Color.GREEN);
                cam.game.drawRectangle(xOffset * cam.getZoom(), yOffset * cam.getZoom(), getCollisionBox().getWidth() * cam.getZoom(), getCollisionBox().getHeight() * cam.getZoom());
            }
        }
    }
}
