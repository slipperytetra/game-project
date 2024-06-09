package main;

public class CollisionBox {

    private Location loc, corner;
    private double width, height;

    /*
    *   Simple collision box class. Basically it just consists of a theoretical rectangle from 'loc' to 'corner' than can be compared
    *   with other collision boxes using a formula we found online.
    *
    *   https://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other
    * */

    public CollisionBox(double x, double y, double width, double height) {
        this.loc = new Location(x, y);
        this.width = width;
        this.height = height;
        this.corner = new Location(getLocation().getX() + getWidth(), getLocation().getY() + getHeight());
    }

    public Location getLocation() {
        return loc;
    }

    public void setLocation(double locX, double locY) {
        this.getLocation().setX(locX);
        this.getLocation().setY(locY);
        this.corner.setX(getLocation().getX() + getWidth());
        this.corner.setY(getLocation().getY() + getHeight());
    }

    public Location getCorner() {
        corner.setX(getLocation().getX() + getWidth());
        corner.setY(getLocation().getY() + getHeight());

        return corner;
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
