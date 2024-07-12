package level;


import main.Camera;
import main.Game;
import utils.Location;
import utils.Texture;
import utils.TextureAnimated;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;


public class Particle {
    Location loc;
    double timeAlive, ticksAlive;
    boolean isActive;
    Level level;
    Texture image;
    ParticleTypes type;

    boolean isFlipped;
    double opacity;
    double size;
    double offsetX, offsetY;
    double forceX, forceY;
    double velX, velY;
    final double speed = 32; //pixels per secondw
    double initialSize;
    double scale;
    double rotation;

    private final double GRAVITY = 32 * Game.BLOCK_SIZE;

    public Particle(ParticleTypes type, Location spawnLoc, Level level){
        this.loc = spawnLoc;
        this.timeAlive = type.getTimeAlive();
        this.type = type;
        this.image = level.getManager().getEngine().getTextureBank().getTexture(type.toString().toLowerCase());
        this.level =  level;

        this.isActive = true;
        this.ticksAlive = 0;
        this.opacity = 1.0;
        this.velX = getType().getVelX() * Game.BLOCK_SIZE;
        this.velY = getType().getVelY() * Game.BLOCK_SIZE;
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


        if (getVelY() != 0 || type.hasGravity()) {
            if (type.hasGravity()) {
                setVelY(getVelY() + (GRAVITY * dt));
            }
        }


        if (getType() == ParticleTypes.SMOKE) {
            rotation += 1.5 * dt;
        }

        this.loc.setX(loc.getX() + (getVelX() * dt));
        this.loc.setY(loc.getY() + (getVelY() * dt));
    }

    public void render(Camera cam){
        double sizeDiff = (initialSize - size) / 2;
        double camLocX = cam.toScreenX(loc.getX() + sizeDiff + offsetX);
        double camLocY = cam.toScreenY(loc.getY() + sizeDiff + offsetY);

        image.setFlipped(isFlipped);

        BufferedImage img = image.getImage();
        if (image instanceof TextureAnimated textA) {
            img = textA.getFrame((int)ticksAlive);
        }

        if (img == null) {
            return;
        }

        Graphics2D g2d = cam.game.mGraphics;
        AffineTransform oldTrans = g2d.getTransform();
        g2d.translate(camLocX, camLocY);
        if (rotation != 0) {
            g2d.rotate(rotation, size / 2, size / 2);
        }
        //g2d.scale(scale, scale);
        //g2d.drawImage(img, 0, 0, null);

        if (type.isFadeOut()) {
            drawImage(g2d, img, (float) opacity);
            //cam.game.drawImage(img, camLocX, camLocY, size, size, (float) opacity);
        } else {
            drawImage(g2d, img, 1f);
            //cam.game.drawImage(img, camLocX, camLocY, size, size);
        }
        g2d.setTransform(oldTrans);
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
        this.velX = velX * Game.BLOCK_SIZE;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
    }

    public Location getLocation() {
        return loc;
    }


    private void drawImage(Graphics2D g2d, BufferedImage img, float opacity) {
        if (opacity < 0) {
            opacity = 0;
        }

        if (opacity < 1) {
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity);
            g2d.setComposite(ac);
            g2d.drawImage(img, 0, 0, (int)size, (int)size, null,null);
            ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
            g2d.setComposite(ac);
        } else {
            g2d.drawImage(img, 0, 0, (int)size, (int)size, null,null);
        }
    }
}
