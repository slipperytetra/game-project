package entity;

import level.Level;
import main.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class EntityLiving extends Entity {

    private int hitDamage;
    private EntityLiving target;

    private GameEngine.AudioClip soundHit;
    private GameEngine.AudioClip soundAttack;

    private AttackTimer attackTimer;

    private double attackCounter;
    private double attackCooldown;

    public double attackSearchTicks;
    public final double ATTACK_SEARCH_COOLDOWN = 1.0;

    public EntityLiving(EntityType type, Level level, Location loc) {
        super(type, level, loc);
        setAttackCooldown(1.0); //Default attack cooldown
        setShouldRespawn(true);

        System.out.println("New attack timer for " + type.toString());
        setAttackTimer(new AttackTimer(this));
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        if (attackCounter < attackCooldown) {
            attackCounter += 1 * dt;
        }
    }

    @Override
    public void processMovement(double dt) {
        super.processMovement(dt);
    }

    @Override
    public void kill() {
        super.kill();

        System.out.println("Killed");
        if (attackTimer.isRunning()) {
            System.out.println("stopped");
            attackTimer.stop();
        }
    }

    public int getDamage() {
        return hitDamage;
    }

    public void setDamage(int maxDamage) {
        hitDamage = maxDamage;
    }

    public EntityLiving getTarget() {
        return target;
    }

    public void setTarget(EntityLiving target) {
        this.target = target;
    }

    public GameEngine.AudioClip getHitSound() {
        return soundHit;
    }

    public void setHitSound(GameEngine.AudioClip sound) {
        this.soundHit = sound;
    }

    public GameEngine.AudioClip getAttackSound() {
        return soundAttack;
    }

    public void setAttackSound(GameEngine.AudioClip sound) {
        this.soundAttack = sound;
    }

    public TextureAnimated getAttackFrame() {
        return (TextureAnimated) getLevel().getManager().getEngine().getTexture(getType().toString().toLowerCase() + "_attack");
    }

    public void findTarget() {
        //System.out.println("Looking for target " + System.currentTimeMillis());
        if (getDistanceTo(getLevel().getPlayer()) < 64) {
            setTarget(getLevel().getPlayer());
            return;
        }

        setTarget(null);
    }

    public void attack() {
        if (getTarget() == null) {
            return;
        }

        if (getType() != EntityType.PLAYER) {
            double direction = getLocation().getX() - target.getLocation().getX();
            if (direction < 0) {
                setFlipped(true);
            } else {
                setFlipped(false);
            }
        }

        target.damage(this);
    }

    public double getAttackCooldown() {
        return attackCooldown;
    }

    public boolean isAttacking() {
        return attackCounter < getAttackCooldown();
    }

    public double getAttackTicks() {
        return attackCounter;
    }

    public void setAttackTicks(double num) {
        this.attackCounter = num;
    }

    public boolean canAttack() {
        return getAttackTicks() >= getAttackCooldown() && getTarget() != null;
    }

    public void damage(EntityLiving attacker) {
        setHealth(getHealth() - attacker.getDamage());
        if (getHitSound() != null) {
            getLevel().getManager().getEngine().playAudio(getHitSound());
        }
    }

    public void setAttackCooldown(double cooldown) {
        this.attackCooldown = cooldown;
        setAttackTicks(getAttackCooldown() - 0.01);
    }


    public boolean isInRange(Entity entity) {
        if (entity == null) {
            return false;
        }

        return getLocation().calculateDistance(entity.getLocation()) < 64;
    }

    public AttackTimer getAttackTimer() {
        return attackTimer;
    }

    public void setAttackTimer(AttackTimer timer) {
        this.attackTimer = timer;
    }
}
