package block;

import java.awt.*;

public enum BlockTypes {

    VOID("resources/images/blocks/void.png", false),
    BARRIER("resources/images/blocks/dirt.png"),
    FOREST_GROUND("resources/images/blocks/forest_ground/forest_ground", true, 16),
    LADDER("resources/images/blocks/ladder.png", false),
    ROPE("resources/images/blocks/rope.png", false),
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
    SNOWDOWN("resources/images/snowmiddle.png"),
    STONERCORNER("resources/images/blocks/STONERCORNER.png"),
    STONERMIDDLE("resources/images/blocks/STONERIGHTMIDDLE.png"),
    STONELCORNER("resources/images/blocks/STONELCORNER.png"),
    STONELMIDDLE("resources/images/blocks/STONELMIDDLE.png"),
    LOG_START("resources/images/blocks/log_left.png"),
    LOG_MID("resources/images/blocks/log_mid.png"),
    LOG_END("resources/images/blocks/log_end.png"),
    PLAYER_SPAWN("resources/images/blocks/player_spawn.png", false);




    private final String getFilePath;
    private final boolean isCollidable;
    private final int blockSetAmount;

    BlockTypes(String getFilePath) {
        this.getFilePath = getFilePath;
        this.isCollidable = true;
        this.blockSetAmount = 0;
    }

    BlockTypes(String getFilePath, boolean isCollidable) {
        this.getFilePath = getFilePath;
        this.isCollidable = isCollidable;
        this.blockSetAmount = 0;
    }

    BlockTypes(String getFilePath, boolean isCollidable, int blockSetAmount) {
        this.getFilePath = getFilePath;
        this.isCollidable = isCollidable;
        this.blockSetAmount = blockSetAmount;
    }

    public String getFilePath() {
        return getFilePath;
    }

    public boolean isCollidable() {
        return isCollidable;
    }

    public int getBlockSetAmount() {
        return blockSetAmount;
    }

    public boolean isBlockSet() {
        return blockSetAmount > 0;
    }
}
