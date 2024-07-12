package entity;

import level.Level;
import main.Camera;
import utils.Location;
import main.SoundType;
import utils.Texture;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class EnemyPlant extends Enemy {

    public EnemyPlant(Level level, Location loc) {
        super(EntityType.PLANT_MONSTER, level, loc);

        setDamage(5);
        setMaxHealth(20);
        setHealth(20);
        setScale(1);
        setAttackCooldown(2.0);

        setHitSound(SoundType.GENERIC_HIT);
        setAttackSound(null);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
    }

    @Override
    public void render(Camera cam) {
        double offsetX = cam.toScreenX(getLocation().getX());
        double offsetY = cam.toScreenY(getLocation().getY());

        if (deathTimer.isRunning()) {
            Graphics2D g2d = cam.game.mGraphics;
            AffineTransform oldTrans = g2d.getTransform();
            g2d.translate(offsetX, offsetY);
            g2d.rotate(getRotation(), getWidth()/2, getHeight()/2);
            g2d.scale(getScale(), getScale());
            g2d.drawImage(getIdleFrame().getImage(), 0, 0, null);
            g2d.setTransform(oldTrans);
            return;
        }

        if (isAttacking()) {
            int diffX = -70;
            int diffY = -17;

            if (isFlipped()) {
                diffX = 8;
            }
            offsetX = offsetX + diffX;
            offsetY = offsetY + diffY;
        }

        getLevel().getManager().getEngine().drawImage(getActiveFrame().getImage(), offsetX , offsetY , getWidth() , getHeight() );

        if (cam.debugMode) {
            getLevel().getManager().getEngine().changeColor(getHitboxColor());
            getLevel().getManager().getEngine().drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()), cam.toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth() , getCollisionBox().getHeight() );
        }
    }

    @Override
    public Texture getActiveFrame() {
        Texture texture = getIdleFrame();

        if (isAttacking()) {
            texture = getAttackFrame();
        }

        texture.setFlipped(isFlipped());

        if (getHurtTicks() > 0) {
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

        return texture;
    }
}