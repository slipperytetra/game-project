package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProceduralGeneration {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50; // Adjusted height for platformer game
    private static final char WALL = '.';
    private static final char GROUND = 'G';
    private static final char LADDER = 'L';
    private static final char PLATFORM = 'P';

    private char[][] map;
    private Random random;
    private List<Polygon> polygons = new ArrayList<>();

    public ProceduralGeneration() {
        map = new char[WIDTH][HEIGHT];
        random = new Random();
        initializeMap();
        generatePolygons();
        convertPolygonsToMap();
        createLadders();
        createHitboxes();
    }

    private void initializeMap() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                map[x][y] = WALL;
            }
        }
    }

    private void generatePolygons() {
        int polygonCount = 20; // Total number of polygons
        int minVertices = 3; // Minimum vertices for a polygon (triangle)
        int maxVertices = 5; // Maximum vertices for a polygon (pentagon)
        int maxPolygonSize = 8; // Maximum size of the polygon (in terms of width or height)

        for (int i = 0; i < polygonCount; i++) {
            int verticesCount = random.nextInt(maxVertices - minVertices + 1) + minVertices;
            List<Vertex> vertices = new ArrayList<>();

            // Generate vertices within the map boundaries
            for (int j = 0; j < verticesCount; j++) {
                int x = random.nextInt(WIDTH);
                int y = random.nextInt(HEIGHT);
                vertices.add(new Vertex(x, y));
            }

            // Adjust polygon size based on vertex positions
            int minX = vertices.stream().mapToInt(Vertex::getX).min().orElse(0);
            int maxX = vertices.stream().mapToInt(Vertex::getX).max().orElse(0);
            int minY = vertices.stream().mapToInt(Vertex::getY).min().orElse(0);
            int maxY = vertices.stream().mapToInt(Vertex::getY).max().orElse(0);

            int polygonWidth = maxX - minX;
            int polygonHeight = maxY - minY;

            // Scale down large polygons
            if (polygonWidth > maxPolygonSize || polygonHeight > maxPolygonSize) {
                double scaleRatio = 1.0;
                if (polygonWidth > maxPolygonSize) {
                    scaleRatio = (double) maxPolygonSize / polygonWidth;
                }
                if (polygonHeight > maxPolygonSize) {
                    scaleRatio = Math.min(scaleRatio, (double) maxPolygonSize / polygonHeight);
                }

                // Apply scaling to vertices
                for (Vertex vertex : vertices) {
                    int scaledX = (int) (vertex.getX() * scaleRatio);
                    int scaledY = (int) (vertex.getY() * scaleRatio);
                    vertex.setX(scaledX);
                    vertex.setY(scaledY);
                }
            }

            polygons.add(new Polygon(vertices));
        }
    }

    private void convertPolygonsToMap() {
        for (Polygon polygon : polygons) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    if (polygon.contains(new Vertex(x, y))) {
                        map[x][y] = PLATFORM;
                    }
                }
            }
        }
    }

    private void createLadders() {
        // Create ladders between platforms if needed
        // Implement logic as per your game's requirements
    }

    private void createHitboxes() {
        // Create hitboxes around polygons for collision detection or other purposes
        // Implement logic as per your game's requirements
    }

    public void saveMapToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Static lines
            writer.write("name: LEVEL1\n");
            writer.write("background: resources/images/backgrounds/level1_background.png\n");
            writer.write("midground: resources/images/backgrounds/level1_midground.png\n");
            writer.write("foreground: resources/images/backgrounds/level1_foreground.png\n");
            writer.write("background_music: resources/sounds/level1_music.wav\n");
            writer.write("overlay: resources/images/overlay.png\n");
            writer.write("next_level: level2\n");
            writer.write("level_data:\n");

            // Map data (bottom to top)
            for (int y = HEIGHT - 1; y >= 0; y--) {
                for (int x = 0; x < WIDTH; x++) {
                    writer.write(map[x][y]);
                }
                writer.newLine();
            }

            // Keymap
            writer.write("keymap:\n");
            writer.write("G: PLATFORM\n");
            writer.write("L: LADDER\n");
            writer.write(".: WALL\n");
            writer.write("P: PLATFORM\n");

            System.out.println("Map saved to " + filename); // Print success message

        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for any IOException
        }
    }

    public static void main(String[] args) {
        ProceduralGeneration pg = new ProceduralGeneration();
        pg.saveMapToFile("saves/levels/level1.txt");
    }

    static class Polygon {
        private List<Vertex> vertices;

        public Polygon(List<Vertex> vertices) {
            this.vertices = vertices;
        }

        public boolean contains(Vertex point) {
            // Implement polygon containment logic (e.g., ray-casting algorithm)
            // Return true if the point is inside the polygon
            int i, j;
            boolean c = false;
            for (i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
                if (((vertices.get(i).getY() > point.getY()) != (vertices.get(j).getY() > point.getY())) &&
                        (point.getX() < (vertices.get(j).getX() - vertices.get(i).getX()) * (point.getY() - vertices.get(i).getY()) / (vertices.get(j).getY() - vertices.get(i).getY()) + vertices.get(i).getX())) {
                    c = !c;
                }
            }
            return c;
        }
    }

    static class Vertex {
        private int x;
        private int y;

        public Vertex(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
