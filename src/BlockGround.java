import java.awt.*;

public class BlockGround extends Block {
    public BlockGround(Location loc) {
        super(loc);
    }

    @Override
    public String getString() {
        return "resources/images/ground.png";
    }


}
