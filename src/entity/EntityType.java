package entity;

public enum EntityType {

    ARROW("resources/images/objects/arrow.png"),
    BEE("resources/images/characters/bee/bee_idle_frame_", 4, 8),
    STINGER("resources/images/objects/stinger.png"),
    PLAYER("resources/images/characters/idle.png"),
    DOOR("resources/images/blocks/door.png"),
    SDOOR("resources/images/blocks/sDoor.png"),
    KEY("resources/images/characters/key/key_", 12, 7),
    GOLD_COIN("resources/images/objects/coins/gold_coin_", 6, 8),
    PLANT_MONSTER("resources/images/characters/plant/plant_monster.png"),
    STONE_DOOR("resources/images/blocks/stoneDoor.png"),
    HEART("resources/images/characters/heart_", 6, 6);


    private final String filePath;
    private int frames, frameRate;

    EntityType(String filePath) {
        this.filePath = filePath;
    }

    EntityType(String filePath, int frames, int frameRate) {
        this.filePath = filePath;
        this.frames = frames;
        this.frameRate = frameRate;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getFrames() {
        return frames;
    }

    public int getFrameRate() {
        return frameRate;
    }
}
