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
        renderPlayer();
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

                    b.drawBlock(this, blockOffsetX, blockOffsetY);
                }
            }
        }
    }

    private void renderPlayer() {
        double playerOffsetX = player.getLocation().getX() + centerOffsetX;
        double playerOffsetY = player.getLocation().getY() + centerOffsetY;

        if (player.isMovingHorizontally()) {
            game.drawImage(player.getRunFrame(), zoom * playerOffsetX, zoom * playerOffsetY, zoom * player.getRunFrame().getWidth() * player.getScale(), zoom * player.getRunFrame().getHeight() * player.getScale());
        } else {
            game.drawImage(player.getIdleFrame(), zoom * playerOffsetX, zoom * playerOffsetY, zoom * player.getIdleFrame().getWidth() * player.getScale(), zoom * player.getIdleFrame().getHeight() * player.getScale());
        }

        if (showHitboxes) {
            game.changeColor(Color.magenta);
            game.drawRectangle(player.testLeftX + centerOffsetX, player.testLeftY + centerOffsetY, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
            game.drawRectangle(player.testRightX + centerOffsetX, player.testRightY + centerOffsetY, Game.BLOCK_SIZE, Game.BLOCK_SIZE);

            game.changeColor(Color.red);
            game.drawRectangle(zoom * playerOffsetX, zoom * playerOffsetY, zoom * player.hitboxWidth, zoom * player.hitboxHeight);
            game.drawRectangle(point1.getX() + centerOffsetX, point1.getY() + centerOffsetY, point2.getX() + centerOffsetX, point2.getY() + centerOffsetY);
        }
    }

    public void renderEnemies() {
        for (Enemy enemy : game.activeLevel.getEnemies()) {
            if (enemy.getLocation().isBetween(point1, point2)) {
                double enemyOffsetX = enemy.getLocation().getX() + centerOffsetX;
                double enemyOffsetY = enemy.getLocation().getY() + centerOffsetY;

                game.drawImage(enemy.getIdleFrame(), enemyOffsetX, enemyOffsetY, zoom * enemy.getIdleFrame().getWidth() * enemy.getScale(), zoom * enemy.getIdleFrame().getWidth() * enemy.getScale());

                if (showHitboxes) {
                    game.changeColor(Color.cyan);
                    game.drawRectangle(zoom * enemyOffsetX, zoom * enemyOffsetY, zoom * enemy.getCollisionBox().getWidth(), zoom * enemy.getCollisionBox().getHeight());
                }
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
