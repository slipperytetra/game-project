package utils;

public class Vector {

    private double vecX, vecY;

    public Vector(double x, double y) {
        this.vecX = x;
        this.vecY = y;
    }

    public double getX() {
        return vecX;
    }

    public void setX(double vecX) {
        this.vecX = vecX;
    }

    public double getY() {
        return vecY;
    }

    public void setY(double vecY) {
        this.vecY = vecY;
    }

    public double getMagnitude() {
        return Math.sqrt((vecX * vecX) + (vecY * vecY));
    }

    public void normalise() {
        double mag = getMagnitude();

        vecX = vecX / mag;
        vecY = vecY / mag;
    }

    public void multiply(double amount) {
        this.vecX *= amount;
        this.vecY *= amount;
    }

    public void add(double vecX, double vecY) {
        this.vecX += vecX;
        this.vecY += vecY;
    }

    public void subtract(double vecX, double vecY) {
        this.vecX -= vecX;
        this.vecY -= vecY;
    }

    public Vector clone() {
        return new Vector(getX(), getY());
    }
}
