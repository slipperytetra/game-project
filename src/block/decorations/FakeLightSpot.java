package block.decorations;

import main.Camera;
import main.Game;
import main.GameObject;

import java.util.Random;

public class FakeLightSpot {

    private GameObject parent;
    private double offsetX, offsetY, width, height;
    private double intensity = 1.0;
    private Random random;

    private double flicker;
    private boolean shouldFlicker;
    private double flickerCooldown;
    private boolean isActive;

    private final double SPOT_LIGHT_SIZE = 75;
    private final double FLICKER_FREQUENCY = 0.1; //seconds

    public FakeLightSpot(GameObject parent) {
        this.parent = parent;
        this.flicker = 1.0;
        if (parent instanceof Decoration deco) {
            this.shouldFlicker = deco.getType().shouldSpotLightFlicker();
            this.offsetX = deco.getType().getSpotLightOffsetX();
            this.offsetY = deco.getType().getSpotLightOffsetY();
            this.intensity = deco.getType().getSpotLightIntensity();
        }
        this.isActive = true;

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
        double decoOffsetX = cam.toScreenX(getParent().getLocation().getX() + getOffsetX());
        double decoOffsetY = cam.toScreenY(getParent().getLocation().getY() - getParent().getHeight() + Game.BLOCK_SIZE + getOffsetY());

        cam.game.drawImage(cam.game.getTextureBank().getTexture("spot_light").getImage(),
                decoOffsetX , decoOffsetY , getWidth() , getHeight() );
    }

    public GameObject getParent() {
        return parent;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    private double getFlicker() {
        return flicker;
    }

    public void setFlicker(double flicker) {
        this.flicker = flicker;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
