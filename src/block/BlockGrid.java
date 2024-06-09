package block;

import level.Level;
import main.Game;
import main.Location;

public class BlockGrid {

    int width, height;
    Block[][] blocks;

    public BlockGrid(Level level, int width, int height) {
        this.width = width;
        this.height = height;
        this.blocks = new Block[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Block block = new Block(level, new Location(x * Game.BLOCK_SIZE, y * Game.BLOCK_SIZE), BlockTypes.VOID);
                block.setCollidable(false);
                blocks[x][y] = block;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Block[][] getBlocks() {
        return blocks;
    }

    public Block getBlockAt(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            System.out.println("Error: trying to get block outside of block.BlockGrid dimensions. (" + x + "/" + getWidth() + ", " + y + "/" + getHeight() + ")");
            return null;
        }

        return blocks[x][y];
    }

    public void setBlock(int x, int y, Block block) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            System.out.println("Error: trying to set block outside of BlockGrid dimensions [" + x + ", " + y + "]");
            return;
        }

        blocks[x][y] = block;
    }

    public void setBlockType(int x, int y, BlockTypes type) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            System.out.println("Error: trying to set block outside of BlockGrid dimensions [" + x + ", " + y + "]");
            return;
        }

        blocks[x][y].setType(type);
    }

    public void applySets() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //System.out.println("Test");
                Block block = getBlockAt(x, y);
                if (block instanceof BlockSet) {
                    BlockSet bSet = (BlockSet) block;
                    bSet.calculateState(this);
                    setBlock(x, y, bSet);
                }
            }
        }
    }
}
