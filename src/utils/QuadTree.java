package utils;

import main.Camera;
import main.GameObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private static final int MAX_OBJECTS = 4;
    private static final int MAX_LEVELS = 150;

    private int level;
    private List<GameObject> objects;
    private CollisionBox bounds;
    private QuadTree[] nodes;
    private boolean divided;
    public GameObject focus;

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
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();

        divided = true;
        nodes[0] = new QuadTree(new CollisionBox(x + subWidth, y, subWidth, subHeight), level + 1);
        nodes[0].focus = focus;
        nodes[1] = new QuadTree(new CollisionBox(x, y, subWidth, subHeight), level + 1);
        nodes[1].focus = focus;
        nodes[2] = new QuadTree(new CollisionBox(x, y + subHeight, subWidth, subHeight), level + 1);
        nodes[2].focus = focus;
        nodes[3] = new QuadTree(new CollisionBox(x + subWidth, y + subHeight, subWidth, subHeight), level + 1);
        nodes[3].focus = focus;
    }

    private int getIndex(CollisionBox pRect) {
        int index = -1;
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        boolean topQuadrant = (pRect.getY() < horizontalMidpoint && pRect.getY() + pRect.getHeight() < horizontalMidpoint);
        boolean bottomQuadrant = (pRect.getY() > horizontalMidpoint);

        if (pRect.getX() < verticalMidpoint && pRect.getX() + pRect.getWidth() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (pRect.getX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    public void insert(GameObject gameObject) {
        if (nodes[0] != null) {
            int index = getIndex(gameObject.getCollisionBox());

            if (index != -1) {
                nodes[index].insert(gameObject);
                return;
            }
        }

        objects.add(gameObject);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i).getCollisionBox());
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public List<GameObject> retrieve(GameObject target) {
        List<GameObject> found = new ArrayList<>();

        if (this.bounds.collidesWith(target)) {
            for (GameObject gameObject : objects) {
                if (target.equals(gameObject) || !target.getCollisionBox().collidesWith(bounds)) {
                    continue;
                }

                if (target.getCollisionBox().collidesWith(gameObject)) {
                    found.add(gameObject);
                }
            }

            if (divided) {
                found.addAll(this.nodes[0].retrieve(target));
                found.addAll(this.nodes[1].retrieve(target));
                found.addAll(this.nodes[2].retrieve(target));
                found.addAll(this.nodes[3].retrieve(target));
            }

        }

        return found;
    }

    public List<GameObject> retrieve(CollisionBox target) {
        List<GameObject> found = new ArrayList<>();

        if (this.bounds.collidesWith(target)) {
            for (GameObject gameObject : objects) {
                if (target.equals(gameObject.getCollisionBox()) || !target.collidesWith(bounds)) {
                    continue;
                }

                if (target.collidesWith(gameObject)) {
                    found.add(gameObject);
                }
            }

            if (divided) {
                found.addAll(this.nodes[0].retrieve(target));
                found.addAll(this.nodes[1].retrieve(target));
                found.addAll(this.nodes[2].retrieve(target));
                found.addAll(this.nodes[3].retrieve(target));
            }

        }

        return found;
    }

    public void render(Camera cam) {
        cam.game.changeColor(Color.MAGENTA);

        if (focus != null && focus.getCollisionBox().collidesWith(bounds)) {
            cam.game.changeColor(Color.ORANGE);
        }

        //System.out.println(cam.toScreenX(boundary.getLocation().getX()) + ", " + cam.toScreenY(boundary.getLocation().getY()));
        cam.game.drawRectangle(cam.toScreenX(bounds.getLocation().getX()),
                cam.toScreenY(bounds.getLocation().getY()),
                bounds.getWidth(),
                bounds.getHeight());

        if (divided) {
            this.nodes[0].render(cam);
            this.nodes[1].render(cam);
            this.nodes[2].render(cam);
            this.nodes[3].render(cam);
        }
    }
}
