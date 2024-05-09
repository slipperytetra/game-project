package main;

public class CollisionBox {

    Location loc;
    int width, height;

    public CollisionBox(double locX, double locY, int width, int height) {
        this.loc = new Location(locX, locY);
        this.width = width;
        this.height = height;
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(double x, double y) {
        this.loc.setX(x);
        this.loc.setY(y);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
