package main;

import block.Block;
import block.BlockTypes;
import level.TextMessage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Camera {

    public Location loc, point1, point2, localP;
    public Game game;
    public Player player;
    public int[][] pixels;
    public int zoom = 1;

    public Camera(Game game, Player p) {
        this.game = game;
        this.player = p;
        pixels = new int[Game.WIDTH][Game.HEIGHT];
        for (int x = 0; x < Game.WIDTH; x++) {
            for (int y = 0; y < Game.HEIGHT; y++) {
                pixels[x][y] = Color.WHITE.getRGB();
            }
        }
        this.loc = new Location(p.getLocation().getX(), p.getLocation().getY());
        this.point1 = new Location(p.getLocation().getX() - 250, p.getLocation().getY() - 250);
        this.point2 = new Location(p.getLocation().getX() + 250, p.getLocation().getY() + 250);
    }

    public void update() {
        this.loc.setX(player.getLocation().getX());
        this.loc.setY(player.getLocation().getY());

        double offsetX = Game.WIDTH / 2;
        double offsetY = Game.HEIGHT / 2;

        this.point1.setX(player.getLocation().getX() - offsetX);
        this.point1.setY(player.getLocation().getY() - offsetY);

        this.point2.setX(player.getLocation().getX() + offsetX);
        this.point2.setY(player.getLocation().getY() + offsetY);
        //System.out.println("P1: " + point1.toString());
        //System.out.println("P2: " + point2.toString());
    }


    public void draw() {
        localP = new Location(Game.WIDTH / 2, Game.HEIGHT / 2);
        double offsetX = localP.getX() - player.getLocation().getX();
        double offsetY = localP.getY() - player.getLocation().getY();

        for (int x = 0; x < game.activeLevel.getBlockGrid().getWidth(); x++) {
            for (int y = 0; y < game.activeLevel.getBlockGrid().getHeight(); y++) {
                Block b = game.activeLevel.getBlockGrid().getBlocks()[x][y];
                if (b.getType() == BlockTypes.VOID) {
                    continue;
                }
                if (b.getLocation().getX() > point1.getX() && b.getLocation().getX() < point2.getX()) {
                    if (b.getLocation().getY() > point1.getY() && b.getLocation().getY() < point2.getY()) {
                        double xDiff = b.getLocation().getX() + offsetX;
                        double yDiff = b.getLocation().getY() + offsetY;
                        Image texture = game.getTexture(b.getType().toString());
                        if (texture == null) {
                            System.out.println("Null image: " + b.getType().getFilePath());
                        }
                        game.drawImage(texture, xDiff, yDiff, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
                        //.changeColor(b.getColor());
                        //game.drawSolidRectangle(xDiff, yDiff, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
                    }
                }
            }
        }
        for (int x = 0; x < game.activeLevel.getBlockGrid().getWidth(); x++) {
            for (int y = 0; y < game.activeLevel.getBlockGrid().getHeight(); y++) {
                Block b = game.activeLevel.getBlockGrid().getBlocks()[x][y];
                if (b.getLocation().getX() > point1.getX() && b.getLocation().getX() < point2.getX()) {
                    if (b.getLocation().getY() > point1.getY() && b.getLocation().getY() < point2.getY()) {
                        double xDiff = b.getLocation().getX() + offsetX;
                        double yDiff = b.getLocation().getY() + offsetY;
                        game.changeColor(Color.GREEN);
                        game.drawRectangle(xDiff, yDiff, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
                    }
                }
            }
        }
        double xDiff = player.getLocation().getX() + offsetX;
        double yDiff = player.getLocation().getY() + offsetY;
        //game.changeColor(Color.pink);
        //game.drawSolidRectangle(xDiff, yDiff, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
        game.drawImage(game.getTexture("player"), xDiff, yDiff);
        game.changeColor(Color.red);
        game.drawRectangle(xDiff, yDiff, Game.BLOCK_SIZE, Game.BLOCK_SIZE);

        for (int i = 0; i < game.activeLevel.getTextMessages().size(); i++) {
            //System.out.println("Attempting to draw text (" + i + ")");
            TextMessage txtMsg = game.activeLevel.getTextMessages().get(i);
            if (txtMsg == null) {
                //System.out.println("-----it was null.");
                continue;
            }

            xDiff = txtMsg.getLocation().getX();
            yDiff = txtMsg.getLocation().getY();
            if (!txtMsg.isStatic()) {
                xDiff += offsetX;
                yDiff += offsetY;
            }

            game.changeColor(txtMsg.getColor());
            if (!txtMsg.isBold()) {
                //System.out.println("Drawing text '" + txtMsg.getText() + "' at " + txtMsg.getLocation().toString() + ", in Color:" + txtMsg.getColor().toString());
                game.drawText(xDiff, yDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
            } else {
                //System.out.println("Drawing text '" + txtMsg.getText() + "' at " + txtMsg.getLocation().toString() + ", in Color:" + txtMsg.getColor().toString());
                game.drawBoldText(xDiff, yDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
            }
        }
        //game.drawImage(game.idle, this.loc.getX(), this.loc.getY());
    }

    public Player getPlayer() {
        return player;
    }

}
