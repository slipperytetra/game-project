package entity;

import level.Level;
import level.ParticleTypes;
import main.Game;
import main.SoundType;
import utils.Location;
import utils.Vector;

public class ProjectileArrow extends Projectile {

    public ProjectileArrow(Entity shooter, Level level, Location loc, double targetX, double targetY) {
        super(shooter, EntityType.ARROW, level, loc, Game.BLOCK_SIZE * 25, targetX, targetY);

        setScale(1.5);
        setDamage(5);
        setHitboxOffsetX(16);
        setHitboxOffsetY(16);
        setHitboxWidth(8);
        setHitboxHeight(8);

        setTrail(ParticleTypes.ARROW_TRAIL);
        setImpactSound(SoundType.ARROW_IMPACT);
    }

    @Override
    public void kill() {
        super.kill();

        //getLevel().getManager().getEngine().getAudioBank().playSound(SoundType.BLOCK_BREAK);

        // Need to add formula to calculate intersection of collisions so i can get exactly where it hit the block/entity.
        //getLevel().spawnParticle(ParticleTypes.CLOUD, getLocation().getX(), getLocation().getY());
    }
}
