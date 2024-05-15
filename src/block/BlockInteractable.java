package block;

import main.Location;

public class BlockInteractable extends Block {
    public BlockInteractable(BlockTypes type, Location loc) {
        super(type, loc);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }
}
