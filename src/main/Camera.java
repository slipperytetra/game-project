package main;

import block.Block;
import block.BlockTypes;
import level.TextMessage;

import java.awt.*;

public class Camera {

    public Location loc, point1, point2, centerPoint;
    public Game game;
    public Player player;
    public double zoom = 1.0;
    public boolean showHitboxes;
    public double centerOffsetX, centerOffsetY;

    public Camera(Game game, Player p) {
        this.game = game;
        this.player = p;
        this.loc = new Location(p.getLocation().getX(), p.getLocation().getY());
        this.point1 = new Location(p.getLocation().getX() - (game.width() / 2), p.getLocation().getY() - (game.height() / 2));
        this.point2 = new Location(p.getLocation().getX() + (game.width() / 2), p.getLocation().getY() + (game.height() / 2));
    }

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
        renderBlocks();
        getPlayer().render(this);
        renderEnemies();
        renderTextMessages();
        renderUI();
    }

    private void renderBlocks() {
        for (int x = 0; x < game.activeLevel.getBlockGrid().getWidth(); x++) {
            for (int y = 0; y < game.activeLevel.getBlockGrid().getHeight(); y++) {
                Block b = game.activeLevel.getBlockGrid().getBlocks()[x][y];
                if (b.getType() == BlockTypes.VOID) {
                    continue;
                }

                if (b.getLocation().isBlockBetween(point1, point2)) {
                    double blockOffsetX = b.getLocation().getX() + centerOffsetX;
                    double blockOffsetY = b.getLocation().getY() + centerOffsetY;

                    b.drawBlock(this, blockOffsetX, blockOffsetY, centerOffsetX, centerOffsetY);
                }
            }
        }
    }

    public void renderEnemies() {
        for (Enemy enemy : game.activeLevel.getEnemies()) {
            if (enemy.getLocation().isBetween(point1, point2)) {
                enemy.render(this);
            }
        }
    }

    public void renderTextMessages() {
        for (int i = 0; i < game.activeLevel.getTextMessages().size(); i++) {
            TextMessage txtMsg = game.activeLevel.getTextMessages().get(i);
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
                game.drawText(zoom * localXDiff, zoom * localYDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
            } else {
                game.drawBoldText(zoom * localXDiff, zoom * localYDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
            }
        }
    }

    public void renderUI() {
        Location healthBarLoc = new Location(50, 35);

        double localXDiff = healthBarLoc.getX();
        double localYDiff = healthBarLoc.getY();

        game.drawText(50,35,"Health:",15);
        game.changeColor(Color.RED);
        game.drawSolidRectangle(localXDiff,localYDiff, player.getHealth(), 15);
        //game.drawText(zoom * localXDiff, zoom * localYDiff, txtMsg.getText(), "Serif", txtMsg.getFontSize());
    }

    public Player getPlayer() {
        return player;
    }
}
