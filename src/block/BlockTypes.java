package block;

import java.awt.*;

public enum BlockTypes {

    VOID(""),
    DIRT("resources/images/blocks/dirt.png"),
    GRASS("resources/images/blocks/ground.png"),
    FOREST_GROUND_0("resources/images/blocks/forest_ground_0.png"),
    FOREST_GROUND_1("resources/images/blocks/forest_ground_1.png"),
    FOREST_GROUND_2("resources/images/blocks/forest_ground_2.png"),
    FOREST_GROUND_3("resources/images/blocks/forest_ground_3.png"),
    FOREST_GROUND_4("resources/images/blocks/forest_ground_4.png"),
    FOREST_GROUND_5("resources/images/blocks/forest_ground_5.png"),
    FOREST_GROUND_6("resources/images/blocks/forest_ground_6.png"),
    FOREST_GROUND_7("resources/images/blocks/forest_ground_7.png"),
    FOREST_GROUND_8("resources/images/blocks/forest_ground_8.png"),
    LADDER("resources/images/blocks/ladder.png"),
    ROPE("resources/images/blocks/rope.png"),
    BARRIER(""),
    WATER_TOP("resources/images/blocks/waterTop.png"),
    WATER_BOTTOM("resources/images/blocks/waterBottom.png"),
    STONE_FLOOR("resources/images/blocks/STONE.png"),
    STONE_FILLER("resources/images/blocks/stone_filler.png"),
    LAVA("resources/images/lava.png"),
    BRIDGE("resources/images/bridgeMid.png"),
    BL("resources/images/bridgeL.png"),
    BR("resources/images/bridgeR.png"),
    SNOWTOPR("resources/images/blocks/SnowTopL.png"),
    SNOWMID("resources/images/blocks/SnowMiddle.png"),
    SNOWFILL("resources/images/snowB.png"),
    SNOWDOWN("resources/images/snowmiddle.png");



    private final String getFilePath;

    BlockTypes(String getFilePath) {
        this.getFilePath = getFilePath;
    }

    public String getFilePath() {
        return getFilePath;
    }
}
