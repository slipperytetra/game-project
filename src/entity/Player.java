package entity;

import block.BlockClimbable;
import block.BlockTypes;
import level.Level;
import level.ParticleTypes;
import main.Camera;
import main.Game;
import main.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Set;

public class Player extends EntityLiving {
    private JProgressBar healthBar;
    private boolean isJumping;
    private boolean hasKey;

    private Timer runAnimationTimer;
    private int runFrameIndex;

    public Timer jumpAnimationTimer;
    private int jumpFrameIndex;
    private double timeJumping;
    private double maxJumpTime = 0.20; //seconds

    private double runParticleTimer;
    private double RUN_PARTICLE_FREQUENCY = 0.075;

    Image gifImage;
    Image plantAttack;
    Image gifImage2;
    Image level1;

    public Player(Level level, Location loc) {
        super(EntityType.PLAYER, level, loc, 19, 29);

        setHitboxColor(Color.cyan);
        setMaxHealth(100);
        setDamage(5);
        setHealth(getMaxHealth());
        setDirectionY(1);

        init();
    }

    public void init() {
        gifImage = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
        plantAttack = Toolkit.getDefaultToolkit().createImage("resources/images/plantAttack.gif");

        gifImage2 = Toolkit.getDefaultToolkit().createImage("resources/images/keyy.gif");
        level1 = Toolkit.getDefaultToolkit().createImage("resources/images/level1.gif");

        setHitSound(getLevel().getManager().getEngine().loadAudio("resources/sounds/hitSound.wav"));
        setAttackSound(getLevel().getManager().getEngine().loadAudio("resources/sounds/attackSound.wav"));

        this.healthBar = new JProgressBar(0, getMaxHealth());
        this.healthBar.setBounds(100, 25, 100, 10); // Adjust position and size as needed
        this.healthBar.setForeground(Color.RED); // Set the color
        this.healthBar.setValue(getMaxHealth()); // Set initial health
        this.healthBar.setStringPainted(true); // Show health value

        this.runAnimationTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runFrameIndex = (runFrameIndex + 1) % 4;
                //System.out.println("Run " + currentFrameIndex);
            }
        });

        this.jumpAnimationTimer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Jump: " + jumpFrameIndex);
                jumpFrameIndex = (jumpFrameIndex + 1) % 4;
            }
        });
    }

    public void update(double dt) {
        super.update(dt);
        animateCharacter();
        //System.out.println("run: " + runParticleTimer);
        if (runParticleTimer < RUN_PARTICLE_FREQUENCY) {
            runParticleTimer += 1 * dt;
        }

        if  (runParticleTimer >= RUN_PARTICLE_FREQUENCY) {
            if(isMovingHorizontally() && isOnGround()){
                getLevel().spawnParticle(ParticleTypes.CLOUD, getLocation().getX(), getLocation().getY() + 40);
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
        moveX = getDirectionX() * (speed * dt);
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

        if (isFalling() && !canClimb()) {
            if (fallAccel > 0) {
                fallAccel *= fallSpeedMultiplier;
                setDirectionY(1 * fallAccel);
            }
        } else {
            fallAccel = 1;
            setDirectionY(0);
        }
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    @Override
    public void render(Camera cam) {
        double playerOffsetX = getLocation().getX() + cam.centerOffsetX;
        double playerOffsetY = getLocation().getY() + cam.centerOffsetY;
        Game game = getLevel().getManager().getEngine();

        if (isAttacking()) {
            playerOffsetX = playerOffsetX - 31;
            playerOffsetY = playerOffsetY - 8;
        }

        game.drawImage(getActiveFrame(), playerOffsetX, playerOffsetY, getWidth(), getHeight());

        if (cam.debugMode) {
            game.changeColor(Color.magenta);

            if (getBlockBelowEntityLeft() != null) {
                game.drawRectangle(getBlockBelowEntityLeft().getLocation().getX() + cam.centerOffsetX, getBlockBelowEntityLeft().getLocation().getY() + cam.centerOffsetY, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
            }

            if (getBlockBelowEntityRight() != null) {
                game.drawRectangle(getBlockBelowEntityRight().getLocation().getX() + cam.centerOffsetX, getBlockBelowEntityRight().getLocation().getY() + cam.centerOffsetY, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
            }

            double hitBoxOffsetX = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
            double hitBoxOffsetY = getCollisionBox().getLocation().getY() + cam.centerOffsetY;
            game.changeColor(getHitboxColor());
            game.drawRectangle(hitBoxOffsetX, hitBoxOffsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }
    }

    public void jump() {
        this.isJumping = true;
        this.jumpFrameIndex = 0;
        this.jumpAnimationTimer.start();
        this.timeJumping = 0;
    }

    public void playerMovement(Set<Integer> keysPressed) {
        if (keysPressed.contains(32)) {//SPACE
            if (!isJumping() && !isAttacking() && (isOnGround() || canClimb())) {
                jump();
            }
        }
        if (keysPressed.contains(87)) {//W
            if (canClimb() && getBlockAtLocation(0, -1).getType() != BlockTypes.VOID) {
                setDirectionY(-1);
            }
        }
        if (keysPressed.contains(65)) {//A
            setDirectionX(-calculateHorizontalMovement());
        }
        if (keysPressed.contains(83)) {//S
            if (canClimb()) {
                setDirectionY(1);
            }
        }
        if (keysPressed.contains(68)) {//D
            setDirectionX(calculateHorizontalMovement());
        }
        if (keysPressed.contains(81)){
            attack();
        }
    }

    public JProgressBar getHealthBar() {
        return healthBar;
    }

    @Override
    public double getWidth() {
        if (isAttacking()) {
            return 50 * getScale();
        }

        return ((BufferedImage) getIdleFrame()).getWidth() * getScale();
    }

    @Override
    public double getHeight() {
        if (isAttacking()) {
            return 37 * getScale();
        }

        return ((BufferedImage) getIdleFrame()).getHeight() * getScale();
    }

    public double calculateHorizontalMovement() {
        if (isAttacking()) {
            return 0;
        }

        if (isMovingVertically()) {
            return 0.75;
        }

        return 1;
    }

    private void animateCharacter() {
        if (isMovingHorizontally() && !isMovingVertically()) {
            if (!this.runAnimationTimer.isRunning()) {
                this.runAnimationTimer.start();
            }
        } else {
            this.runAnimationTimer.stop();
        }
    }

    public Image getRunFrame() {
        if (!isFlipped()) {
            return getLevel().getManager().getEngine().flipImageHorizontal(getLevel().getManager().getEngine().getTexture("player_run_" + runFrameIndex));
        }

        return getLevel().getManager().getEngine().getTexture("player_run_" + runFrameIndex);
    }

    public Image getFallFrame() {
        if (!isFlipped()) {
            return getLevel().getManager().getEngine().flipImageHorizontal(getLevel().getManager().getEngine().getTexture("player_jump_" + runFrameIndex));
        }

        return getLevel().getManager().getEngine().getTexture("player_jump_" + runFrameIndex);
    }

    @Override
    public Image getActiveFrame() {
        if (isAttacking()) {
            return getAttackFrame();
        } else if (isMovingVertically()) {
            return getFallFrame();
        } else if (isMovingHorizontally()) {
            return getRunFrame();
        }

        return getIdleFrame();
    }

      public Image getAttackFrame(){
        return getLevel().getManager().getEngine().getTexture("player_attack");
     }


    public Enemy getTarget() {
        for (Entity enemy : getLevel().getEntities()){
            if (!enemy.isActive()) {
                continue;
            }

            if (enemy instanceof Enemy) {
                if (Location.calculateDistance(getLocation().getX(), getLocation().getY(), enemy.getLocation().getX(), enemy.getLocation().getY()) < 64) {
                    //System.out.println("Close");
                    return (Enemy) enemy;
                }
            }
        }

        return null;
    }

    public boolean canClimb() {
        return getBlockAtLocation() instanceof BlockClimbable;
    }
}
