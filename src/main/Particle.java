package main;


import level.Level;

import java.awt.image.BufferedImage;


public class Particle
{
    Location spawnLoc;
    double fader;
    boolean isActive;
    Level level;
    BufferedImage image;
    public Particle(Location spawnLoc, double fader, BufferedImage image, Level level){
        this.spawnLoc = spawnLoc;
        this.fader = fader;
        this.image = image;
        this.level =  level;
        this.isActive = true;

    }

  public void  update(double dt){
      if (fader < 1) {
          fader += 1 * dt;
      }
      if  (fader >= 1) {
          setActive(false);

      }


    }

    public void render(Camera cam){

        double hitboxOffsetX = spawnLoc.getX() + cam.centerOffsetX;
        double hitboxOffsetY = spawnLoc.getY() + cam.centerOffsetY;
        cam.game.drawImage(image,hitboxOffsetX,hitboxOffsetY,32,32);



    }

    public boolean isActive(){
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }


}
