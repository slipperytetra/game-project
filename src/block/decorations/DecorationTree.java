package block.decorations;

import level.Level;
import level.ParticleTypes;
import main.Location;

import java.util.Random;

public class DecorationTree extends Decoration {

    private Level level;
    private double particleCounter;
    private double particleFrequency = 5;
    private Random rand;

    public DecorationTree(Level level, Location loc, DecorationTypes type) {
        super(level, loc, type);

        this.level = level;
        this.rand = new Random();
        particleFrequency = rand.nextInt(1, 10);
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
    }
}
