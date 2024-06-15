package block;

import block.decorations.BlockDirection;
import level.Level;
import main.Game;
import utils.Location;
import utils.Texture;

public class BlockSet extends Block {

    private int state;

    public BlockSet(Level level, Location loc, BlockTypes type) {
        super(level, loc, type);

        this.state = 0;
    }


    public void calculateState(BlockGrid grid) {
        setState(0);

        for (BlockDirection dir : BlockDirection.values()) {
            double tileX = getLocation().getX() / Game.BLOCK_SIZE;
            double tileY = getLocation().getY() / Game.BLOCK_SIZE;
            tileX += dir.getOffsetX();
            tileY += dir.getOffsetY();

            Block block = null;

            if (tileX >= 0 && tileX < getLevel().getBlockGrid().getWidth()
            && tileY >= 0 && tileY < getLevel().getBlockGrid().getHeight()) {
                block = grid.getBlockAt((int) tileX , (int) tileY);
            }

            if (block == null || block.getType() == this.getType()) {
                setState(getState() + dir.value());
            }

        }
    }

    @Override
    public Texture getTexture() {
        return getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString() + "_" + getState());
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    public int getState() {
        return state;
    }

    public void setState(int newState) {
        if (newState > (BlockDirection.NORTH.value() + BlockDirection.EAST.value() + BlockDirection.SOUTH.value() + BlockDirection.WEST.value())) {
            System.out.println("Error setting block state: exceeds maximum possible states (" + newState + ")");
            return;
        }

        if (newState < 0) {
            System.out.println("Error setting block state: below minimum possible state (" + newState + ")");
            return;
        }

        this.state = newState;
    }
}
