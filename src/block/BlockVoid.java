package block;

import main.Location;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class BlockVoid extends Block {

    public BlockVoid(BlockTypes type, Location loc) {
        super(type, loc);
    }

    @Override
    public Rectangle getCollisionBox() {
        return null;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public Color getColor() {
        return Color.pink;
    }
}
