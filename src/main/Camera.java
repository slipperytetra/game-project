package main;

import block.Block;
import block.BlockTypes;
import block.decorations.Decoration;
import block.decorations.FakeLightSpot;
import entity.Entity;
import entity.EntityLiving;
import entity.Player;
import level.Particle;
import level.TextMessage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

/*
*   This class handles the rendering of all objects on the screen.
*
*   It works by making the players location the center of the screen and only drawings the objects that are
*   around the player, taking into account the dimensions of the screen.
*
* */
public class Camera {

    public Location loc, centerPoint;
    public Game game;
    public Player player;

    public boolean debugMode;
    private int DEBUG_ENTITIES_ON_SCREEN;
    private int DEBUG_BLOCKS_ON_SCREEN;
    private int DEBUG_DECORATIONS_ON_SCREEN;
    private int DEBUG_PARTICLES_ON_SCREEN;
    private double zoom;

    double camWidth;
    double camHeight;
    double boundsX;
    double boundsY;
    double tempLocX;
    double tempLocY;

    private long lastFpsCheck = 0;
    private int currentFps = 0;
    private int totalFrames = 0;
    private CollisionBox collisionBox;
    private Location focusPoint;
    private GameObject focusObj;

    private Texture textBg;
    private Texture textMg;
    private Texture textFg;

    public double centerOffsetX, centerOffsetY;

    public Camera(Game game, Player p) {
        this.game = game;
        this.player = p;
        this.loc = new Location(0, 0);
        this.zoom = 1;

        this.camWidth = 1280;
        this.camHeight = 720;

        this.textBg = game.imageBank.get("background");
        this.textMg = game.imageBank.get("midground");
        this.textFg = game.imageBank.get("foreground");
        /*
        *   'point1' is the top left location of the screen.
        *   'point2' is the bottom right location of the screen.
        *
        *   It works these out by getting the players location and then subtracting half of the screen's width and height
        *   to get the first point (point1), then adding the screen's width and height for the second point (point2).
        *
        *   By having these two points, we can see if world objects are between these two points, and if so then draw them. If they
        *   aren't then we can just ignore them.
        * */

        this.collisionBox = new CollisionBox(0, 0, camWidth, camHeight);
        setFocusPoint(getPlayer().getLocation());
        //this.centerPoint = new Location(camWidth, camHeight);
        //this.focusPoint = new Location(getPlayer().getLocation().getX(), getPlayer().getLocation().getY());
    }

    /*
    *   This method constantly updates the points. This is needed because the players location always changes.
    * */
    public void update() {
        calculateFPS();
        setFocusPoint(getPlayer().getLocation());

        boundsX = game.getActiveLevel().getActualWidth() - camWidth;
        boundsY = game.getActiveLevel().getActualHeight() - camHeight;

        tempLocX = getFocusPoint().getX() - camWidth / 2;
        if (tempLocX < 0) {
            tempLocX = 0;
        } else if (tempLocX > boundsX) {
            tempLocX = boundsX;
        }

        tempLocY = getFocusPoint().getY() - camHeight / 2;
        if (tempLocY < 0) {
            tempLocY = 0;
        } else if (tempLocY > boundsY) {
            tempLocY = boundsY;
        }

        loc.setX(tempLocX);
        loc.setY(tempLocY);

        collisionBox.setLocation(loc.getX(), loc.getY());
    }

    public void draw() {
        renderBackground();
        renderDecorations();
        renderBlocks();
        renderEntities();
        getPlayer().render(this);
        renderTextMessages();
        renderFX();
        renderSpotLights();
        renderUI();
    }




    /*
    *   Most renderer functions work the same way. They look through all the objects in the relevant list,
    *   in this case its decorations. Then it calls their render function which just draws the object's texture
    *   taking into account certain factors like scale, offsets ect.
    *
    *   It also checks if the object is viewable on the camera before drawing it by comparing its collision
    *   box to the camera's collision box (width and height of the screen)
    * */
    private void renderDecorations() {
        DEBUG_DECORATIONS_ON_SCREEN = 0;
        for (Decoration deco : game.getActiveLevel().getDecorations()) {
            if (deco.getCollisionBox().collidesWith(this.getCollisionBox())) {
                deco.render(this);
                DEBUG_DECORATIONS_ON_SCREEN++;
            }
        }
    }

    private void renderSpotLights() {
        for (FakeLightSpot spotLight : game.getActiveLevel().getSpotLights()) {
            if (spotLight.getParent().getCollisionBox().collidesWith(this.getCollisionBox())) {
                spotLight.render(this);
            }
        }
    }

