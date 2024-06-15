package main;

public enum SoundType {

    BLOCK_BREAK("resources/sounds/block_break.wav", -15.0f),
    BLOCK_PLACE("resources/sounds/block_place.wav"),
    STINGER_SHOOT("resources/sounds/stinger_shoot.wav"),
    COLLECT_COIN("resources/sounds/coin_pickup.wav"),
    COLLECT_HEALTH("resources/sounds/health.wav"),
    COLLECT_KEY("resources/sounds/keyObtained.wav"),
    DOOR_OPOEN("resources/sounds/nextLevel.wav"),
    GENERIC_HIT("resources/sounds/attackHit.wav"),
    PLAYER_ATTACK("resources/sounds/attackSound.wav"),
    PLAYER_HIT("resources/sounds/hitSound.wav"),
    PLAYER_RUN("resources/sounds/runningSound.wav"),
    PLANT_HIT("resources/sounds/plantHit.wav"),
    PLANT_ATTACK("resources/sounds/plantAttack.wav"),
    LEVEL_MUSIC_TITLE_SCREEN("resources/sounds/menuMusic.wav"),
    LEVEL_MUSIC_JUNGLE("resources/sounds/jungle_synthetic.wav");

    private String filePath;
    private float volume;

    SoundType(String filePath) {
        this.filePath = filePath;
        this.volume = 1.0f;
    }
    SoundType(String filePath, float volume) {
        this.filePath = filePath;

        this.volume = volume;
    }

    public String getFilePath() {
        return filePath;
    }

    public float getVolume() {
        return volume;
    }
}
