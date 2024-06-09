package level.editor;

import block.Block;
import level.Level;
import main.Camera;
import main.Game;
import main.Location;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class LevelItem {

    private Game game;
    private Level level;
    public double mouseOffsetX, mouseOffsetY;
    private Location loc;
    private Object selectedItem;

    public LevelItem(Level level) {
        this.level = level;
        this.game = level.getManager().getEngine();
    }

    public void render(Camera cam) {
        Block b = getSelectedBlock();
        mouseOffsetX = b.getLocation().getX() + cam.centerOffsetX;
        mouseOffsetY = b.getLocation().getY() + cam.centerOffsetY;

        game.changeColor(Color.GREEN);
        game.drawRectangle(mouseOffsetX, mouseOffsetY, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
        game.changeColor(Color.YELLOW);
    }

    public void mouseMoved(MouseEvent event) {
        game.mouseX = event.getX() - game.getCamera().centerOffsetX;
        game.mouseY = event.getY() - game.getCamera().centerOffsetY;
    }

    public void mousePressed(MouseEvent event) {
        game.mouseX = event.getX() - game.getCamera().centerOffsetX;
        game.mouseY = event.getY() - game.getCamera().centerOffsetY;
        editObject(event);
    }

    public void mouseReleased(MouseEvent event) {
        game.mouseX = event.getX() - game.getCamera().centerOffsetX;
        game.mouseY = event.getY() - game.getCamera().centerOffsetY;
        //editObject(event);
    }

    public void mouseDragged(MouseEvent event) {
        game.mouseX = event.getX() - game.getCamera().centerOffsetX;
        game.mouseY = event.getY() - game.getCamera().centerOffsetY;
        editObject(event);
    }

    public int getTileX() {
        return (int) game.mouseX / Game.BLOCK_SIZE;
    }

    public int getTileY() {
        return (int) game.mouseY / Game.BLOCK_SIZE;
    }

    public abstract void editObject(MouseEvent event);

    public void place() {

    }

    public void remove() {

    }

    public Block getSelectedBlock() {
        return level.getBlockGrid().getBlockAt(getTileX(), getTileY());
    }

    public void setSelectedItem(Object item) {
        this.selectedItem = item;
    }

    public Object getSelectedItem() {
        return selectedItem;
    }

    public Game getEngine() {
        return game;
    }

    public Level getLevel() {
        return level;
    }
}
