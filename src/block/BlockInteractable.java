package block;

import level.Level;
import utils.Location;

public class BlockInteractable extends Block {
    public BlockInteractable(Level level, Location loc, BlockTypes type) {
        super(level, loc, type);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }
}
