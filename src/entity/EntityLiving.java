package entity;

import level.Level;
import main.*;
import utils.Location;
import utils.TextureAnimated;

public abstract class EntityLiving extends Entity {

    private int hitDamage;
    private EntityLiving target;

    private SoundType soundHit;
    private SoundType soundAttack;

    private AttackTimer attackTimer;

    private double attackCounter;
    private double attackCooldown;
    private double attackRange;

    public double attackSearchTicks;
    public double ATTACK_SEARCH_COOLDOWN = 1.0;

    public EntityLiving(EntityType type, Level level, Location loc) {
        super(type, level, loc);
        setAttackCooldown(1.0); //Default attack cooldown
        setShouldRespawn(true);
        this.attackRange = Game.BLOCK_SIZE * 2.5;

        //System.out.println("New attack timer for " + type.toString());
        setAttackTimer(new AttackTimer(this));
    }

    @Override
    public void initAttributes() {
        super.initAttributes();

        setAttribute(AttributeTypes.ATTACK_DAMAGE, AttributeTypes.ATTACK_DAMAGE.getDefaultValue());
        setAttribute(AttributeTypes.ATTACK_RANGE, AttributeTypes.ATTACK_RANGE.getDefaultValue());
        setAttribute(AttributeTypes.ATTACK_RESISTANCE, AttributeTypes.ATTACK_RESISTANCE.getDefaultValue());
        setAttribute(AttributeTypes.ATTACK_SPEED, AttributeTypes.ATTACK_SPEED.getDefaultValue());
        setAttribute(AttributeTypes.PROJECTILE_DAMAGE, AttributeTypes.PROJECTILE_DAMAGE.getDefaultValue());
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
        if (this instanceof Player p) {
            if (p.getItemInHand() != null) {
                return p.getItemInHand().getItemType().getDamage();
            }
        }
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

    public SoundType getHitSound() {
        return soundHit;
    }

    public void setHitSound(SoundType sound) {
        this.soundHit = sound;
    }

    public SoundType getAttackSound() {
        return soundAttack;
    }

    public void setAttackSound(SoundType sound) {
        this.soundAttack = sound;
    }

    public TextureAnimated getAttackFrame() {
        return (TextureAnimated) getLevel().getManager().getEngine().getTextureBank().getTexture(getType().toString().toLowerCase() + "_attack");
    }

    public void findTarget() {
        //System.out.println("Looking for target " + System.currentTimeMillis());
        if (getDistanceTo(getLevel().getPlayer()) < getAttackRange()) {
            setTarget(getLevel().getPlayer());
            return;
        }

        setTarget(null);
    }

    public void attack() {
        if (getTarget() == null || getLevel().isEditMode() || getLevel().getManager().getEngine().isPaused) {
            return;
        }

        if (getType() != EntityType.PLAYER) {
            double direction = getLocation().getX() - target.getLocation().getX();
            if (direction < 0) {
                setFlipped(true);
            } else {
                setFlipped(false);
            }
        } else {
            getLevel().getManager().getEngine().getCamera().shake(10);
        }

        target.damage(this, true);
    }

    public double getAttackCooldown() {
        return attackCooldown;
    }

    public boolean isAttacking() {
        return attackCounter > 0 && attackCounter < getAttackCooldown();
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

    public void damage(EntityLiving attacker, boolean playSound) {
        setHealth(getHealth() - attacker.getDamage());
        if (getHitSound() != null && playSound) {
            getLevel().getManager().getEngine().getAudioBank().playSound(getHitSound());
        }
    }

    public void damage(int damage, boolean playSound) {
        setHealth(getHealth() - damage);
        if (getHitSound() != null && playSound) {
            getLevel().getManager().getEngine().getAudioBank().playSound(getHitSound());
        }
    }

    public void setAttackCooldown(double cooldown) {
        this.attackCooldown = cooldown;
        this.ATTACK_SEARCH_COOLDOWN = cooldown;
        setAttackTicks(getAttackCooldown() - 0.01);
        if (attackTimer != null) {
            attackTimer.setDelay((int) getAttackCooldown() * 1000);
        }
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

    public double getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(double range) {
        this.attackRange = range;
    }
}
