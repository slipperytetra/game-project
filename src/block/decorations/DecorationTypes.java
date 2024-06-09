package block.decorations;

import java.util.ArrayList;
import java.util.Arrays;

public enum DecorationTypes {

    //Can be any size, not limited to 32x32.
    ROCK("resources/images/blocks/decorations/rock.png"),
    ROCK_LARGE("resources/images/blocks/decorations/rock_large.png", 2),
    MUSHROOM("resources/images/blocks/decorations/mushroom.png", 1.5),
    MUSHROOM_BLUE("resources/images/blocks/decorations/mushroom_blue.png"),
    TALL_GRASS("resources/images/blocks/decorations/tall_grass.png"),
    FOG("resources/images/blocks/decorations/fog.png", 1),
    BUSH("resources/images/blocks/decorations/bush.png", 2),
    TREE("resources/images/blocks/decorations/tree.png", 2),
    FIREFLIES("resources/images/blocks/decorations/fireflies/fireflies_", 0.75, 75, 75, 2, true, 90, 12),
    OLD_BRICK_WALL("resources/images/blocks/decorations/old_brick_wall.png", 2),
    FOREST_PLANT_0("resources/images/blocks/decorations/forest_plant_0.png", 0.5),
    FOREST_PLANT_1("resources/images/blocks/decorations/forest_plant_1.png", 0.5),
    TREE_WILLOW_0("resources/images/blocks/decorations/willow_0.png", 1.5),
    TREE_WILLOW_1("resources/images/blocks/decorations/willow_1.png", 1.5),
    TREE_WILLOW_2("resources/images/blocks/decorations/willow_2.png", 1.5),
    HANGING_VINE("resources/images/blocks/decorations/hanging_vine.png", 3),
    HANGING_VINE_FLOWERS("resources/images/blocks/decorations/hanging_vine_flowers.png", 3),
    LAMP_POST("resources/images/blocks/decorations/lamp_post.png", 1, 15.0, 24.0, 1, true),
    SNOW_TREE("resources/images/blocks/decorations/snowTree.png"),
    SNOW_BUSH("resources/images/blocks/decorations/snowBush.png"),
    CAVEINSIDE("resources/images/blocks/decorations/cave.png"),
    ICEBERG("resources/images/blocks/decorations/iceberg.png"),
    ICEICLES("resources/images/blocks/decorations/ice.png");

    private final String getFilePath;
    private final double scale;

    private double spotLightOffsetX, spotLightOffsetY, spotLightIntensity;
    private boolean spotLightFlicker;
    private int frames, frameRate;

    DecorationTypes(String getFilePath) {
        this.getFilePath = getFilePath;
        this.scale = 1.0;
        this.spotLightFlicker = false;
    }

    DecorationTypes(String getFilePath, double scale) {
        this.getFilePath = getFilePath;
        this.scale = scale;
        this.spotLightFlicker = false;
    }

    DecorationTypes(String getFilePath, double scale, double spotLightOffsetX, double spotLightOffsetY, double spotLightIntensity, boolean spotLightFlicker) {
        this.getFilePath = getFilePath;
        this.scale = scale;
        this.spotLightOffsetX = spotLightOffsetX;
        this.spotLightOffsetY = spotLightOffsetY;
        this.spotLightIntensity = spotLightIntensity;
        this.spotLightFlicker = spotLightFlicker;
    }

    DecorationTypes(String getFilePath, double scale, double spotLightOffsetX, double spotLightOffsetY, double spotLightIntensity, boolean spotLightFlicker, int frames, int frameRate) {
        this.getFilePath = getFilePath;
        this.scale = scale;
        this.spotLightOffsetX = spotLightOffsetX;
        this.spotLightOffsetY = spotLightOffsetY;
        this.spotLightIntensity = spotLightIntensity;
        this.spotLightFlicker = spotLightFlicker;
        this.frames = frames;
        this.frameRate = frameRate;
    }

    public String getFilePath() {
        return getFilePath;
    }

    public double getScale() {
        return scale;
    }

    public boolean hasLightSpots() {
        return spotLightIntensity > 0;
    }

    public double getSpotLightOffsetX() {
        return spotLightOffsetX;
    }

    public double getSpotLightOffsetY() {
        return spotLightOffsetY;
    }

    public double getSpotLightIntensity() {
        return spotLightIntensity;
    }

    public boolean shouldSpotLightFlicker() {
        return spotLightFlicker;
    }

    public boolean hasFallingLeaves() {
        return this == DecorationTypes.TREE || this == DecorationTypes.TREE_WILLOW_0 || this == DecorationTypes.TREE_WILLOW_1
                || this == DecorationTypes.TREE_WILLOW_2;
    }

    public int getFrames() {
        return frames;
    }

    public int getFrameRate() {
        return frameRate;
    }
}
