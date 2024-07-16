package block;

import entity.EntityLiving;
import level.Level;
import level.ParticleTypes;
import main.Camera;
import main.Game;
import main.GameObject;
import utils.CollisionBox;
import utils.Location;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class BlockGeyser extends BlockActive {

    private double particleRate = 0.25;
    private double particleTicks;

    private double eruptRate = 5;
    private double eruptTicks;
    private double eruptingCounter;

    private double attackTicks;
    private double attackRate = 0.15;

    private Random rand;
    private CollisionBox triggerBox;

    public BlockGeyser(Level level, Location loc, BlockTypes type) {
        super(level, loc, type);

        this.rand = new Random();
        this.triggerBox = new CollisionBox(getCollisionBox().getLocation().getX() - ((double)Game.BLOCK_SIZE / 2), getCollisionBox().getLocation().getY() - (Game.BLOCK_SIZE * 4),
                getCollisionBox().getWidth() + Game.BLOCK_SIZE, getCollisionBox().getHeight() + (Game.BLOCK_SIZE * 3));
        this.eruptTicks = rand.nextInt(0, (int)eruptRate);
    }

    @Override
    public void update(double dt) {
        System.out.println("test");
        if (particleTicks < particleRate) {
            particleTicks += 1 * dt;
        } else {
            getLevel().spawnParticle(ParticleTypes.SMOKE, getCenterX(), getLocation().getY() + 16);
            particleTicks = 0;
        }

        if (eruptTicks < eruptRate) {
            eruptTicks += 1 * dt;
        } else {
            if (eruptingCounter < 2) {
                eruptingCounter += 1 * dt;
                getLevel().spawnParticle(ParticleTypes.LAVA_BLOB, getCenterX(), getLocation().getY() + 16,
                        rand.nextDouble(-4, 4), -rand.nextDouble(Game.BLOCK_SIZE * 2, Game.BLOCK_SIZE * 3));

                if (attackTicks < attackRate) {
                    attackTicks += 1 * dt;
                } else {
                    List<GameObject> collisions = getLevel().getQuadTree().retrieve(triggerBox);
                    if (!collisions.isEmpty()) {
                        for (GameObject gameObject : collisions) {
                            if (gameObject instanceof EntityLiving entityLiving) {
                                entityLiving.damage(2, true);
                            }
                        }
                    }
                    attackTicks = 0;
                }
            } else {
                eruptingCounter = 0;
                eruptTicks = 0;
            }
        }
    }

    @Override
    public void render(Camera cam) {
        super.render(cam);

        if (cam.debugMode) {
            cam.game.changeColor(Color.RED);
            cam.game.drawRectangle(cam.toScreenX(triggerBox.getX()), cam.toScreenY(triggerBox.getY()), triggerBox.getWidth(), triggerBox.getHeight());
        }
    }
}
