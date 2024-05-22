package block;

import java.awt.*;

public enum BlockTypes {

    VOID(""),
    DIRT("resources/images/blocks/dirt.png"),
    GRASS("resources/images/blocks/ground.png"),
    FOREST_GRASS("resources/images/blocks/forest_grass.png"),
    FOREST_GROUND("resources/images/blocks/forest_ground.png"),
    LADDER("resources/images/blocks/ladder.png"),
    BARRIER(""),
    WATER_TOP("resources/images/blocks/waterTop.png"),
    WATER_BOTTOM("resources/images/blocks/waterBottom.png"),
    STONE_FLOOR("resources/images/blocks/STONE.png"),
    STONE_FILLER("resources/images/blocks/stone_filler.png"),
    LAVA("resources/images/lava.png"),
    BRIDGE("resources/images/bridgeMid.png"),
    BL("resources/images/bridgeL.png"),
    BR("resources/images/bridgeR.png");



    private final String getFilePath;

    BlockTypes(String getFilePath) {
        this.getFilePath = getFilePath;
    }

    public String getFilePath() {
        return getFilePath;
    }
}
