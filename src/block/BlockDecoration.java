package block;

import main.Location;

public class BlockDecoration extends Block {

    public BlockDecoration(BlockTypes type, Location loc) {
        super(type, loc);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }
}
