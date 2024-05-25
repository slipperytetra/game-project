package entity;

public enum EntityType {

    PLAYER("resources/images/characters/idle.png"),
    DOOR("resources/images/blocks/door.png"),
    SDOOR("resources/images/blocks/SDoor.png"),
    KEY("resources/images/blocks/key.png"),
    PLANT_MONSTER("resources/images/characters/plant_monster.png"),
    STONE_DOOR("resources/images/blocks/stoneDoor.png"),
    SKULL_HEAD("resources/images/characters/skull_head"),
    GOLD_COIN("resources/images/goldcoin/gold_coin"),
    HEART("resources/images/heart.gif"),
    BEE("resources/images/characters/bee/bee_idle"),
    BEE_STINGER("resources/images/characters/bee/bee_stinger.png");


    private final String filePath;

    EntityType(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
