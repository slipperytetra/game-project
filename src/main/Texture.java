package main;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Texture {

    public BufferedImage image;
    private boolean isFlipped;

    public Texture(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        if (isFlipped()) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1.0, 1.0);
            tx.translate(-image.getWidth(null), 0.0);
            AffineTransformOp op = new AffineTransformOp(tx, 1);
            return op.filter(image, null);
        }

        return image;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }
}
