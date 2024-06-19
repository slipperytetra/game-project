package entity;

import level.Level;
import main.Game;
import utils.Location;

public class ProjectileStinger extends Projectile {
    public ProjectileStinger(Entity shooter, Level level, Location loc, double targetX, double targetY) {
        super(shooter, EntityType.STINGER, level, loc, Game.BLOCK_SIZE * 8, targetX, targetY);

        setCanMove(true);
        setScale(1);
        setDamage(5);
        setHasGravity(false);
    }
}
