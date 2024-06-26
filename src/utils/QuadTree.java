package utils;

import main.Camera;
import main.GameObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuadTree {

    private CollisionBox boundary;
    private int capacity;
    public List<GameObject> gameObjects;
    private boolean divided;

    private QuadTree northEast, northWest, southEast, southWest;

    public GameObject focus;

    public QuadTree(CollisionBox boundary, int capacity) {
        this.boundary = boundary;
        this.capacity = capacity;
        this.gameObjects = new ArrayList<>();
    }

    public boolean insert(GameObject gameObject) {
        //if (!gameObject.getLocation().isBetween(boundary.getLocation(), boundary.getCorner())) {
        if (!this.boundary.collidesWith(gameObject.getCollisionBox())) {
            return false;
        }

        if (this.gameObjects.size() < this.capacity) {
            this.gameObjects.add(gameObject);
            return true;
        } else {
            if (!divided) {
                this.subdivide();
            }

            if (this.northEast.insert(gameObject)) {
                return true;
            } else if (this.northWest.insert(gameObject)) {
                return true;
            } else if (this.southEast.insert(gameObject)) {
                return true;
            } else if (this.southWest.insert(gameObject)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void subdivide() {
        double x = boundary.getLocation().getX();
        double y = boundary.getLocation().getY();
        double w = boundary.getWidth();
        double h = boundary.getHeight();

        CollisionBox nw = new CollisionBox(x, y, w/2, h/2);
        northWest = new QuadTree(nw, capacity);
        northWest.focus = focus;

        CollisionBox ne = new CollisionBox(x + w/2, y, w/2, h/2);
        northEast = new QuadTree(ne, capacity);
        northEast.focus = focus;

        CollisionBox sw = new CollisionBox(x, y + h/2, w/2, h/2);
        southWest = new QuadTree(sw, capacity);
        southWest.focus = focus;

        CollisionBox se = new CollisionBox(x + w/2, y + h/2, w/2, h/2);
        southEast = new QuadTree(se, capacity);
        southEast.focus = focus;

        /*
        CollisionBox nw = new CollisionBox(x - w/2, y - h/2, w/2, h/2);
        northWest = new QuadTree(nw, capacity);
        CollisionBox ne = new CollisionBox(x + w/2, y - h/2, w/2, h/2);
        northEast = new QuadTree(ne, capacity);
        CollisionBox sw = new CollisionBox(x - w/2, y + h/2, w/2, h/2);
        southWest = new QuadTree(sw, capacity);
        CollisionBox se = new CollisionBox(x + w/2, y + h/2, w/2, h/2);
        southEast = new QuadTree(se, capacity);*/
        this.divided = true;
    }

    public List<GameObject> query(GameObject target) {
        List<GameObject> found = new ArrayList<>();
        if (!this.boundary.collidesWith(target)) {
            return found;
        } else {
            for (GameObject gameObject : gameObjects) {
                if (target.equals(gameObject)) {
                    continue;
                }

                if (target.getCollisionBox().collidesWith(gameObject)) {
                    found.add(gameObject);
                }
            }

            if (divided) {
                found.addAll(this.northWest.query(target));
                found.addAll(this.northEast.query(target));
                found.addAll(this.southWest.query(target));
                found.addAll(this.southEast.query(target));
            }

            return found;
        }
    }

    public List<GameObject> query(GameObject target, CollisionBox box) {
        List<GameObject> found = new ArrayList<>();
        if (!this.boundary.collidesWith(box)) {
            return found;
        } else {
            for (GameObject gameObject : gameObjects) {
                if (target.equals(gameObject)) {
                    continue;
                }

                if (box.collidesWith(gameObject)) {
                    found.add(gameObject);
                }
            }

            if (divided) {
                found.addAll(this.northWest.query(target, box));
                found.addAll(this.northEast.query(target, box));
                found.addAll(this.southWest.query(target, box));
                found.addAll(this.southEast.query(target, box));
            }

            return found;
        }
    }

    public GameObject querySingle(GameObject target) {
        if (!this.boundary.collidesWith(target)) {
            return null;
        } else {
            for (GameObject gameObject : gameObjects) {
                if (target.getCollisionBox().collidesWith(gameObject)) {
                    return gameObject;
                }
            }
        }

        return null;
    }

    public void render(Camera cam) {
        cam.game.changeColor(Color.MAGENTA);

        if (focus != null) {
            if (focus.isCollidable() && focus.getCollisionBox().collidesWith(boundary)) {
                cam.game.changeColor(Color.orange);
            }
        }

        //System.out.println(cam.toScreenX(boundary.getLocation().getX()) + ", " + cam.toScreenY(boundary.getLocation().getY()));
        cam.game.drawRectangle(cam.toScreenX(boundary.getLocation().getX()),
                cam.toScreenY(boundary.getLocation().getY()),
                boundary.getWidth(),
                boundary.getHeight());

        if (divided) {
            this.northWest.render(cam);
            this.northEast.render(cam);
            this.southWest.render(cam);
            this.southEast.render(cam);
        }
    }
}
