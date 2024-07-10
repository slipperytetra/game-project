package utils;

import main.Camera;
import main.GameObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private static final int MAX_OBJECTS = 4;
    private static final int MAX_LEVELS = 5;

    private int level;
    private List<GameObject> objects;
    private CollisionBox bounds;
    private QuadTree[] nodes;
    public GameObject focus;
    public boolean divided;

    public QuadTree(CollisionBox bounds, int level) {
        this.level = level;
        this.objects = new ArrayList<>();
        this.bounds = bounds;
        this.nodes = new QuadTree[4];
    }

    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    private void split() {
        int subWidth = (int) (bounds.getWidth() / 2);
        int subHeight = (int) (bounds.getHeight() / 2);
        int x = (int) bounds.getLocation().getX();
        int y = (int) bounds.getLocation().getY();

        nodes[0] = new QuadTree(new CollisionBox(x + subWidth, y, subWidth, subHeight), level + 1);
        nodes[0].focus = focus;
        nodes[1] = new QuadTree(new CollisionBox(x, y, subWidth, subHeight), level + 1);
        nodes[1].focus = focus;
        nodes[2] = new QuadTree(new CollisionBox(x, y + subHeight, subWidth, subHeight), level + 1);
        nodes[2].focus = focus;
        nodes[3] = new QuadTree(new CollisionBox(x + subWidth, y + subHeight, subWidth, subHeight), level + 1);
        nodes[3].focus = focus;
        divided = true;
    }

    private int getIndex(GameObject gameObject) {
        CollisionBox rect = gameObject.getCollisionBox();
        int index = -1;
        double verticalMidpoint = bounds.getLocation().getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getLocation().getY() + (bounds.getHeight() / 2);

        boolean topQuadrant = (rect.getLocation().getY() < horizontalMidpoint && rect.getLocation().getY() + rect.getHeight() < horizontalMidpoint);
        boolean bottomQuadrant = (rect.getLocation().getY() > horizontalMidpoint);

        if (rect.getLocation().getX() < verticalMidpoint && rect.getLocation().getX() + rect.getWidth() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (rect.getLocation().getX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    private int getIndex(CollisionBox rect) {
        int index = -1;
        double verticalMidpoint = bounds.getLocation().getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getLocation().getY() + (bounds.getHeight() / 2);

        boolean topQuadrant = (rect.getLocation().getY() < horizontalMidpoint && rect.getLocation().getY() + rect.getHeight() < horizontalMidpoint);
        boolean bottomQuadrant = (rect.getLocation().getY() > horizontalMidpoint);

        if (rect.getLocation().getX() < verticalMidpoint && rect.getLocation().getX() + rect.getWidth() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (rect.getLocation().getX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    public void insert(GameObject rect) {
        if (nodes[0] != null) {
            int index = getIndex(rect);

            if (index != -1) {
                nodes[index].insert(rect);
                return;
            }
        }

        objects.add(rect);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public List<GameObject> query(CollisionBox rect) {
        List<GameObject> returnObjects = new ArrayList<>();
        query(returnObjects, rect);

        List<GameObject> found = new ArrayList<>();
        for (GameObject gameObject : returnObjects) {
            if (rect.collidesWith(gameObject.getCollisionBox())) {
                found.add(gameObject);
            }
        }
        return found;
    }

    private void query(List<GameObject> returnObjects, CollisionBox rect) {
        int index = getIndex(rect);
        if (index != -1 && nodes[0] != null) {
            nodes[index].query(returnObjects, rect);
        }

        returnObjects.addAll(objects);
    }

    public void render(Camera cam) {
        //System.out.println(level + " - " + bounds.getLocation().getX() + ", " + bounds.getLocation().getY());
        cam.game.changeColor(Color.MAGENTA);

        if (focus != null) {
            if (focus.isCollidable() && focus.getCollisionBox().collidesWith(bounds)) {
                cam.game.changeColor(Color.orange);
            }
        }

        //System.out.println(cam.toScreenX(boundary.getLocation().getX()) + ", " + cam.toScreenY(boundary.getLocation().getY()));
        cam.game.drawRectangle(cam.toScreenX(bounds.getLocation().getX()),
                cam.toScreenY(bounds.getLocation().getY()),
                bounds.getWidth(),
                bounds.getHeight());

        for (int i = 0; i < 4; i++) {
            if (this.nodes[i] != null) {
                this.nodes[i].render(cam);
            }
        }
    }
}
