package main;
public class Location {
    double locX;
    double locY;

    public Location(double locX, double locY) {
        this.locX = locX;
        this.locY = locY;
    }

    public double getX() {
        return this.locX;
    }

    public double getY() {
        return this.locY;
    }

    public void setX(double newX) {
        this.locX = newX;
    }

    public void setY(double newY) {
        this.locY = newY;
    }

    public String toString() {
        return "main.Location={x:" + getX() + ", y:" + getY() + "}";
    }
}
