package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class ProceduralGeneration {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final char WALL = '.';
    private static final char GROUND = 'G';
    private static final char PLAYER = 'P';
    private char[][] map;
    private Random random;

    public ProceduralGeneration() {
        map = new char[WIDTH][HEIGHT];
        random = new Random();
        initializeMap();
        generatePlatforms();
        placePlayerSpawn();
    }

    private void initializeMap() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                map[x][y] = WALL;
            }
        }
    }

    private void generatePlatforms() {
        int platformCount = 10;
        int platformMinLength = 5;
        int platformMaxLength = 15;

        for (int i = 0; i < platformCount; i++) {
            int platformLength = random.nextInt(platformMaxLength - platformMinLength) + platformMinLength;
            int platformX = random.nextInt(WIDTH - platformLength);
            int platformY = random.nextInt(HEIGHT - 10) + 5; // Avoid the very top and bottom rows

            for (int x = platformX; x < platformX + platformLength; x++) {
                map[x][platformY] = GROUND;
            }
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
