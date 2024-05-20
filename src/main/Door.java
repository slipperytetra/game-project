package main;

import level.Level;

public class Door extends Entity {
    private GameEngine.AudioClip newLevel;
    private boolean audioPlayed;

    public Door(Level level, Location loc) {
        super(EntityType.DOOR, level, loc, 29, 51);


        setScale(1.5);
        setCanMove(false);
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        // Check if the player can enter the next level
        if (canEnter(getLevel().getPlayer())) {
            // Load the audio if it hasn't been loaded yet
            if (newLevel == null) {
                newLevel = getLevel().getManager().getEngine().loadAudio("resources/sounds/newLevel.wav");
            }

            // Play the audio if it hasn't been played yet
            if (!audioPlayed) {
                getLevel().getManager().getEngine().playAudio(newLevel);
                audioPlayed = true; // Set the flag to true to indicate that the audio has been played
            }

            // Check if the specified key is pressed to switch to the next level
            if (getLevel().getManager().getEngine().keysPressed.contains(69)) {
                getLevel().getManager().getEngine().setActiveLevel(getLevel().getManager().getLevels().get(getLevel().getNextLevel()));
            }
        }
    }


    @Override
    public void processMovement(double dt) {
        return;
    }

    public boolean canEnter(Player p) {
        return p.hasKey() && getCollisionBox().collidesWith(p.getCollisionBox());
    }
}
