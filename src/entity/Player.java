package entity;

import block.Block;
import block.BlockClimbable;
import block.BlockTypes;
import level.Level;
import level.ParticleTypes;
import level.item.Inventory;
import level.item.InventoryItem;
import level.item.InventoryItemSlot;
import level.item.ItemType;
import main.*;
import utils.Location;
import utils.Texture;
import utils.TextureAnimated;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Player extends EntityLiving {
    private JProgressBar healthBar;
    private boolean isJumping;
    private boolean hasKey;

    private double keyPressTimer;
    private double KEY_PRESS_COOLDOWN = 0.05;

    private double jumpTimer;
    private double jumpTimerCooldown;

    private double runParticleTimer;
    private double RUN_PARTICLE_FREQUENCY = 0.075;

    private int coins;
    private double MAX_SPEED = Game.BLOCK_SIZE * 11;

    private Inventory backPack;

    public Player(Level level, Location loc) {
        super(EntityType.PLAYER, level, loc);
        this.backPack = new Inventory(5, 1);
        setHitboxColor(Color.cyan);
        setAttackCooldown(0.5);
        setMaxHealth(100);
        setDamage(1);
        setHealth(getMaxHealth());
        setAttackRange(Game.BLOCK_SIZE * 2.5);
        setCollidable(true);
        setHitboxWidth(14);
        setHitboxOffsetX(4);
        init();

        getBackPack().addItem(ItemType.SWORD, 1);
        getBackPack().addItem(ItemType.BOW, 1);
        getBackPack().addItem(ItemType.ARROW, 32);

        File file = new File("saves/player_save.txt");
        try {
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                if (line.startsWith("coins: ")) {
                    setCoins(Integer.parseInt(line.replaceAll("coins: ", "")));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't locate file!");
            return;
        }
    }

    public void init() {
        setHitSound(SoundType.PLAYER_HIT);
        setAttackSound(SoundType.PLAYER_ATTACK);

        this.healthBar = new JProgressBar(0, getMaxHealth());
        this.healthBar.setBounds(100, 25, 100, 10); // Adjust position and size as needed
        this.healthBar.setForeground(Color.RED); // Set the color
        this.healthBar.setValue(getMaxHealth()); // Set initial health
        this.healthBar.setStringPainted(true); // Show health value
    }

    public void update(double dt) {
        super.update(dt);
        //System.out.println(getVelocity().getX() + ", " + getVelocity().getY());
        if (keyPressTimer < KEY_PRESS_COOLDOWN) {
            keyPressTimer += 1 * dt;
        } else {
            //System.out.println("Test");
            keyPressTimer = 0;
        }

        if (jumpTimer < jumpTimerCooldown) {
            jumpTimer += 1 * dt;
        } else {
            //System.out.println("Test");
            jumpTimer = 0;
        }

        if (runParticleTimer < RUN_PARTICLE_FREQUENCY) {
            runParticleTimer += 1 * dt;
        }

        if  (runParticleTimer >= RUN_PARTICLE_FREQUENCY) {
            if(isMovingHorizontally() && isOnGround()){
                double partVelX = 0.75;
                double partVelY = -0.5;
                double offsetX = getWidth() / 2;
                if (isFlipped()) {
                    partVelX *= -1;
                    offsetX *= -1;
                }

                //getLevel().getManager().getEngine().getAudioBank().playSound(SoundType.PLAYER_RUN);
                getLevel().spawnParticle(ParticleTypes.CLOUD, getCenterX() + offsetX, getLocation().getY() + getHitboxHeight(), partVelX, partVelY, isFlipped());
                runParticleTimer = 0;
            }
        }
    }

    public boolean hasKey() {
        return hasKey;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }


    @Override
    public void processMovement(double dt) {
        //super.processMovement(dt);
        /*moveX = getDirectionX() * (speed * dt);
        moveY = getDirectionY() * (speed * dt);

        moveX(moveX);
        moveY(moveY);

        if (isJumping()) {
            setDirectionY(-1.5);
            timeJumping += 1 * dt;

            if (timeJumping > maxJumpTime) {
                this.setJumping(false);
                this.setDirectionY(0);
                this.timeJumping = 0;
            }
            return;
        }

        if (!getLevel().isEditMode()) {
            if (isFalling() && !canClimb()) {
                if (fallAccel > 0) {
                    fallAccel *= fallSpeedMultiplier;
                    setDirectionY(1 * fallAccel);
                }
            } else if (!isFalling()) {
                fallAccel = 1;
                setDirectionY(0);
            }
        }*/
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    @Override
    public void render(Camera cam) {
        double playerOffsetX = cam.toScreenX(getLocation().getX());
        double playerOffsetY = cam.toScreenY(getLocation().getY());
        Game game = getLevel().getManager().getEngine();

        if (isAttacking()) {
            playerOffsetX = playerOffsetX - 31;
            playerOffsetY = playerOffsetY - 8;
        }

        game.drawImage(getActiveFrame().getImage(), playerOffsetX , playerOffsetY , getWidth() , getHeight() );

        if (cam.debugMode) {
            game.changeColor(Color.magenta);
            game.changeColor(getHitboxColor());
            game.drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() , getCollisionBox().getHeight() );
        }
    }

    public void playerMovement(Set<Integer> keysPressed) {
        if (keyPressTimer >= KEY_PRESS_COOLDOWN) {
            if (getLevel().isEditMode()) {
                return;
            }

            if (keysPressed.contains(32)) {//SPACE
                if (!isAttacking() && (isOnGround() || canClimb() || getLevel().isEditMode()) && jumpTimer >= jumpTimerCooldown) {
                    if (!canClimb()) {
                        getVelocity().setY(-Game.BLOCK_SIZE * 16);
                    } else {
                        getVelocity().setY(-Game.BLOCK_SIZE * 13);
                    }
                    this.getJumpFrame().setFrameIndex(0);
                    jumpTimer = 0;
                    setJumping(true);
                }
            }
            if (keysPressed.contains(9)) {//TAB
                if (getBackPack() != null) {
                    getBackPack().setOpen(!getBackPack().isOpen(), getLevel());
                    if (getBackPack().isOpen()) {
                        getLevel().playSound(SoundType.BACKPACK_OPEN);
                    } else {
                        getLevel().playSound(SoundType.BACKPACK_CLOSE);
                    }
                    keysPressed.remove(9);
                }
            }
            if (keysPressed.contains(37)) {//LEFT
                if (getBackPack().isOpen()) {
                    int slot = getBackPack().getSelectedSlot();
                    if (slot < 1) {
                        slot = getBackPack().getWidth() - 1;
                    } else {
                        slot--;
                    }
                    getLevel().playSound(SoundType.MENU_NAVIGATE);
                    getBackPack().setSelectedSlot(slot);
                    keysPressed.remove(37);
                }
            }
            if (keysPressed.contains(39)) {//RIGHT
                if (getBackPack().isOpen()) {
                    int slot = getBackPack().getSelectedSlot();
                    if (slot >= getBackPack().getWidth() - 1) {
                        slot = 0;
                    } else {
                        slot++;
                    }
                    getLevel().playSound(SoundType.MENU_NAVIGATE);
                    getBackPack().setSelectedSlot(slot);
                    keysPressed.remove(39);
                }
            }
            if (keysPressed.contains(87)) {//W
                if (canClimb() && getBlockAtLocation(0, 0).getType() != BlockTypes.VOID) {
                    getVelocity().setY(-Game.BLOCK_SIZE * 13);
                }
            }
            if (keysPressed.contains(65)) {//A
                if (getVelocity().getX() > -MAX_SPEED) {
                    getVelocity().setX(getVelocity().getX() + -ACCELERATION);
                    if (getVelocity().getX() < -MAX_SPEED) {
                        getVelocity().setX(-MAX_SPEED);
                    }
                }
            }
            if (keysPressed.contains(83)) {//S
                if (canClimb() || getLevel().isEditMode()) {
                    getVelocity().setY(256);
                }
            }
            if (keysPressed.contains(68)) {//D
                if (getVelocity().getX() < MAX_SPEED) {
                    getVelocity().setX(getVelocity().getX() + ACCELERATION);
                    if (getVelocity().getX() > MAX_SPEED) {
                        getVelocity().setX(MAX_SPEED);
                    }
                }
            }

            if (keysPressed.contains(81)) {
                if (getItemInHand() != null && getItemInHand().getItemType() == ItemType.SWORD) {
                    attemptAttack(false);
                } else if (getItemInHand() != null && getItemInHand().getItemType() == ItemType.BOW) {
                    attemptAttack(true);
                }
            }
            keyPressTimer = 0;
        }
    }

    public void attemptAttack(boolean shoot) {
        if (getAttackTicks() >= getAttackCooldown()) {
            if (!shoot) {
                if (getAttackSound() != null) {
                    getLevel().getManager().getEngine().getAudioBank().playSound(getAttackSound());
                }
                getAttackFrame().setFrameIndex(0);
                setAttackTicks(0);

                findTarget();

                if (getTarget() != null) {
                    attack();
                }
            } else {
                if (!getBackPack().containsItem(ItemType.ARROW) && !getLevel().getManager().getEngine().getCamera().debugMode) {
                    return;
                }

                Location spawnLoc = new Location(getLocation().getX(), getLocation().getY());
                ProjectileArrow proj = new ProjectileArrow(this, getLevel(), spawnLoc, getLevel().getManager().getEngine().mouseX, getLevel().getManager().getEngine().mouseY);
                proj.setLocation(getLocation().getX() + (getHitboxWidth() / 2) - (proj.getWidth() / 2), getLocation().getY());
                proj.offsetTrajectory(32);
                proj.setDamage(getDamage());
                getLevel().addEntity(proj);
                getLevel().getManager().getEngine().getAudioBank().playSound(SoundType.STINGER_SHOOT);
                setAttackTicks(0);
                for (InventoryItemSlot item : getBackPack().getItems()) {
                    if (item.getItem() == null) {
                        continue;
                    }

                    if (item.getItem().getItemType() == ItemType.ARROW) {
                        item.getItem().setAmount(item.getItem().getAmount() - 1);
                        getBackPack().update();
                    }
                }
            }
        }
    }

    @Override
    public void findTarget() {
        for (Entity entity : getLevel().getEntities()) {
            if (!entity.isActive()) {
                continue;
            }

            if (entity instanceof EntityLiving lEntity && entity.getType() != this.getType()) {
                if (getDistanceTo(lEntity) < getAttackRange()) {
                    setTarget(lEntity);
                    return;
                }
            }
        }

        setTarget(null);
    }

    public JProgressBar getHealthBar() {
        return healthBar;
    }

    @Override
    public double getWidth() {
        if (isAttacking()) {
            return 50 * getScale();
        }

        return getIdleFrame().getWidth() * getScale();
    }

    @Override
    public double getHeight() {
        if (isAttacking()) {
            return 37 * getScale();
        }

        return getIdleFrame().getHeight() * getScale();
    }

    @Override
    public void kill() {
        getLevel().reset();
    }

    public Texture getRunFrame() {
        return getLevel().getManager().getEngine().getTextureBank().getTexture("player_run");
    }

    public Texture getFallFrame() {
        return getLevel().getManager().getEngine().getTextureBank().getTexture("player_fall");
    }

    public TextureAnimated getJumpFrame() {
        return (TextureAnimated) getLevel().getManager().getEngine().getTextureBank().getTexture("player_jump");
    }

    @Override
    public Texture getActiveFrame() {
        Texture texture = getIdleFrame();

        if (isAttacking()) {
            texture = getAttackFrame();
        } else if (isJumping()) {
            texture = getJumpFrame();
        } else if (isFalling()) {
            texture = getFallFrame();
        } else if (isMovingHorizontally()) {
            texture = getRunFrame();
        }

        texture.setFlipped(!isFlipped());

        return texture;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void incrementCoins(int amount) {
        this.coins += amount;
    }

    @Override
    public int getHealth() {
        if (getLevel().isEditMode()) {
            return getMaxHealth();
        }

        return health;
    }

    public int getArrows() {
        int arrows = 0;

        for (InventoryItemSlot itemSlot : getBackPack().getItems()) {
            if (itemSlot.getItem() == null) {
                continue;
            }

            if (itemSlot.getItem().getItemType() == ItemType.ARROW) {
                arrows += itemSlot.getItem().getAmount();
            }
        }

        return arrows;
    }

    public Inventory getBackPack() {
        return backPack;
    }

    public InventoryItem getItemInHand() {
        return getBackPack().getItemAt(getBackPack().getSelectedSlot()).getItem();
    }
}
