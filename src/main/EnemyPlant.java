package main;

import level.Level;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class EnemyPlant extends Enemy {
    private GameEngine.AudioClip audioClip;
    private boolean isAttacking;
    private boolean isCooldown;
    private Timer healthTimer;

    public EnemyPlant(Level level, Location loc) {
        super(level, EntityType.PLANT_MONSTER, loc, 58, 79);

        setDamage(5);
        setMaxHealth(100);
        setScale(1);
        this.healthTimer = new Timer(500, (ActionListener)null);
        this.healthTimer.setRepeats(false);
        this.healthTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getTarget().setHealth(getTarget().getHealth()- getDamage());
                isCooldown = true;
                healthTimer.stop();
                (new Timer(500, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        isCooldown = false;
                        if (getCollisionBox().collidesWith(getTarget().getCollisionBox())) {
                            healthTimer.start();
                        }

                    }
                })).start();

            }
        });



    }

    @Override
    public void update(double dt) {
        super.update(dt);
        if(!isActive()){
            return;
        }
        setTarget(getLevel().getPlayer());
        attack();
    }

    @Override
    public void render(Camera cam) {
        double offsetX = getLocation().getX() + cam.centerOffsetX;
        double offsetY = getLocation().getY() + cam.centerOffsetY;

        if (isAttacking) {
            offsetX = offsetX - 70;
            offsetY = offsetY - 17;
        }

        getLevel().getManager().getEngine().drawImage(getActiveFrame(), offsetX, offsetY, getWidth(), getHeight());

        if (cam.showHitboxes) {
            double hitBoxOffsetX = getCollisionBox().getLocation().getX() + cam.centerOffsetX;
            double hitBoxOffsetY = getCollisionBox().getLocation().getY() + cam.centerOffsetY;

            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(hitBoxOffsetX, hitBoxOffsetY, getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }
    }


    @Override
    public void attack() {

        if (getTarget() != null) {
            if (!this.healthTimer.isRunning() && !this.isCooldown) {
                if (canAttack()) {
                    this.healthTimer.start();

                    getTarget().setHealth(getTarget().getHealth() - getDamage());
                    System.out.println(getTarget().getHealth());
                    isAttacking = true;
                    audioClip = getLevel().getManager().getEngine().loadAudio("resources/sounds/hitSound.wav");
                    getLevel().getManager().getEngine().playAudio(audioClip);


                }else{
                    isAttacking = false;
                }
            }
        }}
    @Override
    public Image getActiveFrame() {
        if (isAttacking){
            return getLevel().getManager().getEngine().getTexture("plant_monsterAttack");

        }


        return getLevel().getManager().getEngine().getTexture(getType().toString().toLowerCase());
    }

    @Override
    public double getWidth() {
        if (isAttacking) {
            return 128 * getScale();
        }

        return ((BufferedImage) getIdleFrame()).getWidth() * getScale();
    }

    @Override
    public double getHeight() {
        if (isAttacking) {
            return 96 * getScale();
        }

        return ((BufferedImage) getIdleFrame()).getHeight() * getScale();
    }


    @Override
    public boolean hasAttackAnimation() {
        return true;
    }


    @Override
    public void destroy(){
        super.destroy();
        healthTimer.stop();

    }
}