package level.editor;

import block.Block;
import block.BlockSet;
import block.BlockTypes;
import block.decorations.DecorationTypes;
import entity.EntityType;
import level.Level;
import main.Camera;
import main.Game;

import java.awt.*;
import java.awt.event.MouseEvent;

public class LevelEditor {

    private Level level;
    private Game game;
    private BlockTypes blockType;
    private DecorationTypes decoType;
    private EntityType entityType;

    public LevelEditor(Level level) {
        this.level = level;
        this.game = level.getManager().getEngine();
    }

    public void render(Camera cam) {
        Block block = getSelectedBlock();
        game.changeColor(Color.YELLOW);
        game.drawText(game.width() - 150, 30, "Block: " + block.getType().toString(), 15);
        game.drawText(game.width() - 150, 45, "Pos: (" + getTileX() + ", " + getTileY() + ")", 15);

        double mouseOffsetX = block.getLocation().getX() + cam.centerOffsetX;
        double mouseOffsetY = block.getLocation().getY() + cam.centerOffsetY;

        game.changeColor(Color.GREEN);
        game.drawRectangle(mouseOffsetX, mouseOffsetY, Game.BLOCK_SIZE, Game.BLOCK_SIZE);
    }

    public void mouseMoved(MouseEvent event) {
        game.mouseX = event.getX() - game.getCamera().centerOffsetX;
        game.mouseY = event.getY() - game.getCamera().centerOffsetY;
    }

    public void mousePressed(MouseEvent event) {
        game.mouseX = event.getX() - game.getCamera().centerOffsetX;
        game.mouseY = event.getY() - game.getCamera().centerOffsetY;
        editBlock(event);
    }

    public void mouseReleased(MouseEvent event) {
        game.mouseX = event.getX() - game.getCamera().centerOffsetX;
        game.mouseY = event.getY() - game.getCamera().centerOffsetY;
        editBlock(event);
    }

    public void mouseDragged(MouseEvent event) {
        game.mouseX = event.getX() - game.getCamera().centerOffsetX;
        game.mouseY = event.getY() - game.getCamera().centerOffsetY;
        editBlock(event);
    }

    private int getTileX() {
        return (int) game.mouseX / Game.BLOCK_SIZE;
    }

    private int getTileY() {
        return (int) game.mouseY / Game.BLOCK_SIZE;
    }

    private Block getSelectedBlock() {
        return level.getBlockGrid().getBlockAt(getTileX(), getTileY());
    }

    private void placeBlock() {
        if (getSelectedBlock() == null || getSelectedBlock() == null || getSelectedBlock().getType() == getSelectedBlockType()) {
            return;
        }

        if (game.getActiveLevel() != null && game.getActiveLevel().isEditMode()) {
            if (getSelectedBlockType().toString().contains("FOREST_GROUND")) {
                BlockSet b = new BlockSet(level, game.getActiveLevel().getBlockGrid().getBlockAt(getTileX(), getTileY()).getLocation(), getSelectedBlockType());
                game.getActiveLevel().getBlockGrid().setBlock(getTileX(), getTileY(), b);
                game.getActiveLevel().getBlockGrid().applySets();
                game.getActiveLevel().getBlockGrid().applySets();
            } else {
                Block b = new Block(level, game.getActiveLevel().getBlockGrid().getBlockAt(getTileX(), getTileY()).getLocation(), getSelectedBlockType());
                game.getActiveLevel().getBlockGrid().setBlock(getTileX(), getTileY(), b);
            }
        }
    }

    private void removeBlock() {
        if (getSelectedBlock().getType() == BlockTypes.VOID) {
            return;
        }

        if (game.getActiveLevel() != null && game.getActiveLevel().isEditMode()) {
            //System.out.println("Pos: " + game.mouseX + ", " + game.mouseY);
            //System.out.println("Tile: " + getTileX() + ", " + getTileY());
            //System.out.println(game.getActiveLevel().getBlockGrid().getBlockAt(getTileX(), getTileY()).getType());
            Block b = new Block(level, game.getActiveLevel().getBlockGrid().getBlockAt(getTileX(), getTileY()).getLocation(), BlockTypes.VOID);
            game.getActiveLevel().getBlockGrid().setBlock(getTileX(), getTileY(), b);
            game.getActiveLevel().getBlockGrid().applySets();
            game.getActiveLevel().getBlockGrid().applySets();
        }
    }

    public void editBlock(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1) {
            placeBlock();
        } else if (event.getButton() == MouseEvent.BUTTON3) {
            removeBlock();
        }
    }

    public BlockTypes getSelectedBlockType() {
        return blockType;
    }

    public void setSelectedBlock(BlockTypes type) {
        this.blockType = type;
    }

    public DecorationTypes getSelectedDecoType() {
        return decoType;
    }

    public void setSelectedDecoType(DecorationTypes type) {
        this.decoType = type;
    }

    public EntityType getSelectedEntityType() {
        return entityType;
    }

    public void setSelectedEntityType(EntityType type) {
        this.entityType = type;
    }
}
