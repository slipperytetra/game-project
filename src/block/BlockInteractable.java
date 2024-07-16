package block;

import level.Level;
import utils.Interactable;
import utils.Location;

public class BlockInteractable extends BlockActive implements Interactable  {
    public BlockInteractable(Level level, Location loc, BlockTypes type) {
        super(level, loc, type);
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public void interact() {
    }

    @Override
    public void onInteract() {

    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public double getRange() {
        return 0;
    }
}
