package main;

public class CollisionBox {

    private Location loc;
    private double width, height;

    public CollisionBox(double x, double y, double width, double height) {
        this.loc = new Location(x, y);
        this.width = width;
        this.height = height;
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(double locX, double locY) {
        this.getLocation().setX(locX);
        this.getLocation().setY(locY);
    }

    public Location getCorner() {
        return new Location(getLocation().getX() + getWidth(), getLocation().getY() + getHeight());
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public boolean collidesWith(CollisionBox box) {
        if (box == null) {
            return false;
        }

        double RectAX1 = getLocation().getX();
        double RectAX2 = getCorner().getX();
        double RectAY1 = getLocation().getY();
        double RectAY2 = getCorner().getY();

        double RectBX1 = box.getLocation().getX();
        double RectBX2 = box.getCorner().getX();
        double RectBY1 = box.getLocation().getY();
        double RectBY2 = box.getCorner().getY();

        if (RectAX1 <= RectBX2 && RectAX2 >= RectBX1 &&
                RectAY1 <= RectBY2 && RectAY2 >= RectBY1) {
            return true;
        }

        return false;
    }
}
