package entity;

import entity.utils.EntityInteractable;
import level.Level;
import utils.Location;
import main.SoundType;

public class Door extends EntityInteractable {

    private boolean isOpened;

    public Door(Level level, Location loc) {
        super(EntityType.DOOR, level, loc);

        setScale(1.5);
        setCanMove(false);
        setPersistent(false);
    }

    /*
    @Override
    public void update(double dt) {
        super.update(dt);

        // Check if the player can enter the next level
        if (canEnter(getLevel().getPlayer())) {

            // Check if the specified key is pressed to switch to the next level
            if (getLevel().getManager().getEngine().keysPressed.contains(69)) {
                getLevel().getManager().getEngine().getAudioBank().playSound(SoundType.DOOR_OPEN);
                getLevel().getManager().getEngine().setActiveLevel(getLevel().getManager().getLevels().get(getLevel().getNextLevel()));
            }
        }
    }*/


    /*
    @Override
    public void processMovement(double dt) {
        return;
    }*/

    /*public boolean canEnter(Player p) {
        return p.hasKey() && getCollisionBox().collidesWith(p.getCollisionBox());
    }*/

    @Override
    public void onInteract() {
        if (isOpened) {
            return;
        }

        System.out.println("test");

        if (getLevel().getManager().getEngine().keysPressed.contains(69)) {
            getLevel().getManager().getEngine().getAudioBank().playSound(SoundType.DOOR_OPEN);
            getLevel().getManager().getEngine().setActiveLevel(getLevel().getManager().getLevels().get(getLevel().getNextLevel()));
            getLevel().getPlayer().setLocation(getLevel().getSpawnPoint().clone());
            isOpened = true;
        }
    }
}
