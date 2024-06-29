package block;

import level.Level;
import utils.Location;

public abstract class BlockActive extends Block {

    private int state;
    private int maxStates;

    public BlockActive(Level level, Location loc, BlockTypes type) {
        super(level, loc, type);

        this.state = 0;
        this.maxStates = type.getBlockSetAmount();
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (state > getMaxStates() - 1) {
            this.state = getMaxStates() - 1;
            return;
        }

        this.state = state;
    }

    public int getMaxStates() {
        return maxStates;
    }
}
