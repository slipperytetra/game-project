package block;

import java.awt.*;

public enum BlockTypes {

    VOID(0, 0, 0),
    DIRT(134, 100, 71),
    GRASS(96, 172, 53),
    DOOR(102, 48, 16);

    private final Color color;
    BlockTypes(int r, int g, int b) {
        this.color = new Color(r, g, b);
    }

    public Color getColor() {
        return color;
    }
}
