package main;

public enum EnemyType {

    PLANT_MONSTER("resources/images/characters/plant_monster.png");

    private final String filePath;

    EnemyType(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
