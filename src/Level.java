//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public class Level {
    private final double GRAVITY = 0.98;
    int id;
    Location spawnPoint;
    Location keyLoc;

    public Level(int id, Location spawnPoint, Location keyLoc) {
        this.id = id;
        this.spawnPoint = spawnPoint;
        this.keyLoc = keyLoc;
    }

    public Location getSpawnPoint() {
        return this.spawnPoint;
    }

    public Location getKeyLocation() {
        return this.keyLoc;
    }

    public double getGravity() {
        return 0.98;
    }
}
