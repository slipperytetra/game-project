import java.awt.*;

public class BlockGround extends Block {
    public BlockGround(Location loc) {
        super(loc);
    }

    @Override
    public Color getColor() {
        return Color.yellow;
    }
}
