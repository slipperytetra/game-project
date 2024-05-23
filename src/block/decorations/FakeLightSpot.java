package block.decorations;

import main.Camera;
import main.Game;

import java.util.Random;

public class FakeLightSpot {

    private Decoration parent;
    private double offsetX, offsetY, width, height;
    private double intensity;
    private Random random;

    private double flicker;
    private boolean shouldFlicker;
    private double flickerCooldown;

    private final double SPOT_LIGHT_SIZE = 75;
    private final double FLICKER_FREQUENCY = 0.1; //seconds

    public FakeLightSpot(Decoration parent) {
        this.parent = parent;
        this.offsetX = parent.getType().getSpotLightOffsetX();
        this.offsetY = parent.getType().getSpotLightOffsetY();
        this.intensity = parent.getType().getSpotLightIntensity();
        this.flicker = 1.0;
        this.shouldFlicker = parent.getType().shouldSpotLightFlicker();

        this.width = SPOT_LIGHT_SIZE * (getIntensity() * getFlicker());
        this.height = SPOT_LIGHT_SIZE * (getIntensity() * getFlicker());
        this.random = new Random();
    }

    public void update(double dt) {
        if (flickerCooldown < FLICKER_FREQUENCY) {
            flickerCooldown += 1 * dt;
        }

        if (isShouldFlicker() && flickerCooldown >= FLICKER_FREQUENCY) {
            flicker = random.nextDouble(1.0, random.nextDouble(1.12, 1.2));
            flickerCooldown = 0;
        }
    }

    public void render(Camera cam) {
        double decoOffsetX = getParent().getLocation().getX() + cam.centerOffsetX;
        double decoOffsetY = getParent().getLocation().getY() + cam.centerOffsetY;

        cam.game.drawImage(cam.game.getTexture("spot_light"),
                decoOffsetX + getOffsetX(), decoOffsetY - getParent().getHeight() + Game.BLOCK_SIZE + getOffsetY(),
                getWidth(), getHeight());
    }

    public Decoration getParent() {
        return parent;
    }

    public double getIntensity() {
        return intensity;
    }

    private double getFlicker() {
        return flicker;
    }

    public boolean isShouldFlicker() {
        return shouldFlicker;
    }

    public double getOffsetX() {
        return offsetX - (getWidth() / 2);
    }

    public void setOffsetX(double x) {
        this.offsetX = x;
    }

    public double getOffsetY() {
        return offsetY - (getHeight() / 2);
    }

    public void setOffsetY(double y) {
        this.offsetY = y;
    }

    public double getWidth() {
        return width * getIntensity() * getFlicker();
    }

    public double getHeight() {
        return height * getIntensity() * getFlicker();
    }
}
