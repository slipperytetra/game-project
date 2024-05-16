package block;

import main.Location;

public class BlockLiquid extends Block{
    public BlockLiquid(BlockTypes type, Location loc) {
        super(type, loc);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }
}
