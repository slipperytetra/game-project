package entity;

import entity.utils.AttackTimer;
import entity.utils.AttributeTypes;
import entity.utils.DeathAnimationTimer;
import level.Level;
import main.*;
import utils.Location;
import utils.Texture;
import utils.TextureAnimated;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class EntityLiving extends Entity {

    private EntityLiving target;
    private SoundType soundHit;
    private SoundType soundAttack;
    private AttackTimer attackTimer;
    public DeathAnimationTimer deathTimer;

    private int hitDamage;
    private double attackCounter;
    public double attackSearchTicks;
    public double ATTACK_SEARCH_COOLDOWN = 1.0;
    private boolean isInvulnerable;

    public final Color tint = new Color(255, 85, 85);

    private final double hurtCooldown = 0.25;
    private double hurtTicks;

    public EntityLiving(EntityType type, Level level, Location loc) {
        super(type, level, loc);
        setAttackCooldown(1.0); //Default attack cooldown
        setShouldRespawn(true);

        //System.out.println("New attack timer for " + type.toString());
        setAttackTimer(new AttackTimer(this));
        setDeathAnimationTimer(new DeathAnimationTimer(this));
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

        if (attackCounter < getAttackCooldown()) {
            attackCounter += 1 * dt;
        }

        if (getHurtTicks() > 0) {
            hurtTicks -= 1 * dt;
        }
    }

    /*
    @Override
    public void processMovement(double dt) {
        super.processMovement(dt);
    }*/

    @Override
    public void kill() {
        super.kill();

        System.out.println("Killed");
        if (attackTimer.isRunning()) {
            System.out.println("stopped");
            attackTimer.stop();
        }

        if (!deathTimer.isRunning()) {
            System.out.println("death started");
            deathTimer.start();
        }
    }

    public double getDamage() {
        if (this instanceof Player p) {
            if (p.getItemInHand() != null) {
                return p.getItemInHand().getItemType().getDamage();
            }
        }
        return getAttributeValue(AttributeTypes.ATTACK_DAMAGE);
    }

    public void setDamage(int maxDamage) {
        setAttribute(AttributeTypes.ATTACK_DAMAGE, maxDamage);
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
        return getAttributeValue(AttributeTypes.ATTACK_SPEED);
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
        if (getHurtTicks() > 0 || isInvulnerable()) {
            return;
        }

        setHealth(getHealth() - attacker.getDamage());
        if (getHitSound() != null && playSound) {
            getLevel().getManager().getEngine().getAudioBank().playSound(getHitSound());
        }

        setHurtTicks(getHurtCooldown());
    }

    public void damage(int damage, boolean playSound) {
        if (getHurtTicks() > 0) {
            return;
        }

        setHealth(getHealth() - damage);
        if (getHitSound() != null && playSound) {
            getLevel().getManager().getEngine().getAudioBank().playSound(getHitSound());
        }

        setHurtTicks(getHurtCooldown());
    }

    public void setAttackCooldown(double cooldown) {
        setAttribute(AttributeTypes.ATTACK_SPEED, cooldown);
        this.ATTACK_SEARCH_COOLDOWN = cooldown;
        setAttackTicks(getAttackCooldown() - 0.01);
        if (attackTimer != null) {
            attackTimer.setDelay((int) getAttackCooldown() * 1000);
        }
    }

    public AttackTimer getAttackTimer() {
        return attackTimer;
    }

    public void setAttackTimer(AttackTimer timer) {
        this.attackTimer = timer;
    }
    public void setDeathAnimationTimer(DeathAnimationTimer timer) {
        this.deathTimer = timer;
    }

    public double getAttackRange() {
        return getAttributeValue(AttributeTypes.ATTACK_RANGE);
    }

    public void setAttackRange(double range) {
        this.setAttribute(AttributeTypes.ATTACK_RANGE, range);
    }



    @Override
    public void render(Camera cam) {
        if (deathTimer.isRunning()) {
            Graphics2D g2d = cam.game.mGraphics;
            AffineTransform oldTrans = g2d.getTransform();
            g2d.translate((int)cam.toScreenX(getLocation().getX()), (int)cam.toScreenY(getLocation().getY()));
            g2d.rotate(getRotation(), getWidth()/2, getHeight()/2);
            g2d.scale(getScale(), getScale());
            g2d.drawImage(getActiveFrame().getImage(), 0, 0, null);
            g2d.setTransform(oldTrans);
            return;
        }

        cam.game.drawImage(getActiveFrame().getImage(), cam.toScreenX(getLocation().getX()), cam.toScreenY(getLocation().getY()), getWidth(), getHeight());

        if (cam.debugMode) {
            cam.game.changeColor(getHitboxColor());
            cam.game.drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() , getCollisionBox().getHeight() );
        }
    }

    @Override
    public boolean isActive() {
        if (deathTimer.isRunning()) {
            return true;
        }

        return super.isActive();
    }

    @Override
    public Texture getActiveFrame() {
        if (getHurtTicks() < getHurtCooldown()) {
            Texture texture = getIdleFrame();
            int width = texture.getWidth();
            int height = texture.getHeight();

            // Create a new BufferedImage with the same dimensions and type as the original
            BufferedImage tintedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Manipulate pixels to apply tint
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Get RGB color of the pixel
                    int rgb = tintedImage.getRGB(x, y);

                    // Apply tinting by combining the pixel's color with the tint color
                    int red = (rgb >> 16) & 0xFF; // Red component
                    int green = (rgb >> 8) & 0xFF; // Green component
                    int blue = rgb & 0xFF; // Blue component

                    // Tint calculation
                    red = (int) (red * tint.getRed() / 255.0);
                    green = (int) (green * tint.getGreen() / 255.0);
                    blue = (int) (blue * tint.getBlue() / 255.0);

                    // Update RGB value with tint
                    rgb = (rgb & 0xFF000000) | (red << 16) | (green << 8) | blue;

                    // Set the updated pixel color in tintedImage
                    tintedImage.setRGB(x, y, rgb);
                }
            }

            //System.out.println("returning tinted img");
            return new Texture(tintedImage);
        }

        return getIdleFrame();
    }

    public double getHurtTicks() {
        return hurtTicks;
    }

    public void setHurtTicks(double hurtTicks) {
        this.hurtTicks = hurtTicks;
    }

    public double getHurtCooldown() {
        return hurtCooldown;
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        isInvulnerable = invulnerable;
    }
}