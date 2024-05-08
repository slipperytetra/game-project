package block;

import main.Location;

public class BlockGrid {

    int width, height;
    Block[][] blocks;

    public BlockGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.blocks = new Block[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                blocks[x][y] = new BlockVoid(BlockTypes.VOID, new Location(x, y));
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
        if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
            System.out.println("Error: trying to get block outside of block.BlockGrid dimensions.");
            return null;
        }
        return blocks[x][y];
    }

    public void setBlock(int x, int y, Block block) {
        if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
            System.out.println("Error: trying to set block outside of block.BlockGrid dimensions.");
            return;
        }
        blocks[x][y] = block;
    }
}
