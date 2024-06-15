package level.editor;

import block.Block;
import level.Level;
import main.Camera;
import main.Game;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class LevelEditor {

    private Game game;
    private Level level;
    public double mouseOffsetX, mouseOffsetY;
    private Object selectedItem;

    public LevelEditor(Level level) {
        this.level = level;
        this.game = level.getManager().getEngine();
    }

    public void render(Camera cam) {
        Block b = getSelectedBlock();
        mouseOffsetX = cam.toScreenX(b.getLocation().getX());
        mouseOffsetY = cam.toScreenY(b.getLocation().getY());

        game.changeColor(Color.GREEN);
        game.drawRectangle(mouseOffsetX, mouseOffsetY, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
        game.changeColor(Color.YELLOW);
    }

    public void mouseMoved(MouseEvent event) {
        game.mouseX = game.getCamera().toWorldX(event.getX());
        game.mouseY = game.getCamera().toWorldY(event.getY());
    }

    public void mousePressed(MouseEvent event) {
        game.mouseX = game.getCamera().toWorldX(event.getX());
        game.mouseY = game.getCamera().toWorldY(event.getY());
        editObject(event);
    }

    public void mouseReleased(MouseEvent event) {
        game.mouseX = game.getCamera().toWorldX(event.getX());
        game.mouseY = game.getCamera().toWorldY(event.getY());
        //editObject(event);
    }

    public void mouseDragged(MouseEvent event) {
        game.mouseX = game.getCamera().toWorldX(event.getX());
        game.mouseY = game.getCamera().toWorldY(event.getY());
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
