package block;

import level.Level;
import main.Camera;
import main.Game;
import utils.ConfigAttribute;
import utils.Location;
import utils.WaveEffect;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BlockLiquid extends Block {

    double time = 0;
    WaveEffect waveEffect;
    BufferedImage distortedImage;

    boolean isTop;

    public BlockLiquid(Level level, Location loc, BlockTypes type) {
        super(level, loc, type);
        isTop = type == BlockTypes.WATER_TOP;
        waveEffect = new WaveEffect(3, 20, 0, 2, 40, Math.PI / 4, 0.5, 1);
    }

    @Override
    public void render(Camera cam) {
        if ((Boolean) getLevel().getManager().getEngine().getConfig().getItem(ConfigAttribute.TITLE_SETTINGS_GRAPHICS_SHADERS) && isTop) {
            cam.game.drawImage(distortedImage, cam.toScreenX(getLocation().getX()), cam.toScreenY(getLocation().getY()));
        } else {
            cam.game.drawImage(getTexture().getImage(), cam.toScreenX(getLocation().getX()) , cam.toScreenY(getLocation().getY()) , Game.BLOCK_SIZE, Game.BLOCK_SIZE );
        }

        if (cam.debugMode) {
            cam.game.changeColor(Color.GREEN);
            cam.game.drawRectangle(cam.toScreenX(getCollisionBox().getLocation().getX()) , cam.toScreenY(getCollisionBox().getLocation().getY()) , getCollisionBox().getWidth() , getCollisionBox().getHeight() );
        }
    }

    @Override
    public void update(double dt) {
        super.update(dt);

        if ((Boolean) getLevel().getManager().getEngine().getConfig().getItem(ConfigAttribute.TITLE_SETTINGS_GRAPHICS_SHADERS) && isTop) {
            int width = getTexture().getWidth();
            int height = getTexture().getHeight();
            distortedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double offset = waveEffect.getOffset(x, (double) y / height, time);  // Pass y as a fraction
                    int newY = (int) Math.round(y + offset);
                    if (newY >= 0 && newY < height) {
                        distortedImage.setRGB(x, y, getTexture().getImage().getRGB(x, newY));
                    }
                }
            }

            time += 1 * dt;
        }
    }
}
