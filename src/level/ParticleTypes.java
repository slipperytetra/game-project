package level;

public enum ParticleTypes {

    ARROW_TRAIL("resources/images/particles/arrow_trail.png", 0.75, 0.25, false),
    CLOUD("resources/images/particles/cloud.png", 1,0.5, 0.75, 4, true),
    LEAF("resources/images/particles/leaf.png", 3,0.25, 0.5, 32, true, 0, 1);

    private final String filePath;
    private final double timeAlive;
    private final boolean fadeOut;
    private final double minSize, maxSize;
    private double offset;
    private double velX, velY;

    ParticleTypes(String filePath, double timeAlive, boolean fadeOut) {
        this.filePath = filePath;
        this.timeAlive = timeAlive;
        this.fadeOut = fadeOut;
        this.minSize = -1;
        this.maxSize = -1;
    }

    ParticleTypes(String filePath, double timeAlive, double size, boolean fadeOut) {
        this.filePath = filePath;
        this.timeAlive = timeAlive;
        this.fadeOut = fadeOut;
        this.minSize = size;
        this.maxSize = size;
    }

    ParticleTypes(String filePath, double timeAlive, double minSize, double maxSize, boolean fadeOut) {
        this.filePath = filePath;
        this.timeAlive = timeAlive;
        this.fadeOut = fadeOut;
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    ParticleTypes(String filePath, double timeAlive, double minSize, double maxSize, double offset, boolean fadeOut) {
        this.filePath = filePath;
        this.timeAlive = timeAlive;
        this.fadeOut = fadeOut;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.offset = offset;
    }

    ParticleTypes(String filePath, double timeAlive, double minSize, double maxSize, double offset, boolean fadeOut, double velX, double velY) {
        this.filePath = filePath;
        this.timeAlive = timeAlive;
        this.fadeOut = fadeOut;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.offset = offset;
        this.velX = velX;
        this.velY = velY;
    }

    public String getFilePath() {
        return filePath;
    }

    public double getTimeAlive() {
        return timeAlive;
    }

    public boolean isFadeOut() {
        return fadeOut;
    }

    public double getMinSize() {
        return minSize;
    }

    public double getMaxSize() {
        return maxSize;
    }

    public double getOffset() {
        return offset;
    }

    public double getVelX() {
        return velX;
    }

    public double getVelY() {
        return velY;
    }

    public boolean isShrink() {
        return this == ParticleTypes.ARROW_TRAIL;
    }
}
