import java.awt.*;

public abstract class Block {

    Location loc;

    public Block(Location loc) {
        this.loc = loc;
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(double x, double y) {
        this.loc.setX(x);
        this.loc.setY(y);
    }

    public abstract Color getColor();
}
