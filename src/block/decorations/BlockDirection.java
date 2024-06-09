package block.decorations;

public enum BlockDirection {

    NORTH(1, 0, -1),
    EAST(2, 1, 0),
    SOUTH(4, 0, 1),
    WEST(8, -1, 0);

    private final int value;
    private final int offsetX, offsetY;

    BlockDirection(int num, int offsetX, int offsetY) {
        this.value = num;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public int value() {
        return value;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }
}
