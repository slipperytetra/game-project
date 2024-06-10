package level.editor;

import block.Block;
import block.BlockSet;
import block.BlockTypes;
import block.decorations.DecorationTypes;
import level.Level;
import main.Camera;
import main.Game;
import main.Texture;

import java.awt.event.MouseEvent;

public class LevelEditorBlocks extends LevelItem {
    public LevelEditorBlocks(Level level) {
        super(level);
    }

    public void render(Camera cam) {
        super.render(cam);

        Block block = getSelectedBlock();
        cam.game.drawText(cam.game.width() - 150, 30, "Block: " + block.getType().toString(), 15);
        cam.game.drawText(cam.game.width() - 150, 45, "Pos: (" + getTileX() + ", " + getTileY() + ")", 15);

        if (getSelectedItem() instanceof BlockTypes type) {
            Texture texture = cam.game.getTexture(type.toString());
            if (type.getBlockSetAmount() > 0) {
                texture = cam.game.getTexture(type.toString() + "_0");
            }

            if (texture != null) {
                cam.game.drawImage(texture.getImage(), mouseOffsetX, mouseOffsetY,
                        32, 32, 0.65f);
            }
        }
    }

    @Override
    public void editObject(MouseEvent event) {
        if (getSelectedItem() instanceof BlockTypes) {
            BlockTypes type = (BlockTypes) getSelectedItem();
            if (event.getButton() == MouseEvent.BUTTON1) {
                placeBlock(type);
            } else if (event.getButton() == MouseEvent.BUTTON3) {
                removeBlock();
            }
            getLevel().getBlockGrid().applySets();
            getLevel().getBlockGrid().applySets();
        }
    }

    private void placeBlock(BlockTypes type) {
        if (getSelectedBlock() == null || getSelectedBlock() == null || getSelectedBlock().getType() == type) {
            return;
        }

        if (getLevel()!= null && getLevel().isEditMode()) {
            if (type.toString().contains("FOREST_GROUND")) {
                BlockSet b = new BlockSet(getLevel(), getLevel().getBlockGrid().getBlockAt(getTileX(), getTileY()).getLocation(), type);
                getLevel().getBlockGrid().setBlock(getTileX(), getTileY(), b);
            } else {
                Block b = new Block(getLevel(), getLevel().getBlockGrid().getBlockAt(getTileX(), getTileY()).getLocation(), type);
                getLevel().getBlockGrid().setBlock(getTileX(), getTileY(), b);
            }
        }
    }

    private void removeBlock() {
        if (getSelectedBlock().getType() == BlockTypes.VOID) {
            return;
        }

        if (getLevel() != null && getLevel().isEditMode()) {
            //System.out.println("Pos: " + game.mouseX + ", " + game.mouseY);
            //System.out.println("Tile: " + getTileX() + ", " + getTileY());
            //System.out.println(game.getActiveLevel().getBlockGrid().getBlockAt(getTileX(), getTileY()).getType());
            Block b = new Block(getLevel(), getLevel().getBlockGrid().getBlockAt(getTileX(), getTileY()).getLocation(), BlockTypes.VOID);
            getLevel().getBlockGrid().setBlock(getTileX(), getTileY(), b);
        }
    }
}
