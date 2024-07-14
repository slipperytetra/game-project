package utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GameUtils {

    public static String capatilize(String str) {
        str = str.toLowerCase();
        String first = str.substring(0, 1).toUpperCase();
        String remaining = str.substring(1);

        return first + remaining;
    }

    public static Texture applyTint(Texture texture, Color tint) {
        int width = texture.getWidth();
        int height = texture.getHeight();

        // Create a new BufferedImage with the same dimensions and type as the original
        BufferedImage tintedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tintedImage.createGraphics();

        // Draw the original image onto the tintedImage
        g.drawImage(texture.getImage(), 0, 0, null);
        g.dispose();

        // Manipulate pixels to apply tint
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get RGB color of the pixel
                int rgb = tintedImage.getRGB(x, y);

                // Apply tinting by combining the pixel's color with the tint color
                int red = (rgb >> 16) & 0xFF; // Red component
                int green = (rgb >> 8) & 0xFF; // Green component
                int blue = rgb & 0xFF; // Blue component

                // Tint calculation
                red = (int) (red * tint.getRed() / 255.0);
                green = (int) (green * tint.getGreen() / 255.0);
                blue = (int) (blue * tint.getBlue() / 255.0);

                // Update RGB value with tint
                rgb = (rgb & 0xFF000000) | (red << 16) | (green << 8) | blue;

                // Set the updated pixel color in tintedImage
                tintedImage.setRGB(x, y, rgb);
            }
        }

        //System.out.println("returning tinted img");
        return new Texture(tintedImage);
    }
}
