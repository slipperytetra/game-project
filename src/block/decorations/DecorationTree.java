package block.decorations;

import level.Level;
import level.ParticleTypes;
import main.Camera;
import main.Game;
import utils.Location;
import utils.WaveEffect;

import java.awt.image.BufferedImage;
import java.util.Random;

public class DecorationTree extends Decoration {

    private Level level;
    private double particleCounter;
    private double particleFrequency = 5;
    private Random rand;

    double time = 0;
    WaveEffect waveEffect;

    BufferedImage distortedImage;

    public DecorationTree(Level level, Location loc, DecorationTypes type) {
        super(level, loc, type);

        this.level = level;
        this.rand = new Random();
        particleFrequency = rand.nextInt(1, 10);
        waveEffect = new WaveEffect(3, 20, 0, 2, 40, Math.PI / 4, 0.5, 1);
    }

    @Override
    public void render(Camera cam) {
        //super.render(cam);
        cam.game.drawImage(distortedImage, cam.toScreenX(getLocation().getX()), cam.toScreenY(getLocation().getY() - getHeight() + Game.BLOCK_SIZE));
    }

    public void update(double dt) {
        super.update(dt);

        if (particleCounter < particleFrequency) {
            particleCounter += 1 * dt;
        }

        if  (particleCounter >= particleFrequency) {
            particleFrequency = rand.nextInt(5, 10);
            level.spawnParticle(ParticleTypes.LEAF, getLocation().getX() + (getWidth() / 2), getLocation().getY() - (getHeight() / 2));
            particleCounter = 0;
        }

        int width = texture.getWidth();
        int height = texture.getHeight();
        distortedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double yOffset = waveEffect.getOffset(x, y, time);
                int newY = (int) (y + yOffset);
                if (newY >= 0 && newY < height) {
                    distortedImage.setRGB(x, newY, texture.getRGB(x, y));
                }
            }
        }

        time += 1 * dt;

    }
}
