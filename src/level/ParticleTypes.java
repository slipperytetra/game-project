package level;

public enum ParticleTypes {

    CLOUD("resources/images/particles/cloud.png", 1,0.5, 1.0, 8, true),
    LEAF("resources/images/particles/leaf.png", 3,0.25, 0.5, 32, true, true);

    private final String filePath;
    private final double timeAlive;
    private final boolean fadeOut;
    private final boolean hasGravity;
    private final double minSize, maxSize;
    private final double offset;

    ParticleTypes(String filePath, double timeAlive, boolean fadeOut) {
        this.filePath = filePath;
        this.timeAlive = timeAlive;
        this.fadeOut = fadeOut;
        this.hasGravity = false;
        this.minSize = -1;
        this.maxSize = -1;
        this.offset = 0;
    }

    ParticleTypes(String filePath, double timeAlive, double minSize, double maxSize, boolean fadeOut) {
        this.filePath = filePath;
        this.timeAlive = timeAlive;
        this.fadeOut = fadeOut;
        this.hasGravity = false;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.offset = 0;
    }

    ParticleTypes(String filePath, double timeAlive, double minSize, double maxSize, double offset, boolean fadeOut) {
        this.filePath = filePath;
        this.timeAlive = timeAlive;
        this.fadeOut = fadeOut;
        this.hasGravity = false;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.offset = offset;
    }

    ParticleTypes(String filePath, double timeAlive, double minSize, double maxSize, double offset, boolean fadeOut, boolean hasGravity) {
        this.filePath = filePath;
        this.timeAlive = timeAlive;
        this.fadeOut = fadeOut;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.offset = offset;
        this.hasGravity = hasGravity;
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

    public boolean hasGravity() {
        return hasGravity;
    }
}
