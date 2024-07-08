package level.item;

public enum ItemType {

    EMPTY("resources/images/objects/arrow.png", ItemTypeCategory.RESOURCE),
    ARROW("resources/images/objects/arrow.png", ItemTypeCategory.AMMO),
    SWORD("resources/images/items/sword.png", ItemTypeCategory.SWORD, 5),
    TEST_SWORD("resources/images/items/test_sword.png", ItemTypeCategory.SWORD, 20),
    TEST_BOW("resources/images/items/test_bow.png", ItemTypeCategory.BOW, 10),
    BOW("resources/images/items/bow.png", ItemTypeCategory.BOW, 5);

    private final String filePath;
    private final ItemTypeCategory typeCategoery;
    private final int damage;


    ItemType(String filePath, ItemTypeCategory typeCategoery) {
        this.filePath = filePath;
        this.typeCategoery = typeCategoery;
        this.damage = 0;
    }
    ItemType(String filePath, ItemTypeCategory typeCategoery, int damage) {
        this.filePath = filePath;
        this.typeCategoery = typeCategoery;
        this.damage = damage;
    }

    public String getFilePath() {
        return filePath;
    }

    public ItemTypeCategory getCategory() {
        return typeCategoery;
    }

    public int getDamage() {
        return damage;
    }
}
