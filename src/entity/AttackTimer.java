package entity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AttackTimer extends Timer {

    private EntityLiving entity;

    public AttackTimer(EntityLiving entity) {
        super((int) (entity.getAttackCooldown() * 1000), null);
        this.entity = entity;
        this.setInitialDelay(0);

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (entity.getLevel().isEditMode() || entity.getLevel().getManager().getEngine().isPaused) {
                    return;
                }
                //System.out.println(entity.getType().toString() + " running attackTimer");
                if (entity.getType() != EntityType.PLAYER) {
                    if (entity.getAttackSound() != null) {
                        entity.getLevel().getManager().getEngine().getAudioBank().playSound(entity.getAttackSound());
                    }
                    entity.getAttackFrame().setFrameIndex(0);
                    entity.setAttackTicks(0);
                }

                entity.findTarget();

                if (entity.getTarget() == null) {
                    entity.setAttackTicks(entity.getAttackCooldown());
                    stop();
                    return;
                }

                entity.attack();
            }
        });
    }

    @Override
    public void stop() {
        super.stop();
        System.out.println("Stopping for " + entity.getType());
    }
}