    /*
    *   This is where each individual block is drawn. It goes through the entire world map and checks if the blocks are
    *   between our point1 and point2. If they are, this means they are visible to the camera and should be drawn.
    *
    * */
    private void renderBlocks() {
        DEBUG_BLOCKS_ON_SCREEN = 0;
        for (int x = 0; x < game.getActiveLevel().getBlockGrid().getWidth(); x++) {
            for (int y = 0; y < game.getActiveLevel().getBlockGrid().getHeight(); y++) { //Iterating over all the blocks
                Block b = game.getActiveLevel().getBlockGrid().getBlocks()[x][y]; //Getting the block from the grid based on the coordinates
                if (b.getType() == BlockTypes.VOID) {
                    continue;
                }

                if (b.getLocation().isBlockBetween(getPoint1(), getPoint2())) {
                    /*
                    *   Here we have to convert the blocks coordinates to be relative to the camera.
                    *   Basically in update(dt) we calculate the centerOffset by getting the center of the screen and then subtracting
                    *   the players location from it.
                    *
                    *   Then in here, we get the block's location and add the centerOffset to it.
                    * */

                    b.render(this);
                    DEBUG_BLOCKS_ON_SCREEN++;
                }
            }
        }
    }


    /*
    *   Same renderer concept, entities just have an active boolean.
    * */
    public void renderEntities() {
        DEBUG_ENTITIES_ON_SCREEN = 0;
        for (Entity entity : game.getActiveLevel().getEntities()) {
            if (!entity.isActive()) {
                continue;
            }

            if (entity.getCollisionBox().collidesWith(this.getCollisionBox())) {
                entity.render(this);
                double offsetX = toScreenX(entity.getLocation().getX());
                double offsetY = toScreenY(entity.getLocation().getY());

                if(entity.getHealth() < entity.getMaxHealth()){
                    drawHealthBar(entity, offsetX, offsetY - 20);
                }

                DEBUG_ENTITIES_ON_SCREEN++;
            }
        }
    }


