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

    public boolean isBlockBetween(Location point1, Location point2) {
        double loc1X = point1.getX() - Game.BLOCK_SIZE;
        double loc1y = point1.getY() - Game.BLOCK_SIZE;

        double loc2X = point2.getX() + Game.BLOCK_SIZE;
        double loc2y = point2.getY() + Game.BLOCK_SIZE;
        return (this.getX() > loc1X && this.getX() < loc2X)
                && (this.getY() > loc1y && this.getY() < loc2y);
    }

    public boolean isBetween(Location point1, Location point2) {
        return (this.getX() > point1.getX() && this.getX() < point2.getX())
                && (this.getY() > point1.getY() && this.getY() < point2.getY());
    }
}
