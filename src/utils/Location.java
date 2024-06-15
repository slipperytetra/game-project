package utils;

import main.Game;

public class Location {
    double locX;
    double locY;

    public Location(double locX, double locY) {
        this.locX = locX;
        this.locY = locY;
    }
    public Location(Location loc) {
        this.locX = loc.getX();
        this.locY = loc.getY();
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
        return "Location={x:" + getX() + ", y:" + getY() + "}";
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

    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    public double calculateDistance(Location otherLoc) {
        double deltaX = otherLoc.getX() - getX();
        double deltaY = otherLoc.getY() - getY();
        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    public double distanceTo(Location loc) {
        return Math.sqrt(Math.pow(loc.getX() - getX(), 2) + Math.pow(loc.getY() - getY(), 2));
    }

    public boolean equals(Location loc) {
        return getX() == loc.getX() && getY() == loc.getY();
    }

    public int getTileX() {
        return (int) (getX() / Game.BLOCK_SIZE);
    }

    public int getTileY() {
        return (int) (getY() / Game.BLOCK_SIZE);
    }

    @Override
    public Location clone() {
        return new Location(getX(), getY());
    }

    public void setLocation(Location loc) {
        if (loc == null) {
            System.out.println("Error: trying to set location to a null location");
            return;
        }

        this.locX = loc.getX();
        this.locY = loc.getY();
    }
}
