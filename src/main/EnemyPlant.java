package main;

import level.Level;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class EnemyPlant extends Enemy {
    private boolean isAttacking;
    private boolean isCooldown;
    private Timer healthTimer;

    public EnemyPlant(Level level, Location loc) {
        super(level, EntityType.PLANT_MONSTER, loc);

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
        setTarget(getLevel().getPlayer());
        attack();
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
               // getLevel().getManager().getEngine().loadAudio();
                //getLevel().getManager().getEngine().playAudio();


            }else{
                isAttacking = false;
            }
        }
    }}
    @Override
    public Image getIdleFrame() {
        if (isAttacking){
            return getLevel().getManager().getEngine().getTexture("plant_monsterAttack");

        }


        return getLevel().getManager().getEngine().getTexture(getType().toString().toLowerCase());
    }

    public double getWidth() {
        return 50;
    }

    public double getHeight() {
        return 50;    }


    @Override
    public boolean hasAttackAnimation() {
        return true;
    }
}
