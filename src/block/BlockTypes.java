package block;

import java.awt.*;

public enum BlockTypes {

    VOID(""),
    DIRT("resources/images/ground.png"),
    GRASS("resources/images/ground.png"),
    DOOR("resources/images/ground.png");

    private final String getFilePath;
    BlockTypes(String getFilePath) {
        this.getFilePath = getFilePath;
    }

    public String getFilePath() {
        return getFilePath;
    }
}
