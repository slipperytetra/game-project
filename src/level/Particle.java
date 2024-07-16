package level;


import main.Camera;
import main.Game;
import main.GameObject;
import utils.Location;
import utils.Texture;
import utils.TextureAnimated;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;


public class Particle extends GameObject {

    private Texture image;
    private ParticleTypes type;

    private boolean isFlipped;
    private double opacity;
    private double size;
    private double offsetX, offsetY;
    private double timeAlive, ticksAlive;
    private double velX, velY;
    private double initialSize;
    public double GRAVITY = Game.BLOCK_SIZE * 4;

    public Particle(ParticleTypes type, Location spawnLoc, Level level) {
        super(level, spawnLoc);

        this.timeAlive = type.getTimeAlive();
        this.type = type;
        this.image = level.getManager().getEngine().getTextureBank().getTexture(type.toString().toLowerCase());
        this.ticksAlive = 0;
        this.opacity = 1.0;
        this.velX = getType().getVelX() * Game.BLOCK_SIZE;
        this.velY = getType().getVelY() * Game.BLOCK_SIZE;
        this.size = 32;
        setCollisionBox(null);
        setScale(1.0);

        init();
    }

    public void init() {
        if (type.getMinSize() > -1 && type.getMaxSize() > -1) {
            if (type.getMinSize() == type.getMaxSize()) {
                setScale(type.getMaxSize());
            } else {
                Random rand = new Random();
                setScale(rand.nextDouble(type.getMinSize(), type.getMaxSize()));
            }
            this.size *= getScale();
        }

        this.offsetX = 0;
        this.offsetY = 0;
        if (type.getOffset() > 0) {
            Random rand = new Random();
            this.offsetX = rand.nextDouble(-type.getOffset(), type.getOffset());
            this.offsetY = rand.nextDouble(-type.getOffset(), type.getOffset());
        }

        this.initialSize = size;
        if (getType().isGrow()) {
            size = 0;
        }

        setLocation((getLocation().getX() + offsetX) - ((image.getWidth() * getScale()) / 2),
                (getLocation().getY() + offsetY) - ((image.getHeight() * getScale()) / 2));
    }

    public void update(double dt){
        if (!isActive()) {
            return;
        }

        opacity = opacity - ((1 / getTimeAlive()) * dt);
        if (getType().isShrink()) {
            size = size - ((initialSize / getTimeAlive()) * dt);
        } else if (getType().isGrow() && size < initialSize) {
            size = size + ((initialSize / getTimeAlive()) * dt);
        }

        if (ticksAlive < getTimeAlive()) {
            ticksAlive += 1 * dt;
        }

        if  (ticksAlive >= getTimeAlive()) {
            setActive(false);
        }


        if (getVelY() != 0 || type.hasGravity()) {
            if (type.hasGravity()) {
                setVelY((getVelY() + GRAVITY) * dt);
            }
        }


        if (getType() == ParticleTypes.SMOKE || getType() == ParticleTypes.LEAF || getType() == ParticleTypes.MUSHROOM_SPORES) {
            setRotation(getRotation() + (1.5 * dt));
        }

        setLocation(getLocation().getX() + (getVelX() * dt),
                getLocation().getY() + (getVelY() * dt));
    }

    public void render(Camera cam){
        double sizeDiff = (initialSize - size) / 2;
        double camLocX = cam.toScreenX(getLocation().getX() + sizeDiff);
        double camLocY = cam.toScreenY(getLocation().getY() + sizeDiff);

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
        if (getRotation() != 0) {
            g2d.rotate(getRotation(), size / 2, size / 2);
        }

        if (type.isFadeOut()) {
            drawImage(g2d, img, (float) opacity);
        } else {
            drawImage(g2d, img, 1f);
        }

        g2d.setTransform(oldTrans);
    }

    public ParticleTypes getType() {
        return type;
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
        this.velY = velY * Game.BLOCK_SIZE;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
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
