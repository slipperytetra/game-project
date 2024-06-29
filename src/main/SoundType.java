package main;

public enum SoundType {

    ARROW_IMPACT("resources/sounds/arrow_impact.wav", -12.0f),
    BACKPACK_OPEN("resources/sounds/backpack_open.wav"),
    BACKPACK_CLOSE("resources/sounds/backpack_close.wav"),
    BLOCK_BREAK("resources/sounds/block_break.wav", -15.0f),
    BLOCK_PLACE("resources/sounds/block_place.wav"),
    STINGER_SHOOT("resources/sounds/stinger_shoot.wav"),
    COLLECT_COIN("resources/sounds/coin_pickup.wav"),
    COLLECT_HEALTH("resources/sounds/health.wav"),
    COLLECT_KEY("resources/sounds/keyObtained.wav"),
    DOOR_OPEN("resources/sounds/newLevel.wav"),
    GENERIC_HIT("resources/sounds/attackHit.wav"),
    MENU_NAVIGATE("resources/sounds/menu/menu_navigate.wav", -10.0f),
    MENU_SELECT("resources/sounds/menu/menu_select.wav", -10.0f),
    PLAYER_ATTACK("resources/sounds/attackSound.wav"),
    PLAYER_HIT("resources/sounds/hitSound.wav"),
    PLAYER_RUN("resources/sounds/runningSound.wav"),
    PLANT_HIT("resources/sounds/plantHit.wav"),
    PLANT_ATTACK("resources/sounds/plantAttack.wav"),
    STONE_CRACK("resources/sounds/stone_crack.wav", -5.0f),
    STONE_CRUMBLE("resources/sounds/stone_crUMBLE.wav", -5.0f),
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
