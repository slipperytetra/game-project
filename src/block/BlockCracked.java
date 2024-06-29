package block;

import entity.EnemyPlant;
import entity.EntityLiving;
import entity.Player;
import level.Level;
import level.Particle;
import level.ParticleTypes;
import main.GameObject;
import main.SoundType;
import utils.CollisionBox;
import utils.Location;
import utils.Texture;

import java.util.List;
import java.util.Random;

public class BlockCracked extends BlockActive {

    private double crackCooldown = 0.5;
    private double crackTimer;
    private boolean cracking;
    boolean hasCracked;
    private Random rand;
    private CollisionBox triggerBox;

    public BlockCracked(Level level, Location loc, BlockTypes type) {
        super(level, loc, type);

        this.rand = new Random();
        this.hasCracked = false;
        this.triggerBox = new CollisionBox(getCollisionBox().getLocation().getX(), getCollisionBox().getLocation().getY() - 4,
                getCollisionBox().getWidth(), getCollisionBox().getHeight() + 4);
        setCollidable(true);
        setIsSolid(true);

        /*
        this.setHitboxWidth(getHitboxWidth() + 4);
        this.setHitboxHeight(getHitboxHeight() + 4);
        setHitboxOffsetX(-2);
        setHitboxOffsetY(-2);*/
    }

    @Override
    public void update(double dt) {
        if (getState() >= getMaxStates() - 1) {
            return;
        }

        if (hasCracked) {
            return;
        }

        if (cracking) {
            if (crackTimer < crackCooldown) {
                crackTimer += 1 * dt;
            } else {
                crackTimer = 0;
                setState(getState() + 1);
                for (int i = 0; i < rand.nextInt(0, 3); i++) {
                    getLevel().spawnParticle(ParticleTypes.DIRT, getCenterX() + rand.nextDouble(-14, 14) , getCenterY() + rand.nextDouble(-14, 14),
                            rand.nextDouble(-0.5, 0.5), rand.nextDouble(-1, 1));
                }

                if (getState() >= getMaxStates() - 1) {
                    breakBlock();
                }
            }
        } else {
            List<GameObject> collisions = getLevel().getQuadTree().query(this, triggerBox);

            if (!collisions.isEmpty()) {
                for (GameObject obj : collisions) {
                    if (obj instanceof EntityLiving entity) {
                        cracking = true;
                        getLevel().playSound(SoundType.STONE_CRACK);
                    }
                }
            }

            /*
            Player p = getLevel().getPlayer();
            if ((p.getLocation().getY() >= getLocation().getY() - p.getHitboxHeight() - 16 && p.getLocation().getY() <= getLocation().getY()) && (p.getLocation().getX() >= getLocation().getX() && p.getLocation().getX() <= getLocation().getX() + 32)) {
                cracking = true;
                getLevel().playSound(SoundType.STONE_CRACK);
            }*/
        }
    }

    @Override
    public Texture getTexture() {
        if (!isCollidable() && !getLevel().isEditMode()) {
            return null;
        }

        return getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString() + "_" + getState());
    }

    @Override
    public boolean isCollidable() {
        return getState() < getMaxStates() - 1;
    }

    private void breakBlock() {
        hasCracked = true;
        getLevel().playSound(SoundType.STONE_CRUMBLE);

        for (int i = 0; i < 4; i++) {
            ParticleTypes type = ParticleTypes.valueOf("GROUND_CRACKED_" + i);
            double velX = type.getVelX() + rand.nextDouble(-0.25, 0.25);
            double velY = type.getVelY() + rand.nextDouble(-0.25, 0.25);

            Particle particle = new Particle(type, new Location(getLocation().getX() + 16, getLocation().getY() + 16), getLevel());
            particle.setVelX(velX);
            particle.setVelY(velY);
            particle.setTimeAlive(type.getTimeAlive() + rand.nextDouble(0, type.getTimeAlive()));
            getLevel().spawnParticle(particle);
        }
    }
}
