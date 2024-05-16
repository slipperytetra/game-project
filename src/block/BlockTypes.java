package block;

import java.awt.*;

public enum BlockTypes {

    VOID(""),
    DIRT("resources/images/blocks/ground.png"),
    GRASS("resources/images/blocks/ground.png"),
    LADDER("resources/images/blocks/ladder.png"),
    BARRIER("");


    private final String getFilePath;
    BlockTypes(String getFilePath) {
        this.getFilePath = getFilePath;
    }

    public String getFilePath() {
        return getFilePath;
    }
}
