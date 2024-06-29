package utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class Texture {

    public BufferedImage image;
    private boolean isFlipped;
    private double rotation;

    public Texture(BufferedImage image) {
        this.image = image;
        this.rotation = 0;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double theta) {
        this.rotation = theta;
    }

    public BufferedImage getImage() {
        if (isFlipped() || getRotation() != 0) {
            BufferedImage img = image;

            if (isFlipped()) {
                AffineTransform tx = AffineTransform.getScaleInstance(-1.0, 1.0);
                tx.translate(-img.getWidth(null), 0.0);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                img = op.filter(img, null);
            }

            if (getRotation() != 0) {
                AffineTransform tx = AffineTransform.getRotateInstance(getRotation(), (double) getWidth() / 2, (double) getHeight() / 2);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                img = op.filter(img, null);
            }

            setRotation(0);
            return img;
        }

        return image;
    }

    public BufferedImage getImageNoAffine() {
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
