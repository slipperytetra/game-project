package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProceduralGeneration {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final char WALL = '.';
    private static final char GROUND = 'G';
    private static final char PLAYER = 'P';
    private static final char DECO = 'D';
    private static final char ROPE = 'R';

    private char[][] map;
    private Random random;
    private List<Platform> platforms = new ArrayList<>();

    public ProceduralGeneration() {
        map = new char[WIDTH][HEIGHT];
        random = new Random();
        initializeMap();
        generatePlatforms();
        addDecos();
        placePlayerSpawn();
        placeRopes();
    }

    private void initializeMap() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                map[x][y] = WALL;
            }
        }
    }

    private void addDecos() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int chance = random.nextInt(100);
                if (map[x][y] == GROUND && chance < 15) {
                    map[x][y - 1] = DECO;
                }
            }
        }
    }

    public double distance(double x1, double y1, double x2, double y2) {
        // Calculate and return the distance
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private void generatePlatforms() {
        int platformCount = 10;
        int platformMinLength = 5;
        int platformMaxLength = 15;

        for (int i = 0; i < platformCount; i++) {
            int platformLength = random.nextInt(platformMaxLength - platformMinLength) + platformMinLength;
            int platformX = random.nextInt(WIDTH - platformLength);
            int platformY = random.nextInt(HEIGHT - 10) + 5;

            for (int x = platformX; x < platformX + platformLength; x++) {
                map[x][platformY] = GROUND;
            }

            platforms.add(new Platform(platformX, platformY, platformX + platformLength, platformY + 1));
        }
    }

    private void placePlayerSpawn() {
        for (int y = HEIGHT - 1; y > 0; y--) {
            for (int x = 0; x < WIDTH; x++) {
                if (map[x][y] == GROUND && map[x][y + 1] == WALL) {
                    map[x][y - 1] = PLAYER;
                    return;
                }
            }
        }
        System.out.println("Player spawn point could not be placed.");
    }

    private void placeRopes() {
        for (Platform p : platforms) {
            Platform nearest = nearestPlatform(p);
            double dist = distance(nearest.minX, nearest.minY, p.minX, p.minY);
            System.out.println("Distance to nearest platform: " + dist);

            if (dist > 4) {
                map[nearest.minX][nearest.minY + 1] = ROPE;
            }
        }
    }

    private Platform nearestPlatform(Platform platform) {
        Platform temp = null;

        for (Platform p : platforms) {
            if (p == platform) {
                continue;
            }

            if (temp == null) {
                temp = p;
                continue;
            }

            double dist = distance(p.minX, p.minY, platform.minX, platform.minY);
            double currentDist = distance(temp.minX, temp.minY, platform.minX, platform.minY);

            if (dist < currentDist) {
                temp = p;
            }
        }
        return temp;
    }

    public void saveMapToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Static lines
            writer.write("name: FOREST1\n");
            writer.write("background: resources/images/backgrounds/forest_background.png\n");
            writer.write("midground: resources/images/backgrounds/forest_midground.png\n");
            writer.write("foreground: resources/images/backgrounds/forest_foreground.png\n");
            writer.write("background_music: resources/sounds/jungle_synthetic.wav\n");
            writer.write("overlay: resources/images/night_filter.png\n");
            writer.write("next_level: forest_2\n");
            writer.write("level_data:\n");

            // Map data
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    writer.write(map[x][y]);
                }
                writer.newLine();
            }

            // Keymap
            writer.write("keymap:\n");
            writer.write("G: FOREST_GROUND\n");
            writer.write("P: PLAYER_SPAWN\n");
            writer.write(".: WALL\n");
            writer.write("D: TALL_GRASS\n");
            writer.write("R: ROPE");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ProceduralGeneration pg = new ProceduralGeneration();
        pg.saveMapToFile("saves/levels/level_forest_0.txt");
        System.out.println("Map saved to generated_map.txt");
    }
}

class Platform {
    int minX;
    int minY;
    int maxX;
    int maxY;

    public Platform(int platformX, int platformY, int i, int i1) {
        this.minX = platformX;
        this.maxX = i;
        this.minY = platformY;
        this.maxY = i1;
    }
}
