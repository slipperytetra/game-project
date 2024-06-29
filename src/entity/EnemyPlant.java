package entity;

import level.Level;
import main.Camera;
import utils.Location;
import main.SoundType;
import utils.Texture;

public class EnemyPlant extends Enemy {

    public EnemyPlant(Level level, Location loc) {
        super(EntityType.PLANT_MONSTER, level, loc);

        setDamage(5);
        setMaxHealth(20);
        setHealth(20);
        setScale(1);
        setAttackCooldown(2.0);

        setHitSound(SoundType.GENERIC_HIT);
        setAttackSound(null);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
    }

    @Override
    public void render(Camera cam) {
        double offsetX = cam.toScreenX(getLocation().getX());
        double offsetY = cam.toScreenY(getLocation().getY());

        if (isAttacking()) {
            int diffX = -70;
            int diffY = -17;

            if (isFlipped()) {
                diffX = 8;
            }
            offsetX = offsetX + diffX;
            offsetY = offsetY + diffY;
        }

        getLevel().getManager().getEngine().drawImage(getActiveFrame().getImage(), offsetX , offsetY , getWidth() , getHeight() );

        if (cam.debugMode) {
            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() , getCollisionBox().getHeight() );
        }
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