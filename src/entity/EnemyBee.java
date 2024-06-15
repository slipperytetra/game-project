package entity;

import level.Level;
import main.Game;
import main.SoundType;
import utils.Location;
import utils.Texture;

public class EnemyBee extends Enemy {
    public EnemyBee(Level level, Location loc) {
        super(EntityType.BEE, level, loc);

        setTarget(getLevel().getPlayer());
        setAttackRange(Game.BLOCK_SIZE * 15);

        setHitSound(SoundType.GENERIC_HIT);
        setAttackSound(null);
        setAttackCooldown(2.0);
    }

    @Override
    public void attack() {
        if (getTarget() == null) {
            return;
        }

        if (getType() != EntityType.PLAYER) {
            double direction = getLocation().getX() - getTarget().getLocation().getX();
            if (direction < 0) {
                setFlipped(true);
            } else {
                setFlipped(false);
            }
        }

        shootProjectile();
    }

    private void shootProjectile() {
        Location spawnLoc = new Location(getLocation().getX() + (getWidth() / 2), getLocation().getY() + getHeight());
        ProjectileStinger proj = new ProjectileStinger(this, getLevel(), spawnLoc,
                getTarget().getLocation().getX() + (getTarget().getHitboxWidth() / 2),
                getTarget().getLocation().getY() + (getTarget().getHitboxHeight() / 2));
        proj.getLocation().setY(proj.getLocation().getY() - proj.getHeight());
        getLevel().addEntity(proj);
        getLevel().getManager().getEngine().getAudioBank().playSound(SoundType.STINGER_SHOOT);
    }

    @Override
    public Texture getActiveFrame() {
        Texture texture = getIdleFrame();

        if (isAttacking()) {
            texture = getAttackFrame();
        }

        texture.setFlipped(isFlipped());

        return texture;
    }
}
