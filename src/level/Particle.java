package level;


import main.Camera;
import utils.Location;
import utils.Texture;

import java.util.Random;


public class Particle {
    Location loc;
    double timeAlive, ticksAlive;
    boolean isActive;
    Level level;
    Texture image;
    ParticleTypes type;

    double opacity;
    double size;
    double offsetX, offsetY;
    double velX, velY;
    final double speed = 32; //pixels per second
    double initialSize;
    double scale;
    public Particle(ParticleTypes type, Location spawnLoc, Level level){
        this.loc = spawnLoc;
        this.timeAlive = type.getTimeAlive();
        this.type = type;
        this.image = level.getManager().getEngine().getTextureBank().getTexture(type.toString().toLowerCase());
        this.level =  level;

        this.isActive = true;
        this.ticksAlive = 0;
        this.opacity = 1.0;
        this.velX = getType().getVelX();
        this.velY = getType().getVelY();
        this.scale = 1.0;
        this.size = 32;
        if (type.getMinSize() > -1 && type.getMaxSize() > -1) {
            if (type.getMinSize() == type.getMaxSize()) {
                scale = type.getMaxSize();
            } else {
                Random rand = new Random();
                scale = rand.nextDouble(type.getMinSize(), type.getMaxSize());
            }
            this.size *= scale;
        }

        this.offsetX = 0;
        this.offsetY = 0;
        if (type.getOffset() > 0) {
            Random rand = new Random();
            this.offsetX += rand.nextDouble(-type.getOffset(), type.getOffset());
            this.offsetY += rand.nextDouble(-type.getOffset(), type.getOffset());
        }

        this.initialSize = size;
        this.loc.setX(loc.getX() - ((image.getWidth() * scale) / 2));
        this.loc.setY(loc.getY() - ((image.getHeight() * scale) / 2));
    }

  public void update(double dt){
        if (!isActive) {
            return;
        }

        opacity = opacity - ((1 / timeAlive) * dt);
        if (getType().isShrink()) {
            //System.out.println(size + " - " + (((initialSize / timeAlive) * dt)));
            size = size - ((initialSize / timeAlive) * dt);
        }

        if (ticksAlive < timeAlive) {
            ticksAlive += 1 * dt;
        }

        if  (ticksAlive >= timeAlive) {
            setActive(false);
        }

        if (getVelX() != 0) {
            this.loc.setX(loc.getX() + ((getVelX() * speed) * dt));
        }

        if (getVelY() != 0) {
            this.loc.setY(loc.getY() + ((getVelY() * speed) * dt));
        }
    }

    public void render(Camera cam){
        double camLocX = cam.toScreenX(loc.getX() + offsetX);
        double camLocY = cam.toScreenY(loc.getY() + offsetY);

        if (type.isFadeOut()) {
            cam.game.drawImage(image.getImage(), camLocX, camLocY, size * cam.getZoom(), size * cam.getZoom(), (float) opacity);
        } else {
            cam.game.drawImage(image.getImage(), camLocX, camLocY, size * cam.getZoom(), size * cam.getZoom());
        }
    }

    public ParticleTypes getType() {
        return type;
    }

    public boolean isActive(){
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public double getTimeAlive() {
        return timeAlive;
    }

    public void setTimeAlive(double timeAlive) {
        this.timeAlive = timeAlive;
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }
}
