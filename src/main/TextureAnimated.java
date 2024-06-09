package main;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.List;

public class TextureAnimated extends Texture {

    int maxFrames;
    double frameCounter;
    int frameRate;
    BufferedImage[] frames;
    boolean repeat;
    public TextureAnimated(BufferedImage[] frames, int frameRate) {
        super(frames[0]);

        this.frames = frames;
        this.frameRate = frameRate;
        this.maxFrames = frames.length - 1;
        this.repeat = true;
    }
    public TextureAnimated(BufferedImage[] frames, int frameRate, boolean repeat) {
        super(frames[0]);

        this.frames = frames;
        this.frameRate = frameRate;
        this.maxFrames = frames.length - 1;
        this.repeat = repeat;
    }

    public void update(double dt) {
        if (frameCounter < maxFrames) {
            frameCounter += (frameRate * dt);
            if (frameCounter > maxFrames) {
                frameCounter = maxFrames;
            }
        } else {
            if (repeat) {
                frameCounter = 0;
            } else {
                frameCounter = maxFrames;
            }
        }
    }

    @Override
    public BufferedImage getImage() {
        BufferedImage img = frames[(int) frameCounter];
        if (isFlipped()) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1.0, 1.0);
            tx.translate(-img.getWidth(null), 0.0);
            AffineTransformOp op = new AffineTransformOp(tx, 1);
            return op.filter(img, null);
        }

        return img;
    }

    public BufferedImage[] getFrames() {
        return frames;
    }

    public double getFrameIndex() {
        return frameCounter;
    }

    public void setFrameIndex(int index) {
        this.frameCounter = index;
    }
}
