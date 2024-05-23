package block;

import main.CollisionBox;
import main.Location;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class BlockVoid extends Block {

    public BlockVoid(Location loc) {
        super(BlockTypes.VOID, loc);
    }

    @Override
    public CollisionBox getCollisionBox() {
        return null;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }
}
