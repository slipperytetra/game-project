package main;

import block.Block;
import block.BlockTypes;
import level.TextMessage;

import java.awt.*;

/*
*   This class handles the rendering of all objects on the screen.
*
*   It works by making the players location the center of the screen and only drawings the objects that are
*   around the player, taking into account the dimensions of the screen.
*
* */
public class Camera {

    public Location loc, point1, point2, centerPoint;
    public Game game;
    public Player player;
    public boolean showHitboxes;
    public double centerOffsetX, centerOffsetY;

    public Camera(Game game, Player p) {
        this.game = game;
        this.player = p;
        this.loc = new Location(p.getLocation().getX(), p.getLocation().getY());

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
        this.point1 = new Location(p.getLocation().getX() - (game.width() / 2), p.getLocation().getY() - (game.height() / 2));
        this.point2 = new Location(p.getLocation().getX() + (game.width() / 2), p.getLocation().getY() + (game.height() / 2));
    }

    /*
    *   This method constantly updates the points. This is needed because the players location always changes.
    * */
    public void update() {
        this.loc.setX(player.getLocation().getX());
        this.loc.setY(player.getLocation().getY());

        centerPoint = new Location(game.width() / 2, game.height() / 2);
        centerOffsetX = centerPoint.getX() - player.getLocation().getX();
        centerOffsetY = centerPoint.getY() - player.getLocation().getY();

        this.point1.setX((player.getLocation().getX() - (double) game.width() / 2));
        this.point1.setY((player.getLocation().getY() - (double) game.height() / 2));

        this.point2.setX((player.getLocation().getX() + (double) game.width() / 2));
        this.point2.setY((player.getLocation().getY() + (double) game.height() / 2));
    }



    public void draw() {
        renderBackground();
        renderBlocks();
        getPlayer().render(this);
        renderEntities();
        renderTextMessages();
        renderUI();
    }

    public void renderBackground() {
        if (game.imageBank.get("background") != null) {
            //System.out.println("Draw bg");
            game.drawImage(game.imageBank.get("background"), 0, 0, game.width(), game.height());
        }
    }

    /*
    *   This is where each individual block is drawn. It goes through the entire world map and checks if the blocks are
    *   between our point1 and point2. If they are, this means they are visible to the camera and should be drawn.
    *
    * */
    private void renderBlocks() {
        for (int x = 0; x < game.getActiveLevel().getBlockGrid().getWidth(); x++) {
            for (int y = 0; y < game.getActiveLevel().getBlockGrid().getHeight(); y++) { //Iterating over all the blocks
                Block b = game.getActiveLevel().getBlockGrid().getBlocks()[x][y]; //Getting the block from the grid based on the coordinates
                if (b.getType() == BlockTypes.VOID || b.getType() == BlockTypes.BARRIER) {
                    continue; //Skip void because its an empty block.
                }

                if (b.getLocation().isBlockBetween(point1, point2)) {
                    /*
                    *   Here we have to convert the blocks coordinates to be relative to the camera.
                    *   Basically in update(dt) we calculate the centerOffset by getting the center of the screen and then subtracting
                    *   the players location from it.
                    *
                    *   Then in here, we get the block's location and add the centerOffset to it.
                    * */
                    double blockOffsetX = b.getLocation().getX() + centerOffsetX;
                    double blockOffsetY = b.getLocation().getY() + centerOffsetY;

                    b.drawBlock(this, blockOffsetX, blockOffsetY);
                }
            }
        }
    }

    public void renderEntities() {
        for (Entity entity : game.getActiveLevel().getEntities()) {
            if (entity.getLocation().isBetween(point1, point2) && entity.isActive()) {
                entity.render(this);
            }
        }
    }

    public void renderTextMessages() {
        for (TextMessage txtMsg : game.getActiveLevel().getTextMessages().values()) {
            if (txtMsg == null) {
                continue;
            }

            double localXDiff = txtMsg.getLocation().getX();
            double localYDiff = txtMsg.getLocation().getY();
            if (!txtMsg.isStatic()) {
                localXDiff += centerOffsetX;
                localYDiff += centerOffsetY;
            }

            game.changeColor(txtMsg.getColor());
            if (!txtMsg.isBold()) {
                game.drawText(localXDiff, localYDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
            } else {
                game.drawBoldText(localXDiff, localYDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
            }
        }
    }

    public void renderUI() {
        Location healthBarLoc = new Location(50, 35);

        double localXDiff = healthBarLoc.getX();
        double localYDiff = healthBarLoc.getY();

        game.drawText(50,35,"Health:",15);
        game.drawText(1200,50,"Key : ", 20);
        if(game.getActiveLevel().getPlayer().hasKey()){
            game.drawImage(game.imageBank.get("key"),1230,20,50,50);
        }

        game.changeColor(Color.RED);
        game.drawSolidRectangle(localXDiff,localYDiff, player.getHealth(), 15);
        //game.drawText(zoom * localXDiff, zoom * localYDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
    }

    public Player getPlayer() {
        return player;
    }
}