    /*
    *   This renders text in the actual level. It's mostly used in the demo levels for displaying help
    *   messages.
    *
    *   Levels have an embedded list of TextMessage objects that have an assigned location, string and other
    *   string attributes. Makes it easier for displaying the text relative to the player's position.
    * */
    public void renderTextMessages() {
        for (TextMessage txtMsg : game.getActiveLevel().getTextMessages().values()) {
            if (txtMsg == null) {
                continue;
            }

            double localXDiff = txtMsg.getLocation().getX();
            double localYDiff = txtMsg.getLocation().getY();
            if (!txtMsg.isStatic()) {
                localXDiff = toScreenX(localXDiff);
                localYDiff = toScreenY(localYDiff);
            }

            game.changeColor(txtMsg.getColor());
            if (!txtMsg.isBold()) {
                game.drawText(localXDiff, localYDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
            } else {
                game.drawBoldText(localXDiff, localYDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
            }
        }
    }

    /*
    *   Here is where all the UI related things are drawn. It displays the player health, pause menu, key ect
    *   and is the one of the last things to render so that it displays on top of the level.
    *
    *   It also displays all the debug information which is useful when making the game or testing features.
    * */
    public void renderUI() {
        if (game.isPaused) {
            game.changeColor(Color.orange);
            game.drawText((game.width() / 2) - 100, game.height() / 2, "Paused", 75);
            game.drawText((game.width() / 2) - 110, game.height() / 2 + 100, "Press 'Q' to Quit.", 40);

            return;
        }

        if (game.getActiveLevel().getPlayer().hasKey()) {
            game.drawImage(game.imageBank.get("key").getImage(), game.width() - 50, 20, 30, 30);
        }

        double localXDiff = 50;
        double localYDiff = 50;
        drawHealthBar(player, localXDiff + 20, localYDiff - 7);
        game.drawImage(game.getTexture("ui_heart").getImage(), localXDiff - 19, localYDiff - 19, 38, 38);
        game.drawImage(game.getTexture("gold_coin").getImage(), localXDiff - 19, localYDiff + 25, 35, 35);
        game.changeColor(Color.ORANGE);
        game.drawBoldText(localXDiff + 20, localYDiff + 55, "" + getPlayer().getCoins(), 35);


        if (debugMode) { //Press 'H' to enable
            game.changeColor(Color.cyan);
            game.drawText(25, 120, "fps: " + currentFps, "Serif", 20);
            game.drawText(25, 140, "entities on screen: " + DEBUG_ENTITIES_ON_SCREEN, "Serif", 20);
            game.drawText(25, 160, "blocks on screen: " + DEBUG_BLOCKS_ON_SCREEN, "Serif", 20);
            game.drawText(25, 180, "decorations on screen: " + DEBUG_DECORATIONS_ON_SCREEN, "Serif", 20);
            game.drawText(25, 220, "particles on screen: " + DEBUG_PARTICLES_ON_SCREEN, "Serif", 20);
            game.drawText(25, 240, "player:", "Serif", 20);
            game.drawText(35, 260, "pos: " + getPlayer().getLocation().toString(), "Serif", 20);
            game.drawText(35, 260, "velocity: " + Math.round(getPlayer().moveX) + ", " + Math.round(getPlayer().moveY), "Serif", 20);
            if (getPlayer().getTarget() != null) {
                game.drawText(35, 300, "target: " + getPlayer().getTarget().toString(), "Serif", 20);
            } else {
                game.drawText(35, 300, "target: null", "Serif", 20);
            }
            game.drawText(35, 320, "onGround: " + getPlayer().isOnGround(), "Serif", 20);
            game.drawText(35, 340, "hasKey: " + getPlayer().hasKey(), "Serif", 20);

            game.changeColor(Color.RED);
            game.drawRectangle(toScreenX(getCollisionBox().getLocation().getX()), toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth(), getCollisionBox().getHeight());
        }

        if (game.getActiveLevel().isEditMode()) {
            game.getEditor().render(this);
        }
    }
    public void renderFX(){
        DEBUG_PARTICLES_ON_SCREEN = 0;
        for (Particle particle : game.getActiveLevel().getParticles()){
            particle.render(this);
            DEBUG_PARTICLES_ON_SCREEN++;
        }

        if (game.imageBank.get("overlay") != null) {
            game.drawImage(game.imageBank.get("overlay").getImage(), 0, 0, game.width(), game.height());
        }
    }

    private void renderBackground() {
        if (textBg != null) {
            game.drawImage(textBg.getImage(), 0, 0, game.width(), game.height());
        }

        drawParallaxImage(textMg, 0.5);
        drawParallaxImage(textFg, 0.85);
    }

    private void drawParallaxImage(Texture bg, double paraZoom) {
        if (bg != null) {
            double scaleX = game.getActiveLevel().getActualWidth() / bg.getWidth(); //remember to do this only once somewhere else
            double scaleY = game.getActiveLevel().getActualHeight() / bg.getHeight();

            double camBotLeftX = (0 + ((0 + getCollisionBox().getLocation().getX()) * paraZoom)) / scaleX;
            double camBotLeftY = (0 + ((0 + getCollisionBox().getLocation().getY()) * paraZoom)) / scaleY;
            double camTopRightX = (game.getActiveLevel().getActualWidth() - ((game.getActiveLevel().getActualWidth() - getCollisionBox().getCorner().getX()) * paraZoom)) / scaleX;
            double camTopRightY = (game.getActiveLevel().getActualHeight() - ((game.getActiveLevel().getActualHeight() - getCollisionBox().getCorner().getY()) * paraZoom)) / scaleY;

            int width = (int) ((camTopRightX - camBotLeftX));
            int height = (int) ((camTopRightY - camBotLeftY));

            game.drawImage(bg.getImage().getSubimage((int) camBotLeftX, (int) camBotLeftY, width, height), 0, 0, game.width(), game.height());
        }
    }

    public Player getPlayer() {
        return player;
    }

    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    public Location getPoint1() {
        return getCollisionBox().getLocation();
    }

    public Location getPoint2() {
        return getCollisionBox().getCorner();
    }

    private void calculateFPS() {
        totalFrames++;
        if (System.nanoTime() > lastFpsCheck + 1000000000) {
            lastFpsCheck = System.nanoTime();
            currentFps = totalFrames;
            totalFrames = 0;
        }
    }

    public void drawHealthBar(Entity entity, double xPos, double yPos) {
        double difference = (double) 100 / entity.getMaxHealth();
        double barSize = entity.getHealth() * difference;

        game.changeColor(Color.red);
        game.drawSolidRectangle(xPos,yPos, barSize, 15);
        game.changeColor(Color.darkGray);
        game.drawSolidRectangle(xPos + barSize,yPos, 100 - barSize, 15);
    }

    public Location getFocusPoint() {
        return focusPoint;
    }

    public void setFocusPoint(Location loc) {
        if (focusPoint == null) {
            focusPoint = new Location(0, 0);
        }

        this.focusPoint.setX(loc.getX());
        this.focusPoint.setY(loc.getY());
    }

    public GameObject getFocusObject() {
        return focusObj;
    }

    public void setFocusObject(GameObject object) {
        this.focusObj = object;
    }

    public double getZoom() {
        return zoom;
    }

    public double toScreenX(double worldX) {
        return worldX - loc.getX();
    }

    public double toScreenY(double worldY) {
        return worldY - loc.getY();
    }

    public double toWorldX(double screenX) {
        return loc.getX() + screenX;
    }

    public double toWorldY(double screenY) {
        return loc.getY() + screenY;
    }
}
