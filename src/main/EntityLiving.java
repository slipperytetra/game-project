package main;

import level.Level;

public abstract class EntityLiving extends Entity {

    private int hitDamage;
    private EntityLiving target;

    private GameEngine.AudioClip soundHit;
    private GameEngine.AudioClip soundAttack;

    private double attackCounter;
    private double attackCooldown;

    public EntityLiving(EntityType type, Level level, Location loc, int hitboxWidth, int hitboxHeight) {
        super(type, level, loc, hitboxWidth, hitboxHeight);

        setAttackCooldown(0.4);
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

    public void attack() {
        if (!canAttack()) {
            return;
        }

        if (getAttackSound() != null) {
            getLevel().getManager().getEngine().playAudio(getAttackSound());
        }

        setAttackTicks(0);

        EntityLiving target = getTarget();
        if (target == null) {
            return;
        }
        System.out.println(target.getHealth());

        double direction = getLocation().getX() - getTarget().getLocation().getX();
        if (direction < 0) {
            setFlipped(true);
        } else {
            setFlipped(false);
        }

        getTarget().damage(this);
        //System.out.println(target.getHealth());
        if (target.getHealth() <= 0){
            target.setDamage(0);
            target.destroy();
        }
    }

    public double getAttackCooldown() {
        return attackCooldown;
    }

    public boolean isAttacking() {
        return attackCounter <= getAttackCooldown();
    }

    public double getAttackTicks() {
        return attackCounter;
    }

    public void setAttackTicks(double num) {
        this.attackCounter = num;
    }

    public boolean canAttack() {
        return getAttackTicks() >= getAttackCooldown();
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
}
