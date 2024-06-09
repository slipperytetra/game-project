package entity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AttackTimer extends Timer {

    public AttackTimer(EntityLiving entity) {
        super((int) (entity.getAttackCooldown() * 1000), null);

        this.setInitialDelay(0);

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println(entity.getType().toString() + " running attackTimer");
                if (entity.getType() != EntityType.PLAYER) {
                    if (entity.getAttackSound() != null) {
                        entity.getLevel().getManager().getEngine().playAudio(entity.getAttackSound());
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
}
