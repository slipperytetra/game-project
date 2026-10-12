package utils;

import level.Level;

import java.awt.image.BufferedImage;

public class BackgroundLayer {

    private Texture texture;
    private String imageFilePath;
    private double scaleX;
    private double scaleY;

    public BackgroundLayer(Texture texture, String filePath, int levelWidth, int levelHeight) {
        this.texture = texture;
        imageFilePath = filePath;
        scaleX = (double) levelWidth / texture.getWidth();
        scaleY = (double) levelHeight / texture.getHeight();
    }

    public BackgroundLayer(String filePath) {
        texture = null;
        imageFilePath = filePath;
        scaleX = 1;
        scaleY = 1;
    }

    public void init(Level level) {
        if (level == null || getImageFilePath().isEmpty()) {
            return;
        }

        texture = new Texture((BufferedImage) level.getManager().getEngine().loadImage(getImageFilePath()));
        scaleX = (double) level.getActualWidth() / texture.getWidth();
        scaleY = (double) level.getActualHeight() / texture.getHeight();
    }

    public Texture getTexture() {
        return texture;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }
}