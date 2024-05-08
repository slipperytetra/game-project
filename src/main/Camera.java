package main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Camera {

    public int width, height;
    public Player player;
    public int[][] pixels;

    public Camera(Player p) {
        this.player = p;
        pixels = new int[width][height];
    }

    public BufferedImage getView() {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        return bufferedImage;
    }

    public Player getPlayer() {
        return player;
    }

}
