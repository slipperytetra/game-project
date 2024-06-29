package level.item;

public enum ItemType {

    ARROW("resources/images/objects/arrow.png", 0),
    EMPTY("resources/images/objects/arrow.png", 0),
    SWORD("resources/images/items/sword.png", 5),
    BOW("resources/images/items/bow.png", 3);

    private final String filePath;
    private final int damage;

    ItemType(String filePath, int damage) {
        this.filePath = filePath;
        this.damage = damage;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getDamage() {
        return damage;
    }
}
