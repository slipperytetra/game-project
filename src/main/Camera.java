package main;

import block.Block;
import block.BlockTypes;
import block.decorations.Decoration;
import block.decorations.FakeLightSpot;
import entity.Entity;
import entity.EntityType;
import entity.Player;
import level.Particle;
import level.TextMessage;
import level.item.Inventory;
import utils.CollisionBox;
import utils.Location;
import utils.Texture;
import utils.Vector;

import java.awt.*;
import java.util.Random;

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
    private Random rand;

    public boolean debugMode;
    public boolean isShaking;
    private double shakeTicks, shakeCooldown;
    private int DEBUG_ENTITIES_ON_SCREEN;
    private int DEBUG_BLOCKS_ON_SCREEN;
    private int DEBUG_DECORATIONS_ON_SCREEN;
    private int DEBUG_PARTICLES_ON_SCREEN;

    double camWidth;
    double camHeight;
    double boundsX;
    double boundsY;
    double tempLocX;
    double tempLocY;
    double shakeOffsetX;
    double shakeOffsetY;

    private long lastFpsCheck = 0;
    private int currentFps = 0;
    private int totalFrames = 0;
    private CollisionBox collisionBox;
    private Location focusPoint;
    private GameObject focusObj;

    private Texture textBg;
    private Texture textMg;
    private Texture textFg;
    public Vector velocity;

    private double deadZoneX, deadZoneY;
    private double deadZoneSize = Game.BLOCK_SIZE * 2;

    public Camera(Game game, Player p) {
        this.game = game;
        this.player = p;
        this.loc = new Location(0, 0);

        this.camWidth = 1280;
        this.camHeight = 720;

        this.textBg = game.getTextureBank().getTexture("background");
        this.textMg = game.getTextureBank().getTexture("midground");
        this.textFg = game.getTextureBank().getTexture("foreground");
        this.rand = new Random();
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

        this.velocity = new Vector(0, 0);
        setFocusPoint(getPlayer().getLocation());
        loc.setX(getFocusPoint().getX() - (camWidth / 2.0));
        loc.setY(getFocusPoint().getY() - (camHeight / 2.0));
        this.collisionBox = new CollisionBox(loc.getX(), loc.getY(), camWidth, camHeight);

        boundsX = game.getActiveLevel().getActualWidth() - camWidth;
        boundsY = game.getActiveLevel().getActualHeight() - camHeight;

        tempLocX = getFocusPoint().getX() - (camWidth / 2.0);
        tempLocY = getFocusPoint().getY() - (camHeight / 2.0);
    }

    /*
    *   This method constantly updates the points. This is needed because the players location always changes.
    * */
    public void update(double dt) {
        calculateFPS();
        if (Game.isPaused) {
            return;
        }

        if (!game.getActiveLevel().isEditMode()) {
            setFocusPoint(getPlayer().getLocation());
        }
        trackFocus(dt);

        //deadZoneX = getFocusPoint().getX() - deadZoneSize + (getPlayer().getWidth() / 2);
        //deadZoneY = getFocusPoint().getY() - deadZoneSize + (getPlayer().getHeight() / 2);
    }

    public void trackFocus(double dt) {
        if (!game.getActiveLevel().isEditMode()) {
            boundsX = game.getActiveLevel().getActualWidth() - camWidth;
            boundsY = game.getActiveLevel().getActualHeight() - camHeight;

            tempLocX = getFocusPoint().getX() - (camWidth / 2.0);
            tempLocY = getFocusPoint().getY() - (camHeight / 2.0);

            velocity.setX(tempLocX - loc.getX());
            velocity.setY(tempLocY - loc.getY());
            double offsetX = 0;
            double offsetY = 0;

            if (getPlayer().getVelocity().getX() > 0 && !getPlayer().isAttacking()) {
                offsetX = 0.025 * getPlayer().getVelocity().getX();
            } else if (getPlayer().getVelocity().getX() < 0 && !getPlayer().isAttacking()) {
                offsetX = -Math.abs(0.025 * getPlayer().getVelocity().getX());
            }

            if (isShaking()) {
                shakeOffsetX = rand.nextDouble(-2, 2);
                shakeOffsetY = rand.nextDouble(-2, 2);
            } else {
                shakeOffsetX = 0;
                shakeOffsetY = 0;
            }


            tempLocX = (loc.getX() + shakeOffsetX + offsetX) + (velocity.getX() * dt);
            tempLocY = (loc.getY() + shakeOffsetY + offsetY) + (velocity.getY() * dt);

            if (shakeTicks < shakeCooldown) {
                shakeTicks++;
                isShaking = true;
            } else {
                isShaking = false;
            }
        }

        if (tempLocX < 0) {
            tempLocX = 0;
            velocity.setX(0);
        } else if (tempLocX > boundsX) {
            tempLocX = boundsX;
            velocity.setX(0);
        }

        if (tempLocY < 0) {
            tempLocY = 0;
            velocity.setY(0);
        } else if (tempLocY > boundsY) {
            tempLocY = boundsY;
            velocity.setY(0);
        }

        loc.setX(tempLocX);
        loc.setY(tempLocY);

        collisionBox.setLocation(tempLocX, tempLocY);
    }

    public void draw() {
        renderBackground();
        renderDecorations();
        renderBlocks();
        renderEntities();
        if (!game.getActiveLevel().isEditMode()) {
            getPlayer().render(this);
        }
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
        if (Game.isPaused) {
            game.changeColor(Color.orange);
            game.drawText((game.width() / 2) - 100, game.height() / 2, "Paused", 75);
            game.drawText((game.width() / 2) - 110, game.height() / 2 + 100, "Press 'Q' to Quit.", 40);

            return;
        }

        if (game.getActiveLevel().getPlayer().hasKey()) {
            game.drawImage(game.getTextureBank().getTexture("key").getImage(), game.width() - 50, 20, 30, 30);
        }

        /*
        if (game.mouseBox != null) {
            game.changeColor(Color.ORANGE);
            game.drawRectangle(toScreenX(game.mouseBox.getLocation().getX()), toScreenY(game.mouseBox.getLocation().getY()), game.mouseBox.getWidth(), game.mouseBox.getHeight());
        }*/

        double localXDiff = 50;
        double localYDiff = 50;
        drawHealthBar(player, localXDiff + 20, localYDiff - 7);
        game.drawImage(game.getTextureBank().getTexture("ui_heart").getImage(), localXDiff - 19, localYDiff - 19, 38, 38);
        game.drawImage(game.getTextureBank().getTexture("gold_coin").getImage(), localXDiff - 19, localYDiff + 25, 35, 35);

        Texture texture = game.getTextureBank().getTexture("arrow");
        //texture.setRotation(0);
        game.drawImage(texture.getImageNoAffine(), game.width() - 75, 55, 35, 35);
        game.changeColor(Color.WHITE);
        game.drawBoldText(game.width() - 40, 80, "" + getPlayer().getArrows(), 22);
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
            game.drawText(35, 280, "velocity: " + Math.round(getPlayer().getVelocity().getX()) + ", " + Math.round(getPlayer().getVelocity().getY()), "Serif", 20);
            if (getPlayer().getTarget() != null) {
                game.drawText(35, 320, "target: " + getPlayer().getTarget().toString(), "Serif", 20);
            } else {
                game.drawText(35, 320, "target: null", "Serif", 20);
            }
            game.drawText(35, 340, "onGround: " + getPlayer().isOnGround(), "Serif", 20);
            game.drawText(35, 360, "hasKey: " + getPlayer().hasKey(), "Serif", 20);

            game.changeColor(Color.RED);
            game.drawRectangle(toScreenX(getCollisionBox().getLocation().getX()), toScreenY(getCollisionBox().getLocation().getY()), getCollisionBox().getWidth(), getCollisionBox().getHeight());
            game.getActiveLevel().getQuadTree().render(this);

            game.changeColor(Color.YELLOW);
            game.drawRectangle(toScreenX(getPlayer().tempBoxX.getLocation().getX()), toScreenY(getPlayer().tempBoxX.getLocation().getY()), getPlayer().tempBoxX.getWidth(), getPlayer().tempBoxX.getHeight());
        }

        if (game.getActiveLevel().isEditMode()) {
            game.getEditor().render(this);
        }

        for (Inventory inv : game.getActiveLevel().getOpenInventories()) {
            if (!inv.isOpen()) {
                continue;
            }

            inv.render(this, getPlayer().getLocation().getX(), getPlayer().getLocation().getY() - 96);
        }


        if (debugMode) {
            game.changeColor(Color.BLUE);
            for (GameObject gameObjectX : getPlayer().getCollisionsY()) {
                game.drawRectangle(toScreenX(gameObjectX.getLocation().getX()), toScreenY(gameObjectX.getLocation().getY()),
                        gameObjectX.getWidth(), gameObjectX.getHeight());
            }
            for (GameObject gameObjectY : getPlayer().getCollisionsY()) {
                game.drawRectangle(toScreenX(gameObjectY.getLocation().getX()), toScreenY(gameObjectY.getLocation().getY()),
                        gameObjectY.getWidth(), gameObjectY.getHeight());
            }


            /*
            game.changeColor(Color.RED);
            game.drawLine(toScreenX(getCenterX()), toScreenY(getCenterY()), toScreenX(deadZoneX), toScreenY(deadZoneY));

            game.drawRectangle(toScreenX(deadZoneX), toScreenY(deadZoneY), deadZoneSize * 2, deadZoneSize * 2);
            (/
             */
        }
    }

    public void renderFX(){
        DEBUG_PARTICLES_ON_SCREEN = 0;
        for (Particle particle : game.getActiveLevel().getParticles()){
            particle.render(this);
            DEBUG_PARTICLES_ON_SCREEN++;
        }

        if (game.getTextureBank().getTexture("overlay") != null) {
            game.drawImage(game.getTextureBank().getTexture("overlay").getImage(), 0, 0, game.width(), game.height());
        }
    }

    private void renderBackground() {
        if (textBg != null) {
            game.drawImage(textBg.getImage(), 0, 0, game.width() , game.height() );
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

            if (camBotLeftX < 0) {
                camBotLeftX = 0;
            }
            if (camBotLeftY < 0) {
                camBotLeftY = 0;
            }
            if (width > bg.getImage().getWidth()) {
                width = bg.getImage().getWidth();
            }
            if (height > bg.getImage().getHeight()) {
                height = bg.getImage().getHeight();
            }

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

    public boolean isShaking() {
        return isShaking;
    }

    public void shake(double time) {
        this.shakeCooldown = time;
        this.shakeTicks = 0;
    }

    public double getCenterX() {
        return loc.getX() + (camWidth / 2.0);
    }

    public double getCenterY() {
        return loc.getY() + (camHeight / 2.0);
    }
}
