package level.editor;

import block.Block;
import block.decorations.Decoration;
import block.decorations.DecorationTypes;
import level.Level;
import main.Camera;
import main.Game;
import main.Texture;

import java.awt.event.MouseEvent;

public class LevelEditorDecorations extends LevelItem {

    private Decoration selectedDecoration;

    public LevelEditorDecorations(Level level) {
        super(level);
    }

    public void render(Camera cam) {
        super.render(cam);
        
        Block block = getSelectedBlock();
        cam.game.drawText(cam.game.width() - 150, 30, "Decoration: " + block.getType().toString(), 15);
        cam.game.drawText(cam.game.width() - 150, 45, "Pos: (" + getTileX() + ", " + getTileY() + ")", 15);
        if (getSelectedItem() instanceof DecorationTypes decoType) {
            Texture texture = cam.game.getTexture(decoType.toString().toLowerCase());
            if (texture != null) {
                double height = texture.getHeight() * decoType.getScale();
                cam.game.drawImage(texture.getImage(), mouseOffsetX, mouseOffsetY - height + Game.BLOCK_SIZE,
                        texture.getWidth() * decoType.getScale(), height, 0.65f);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        super.mousePressed(event);
        findDecoration();
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        super.mouseMoved(event);
        findDecoration();
    }

    @Override
    public void mouseDragged(MouseEvent event) {
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        super.mouseReleased(event);
        findDecoration();
    }

    @Override
    public void editObject(MouseEvent event) {
        if (getSelectedItem() instanceof DecorationTypes type) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                placeDeco(type);
            } else if (event.getButton() == MouseEvent.BUTTON3) {
                removeDeco();
            }
        }
    }

    private void placeDeco(DecorationTypes type) {
        //System.out.println("Place deco:");
        findDecoration();
        if (getLevel() == null) {
            System.out.println("level null ");
        }

        //System.out.println(getLevel().isEditMode);
        if (getLevel() != null && getLevel().isEditMode()) {
            if (getSelectedDecoration() != null) {
                getSelectedDecoration().setActive(false);
            }


            //System.out.println(getLevel().getBlockGrid().getBlockAt(getTileX(), getTileY()).getType().toString());
            getLevel().addDecoration(type, getLevel().getBlockGrid().getBlockAt(getTileX(), getTileY()).getLocation().clone());
        }
    }

    public void removeDeco() {
        if (getSelectedDecoration() != null) {
            getSelectedDecoration().setActive(false);
        }
    }

    public Decoration getSelectedDecoration() {
        return selectedDecoration;
    }

    public void findDecoration() {
        for (Decoration deco : getLevel().getDecorations()) {
            if (deco.isOnScreen(getEngine().getCamera())) {
                //System.out.println("Checking if " + deco.getType().toString());
                //System.out.println(deco.getLocation().toString());
                //System.out.println("matches");
                //System.out.println(getSelectedBlock().getLocation().toString());
                if (matches(deco)) {
                    //System.out.println("It does!");
                    selectedDecoration = deco;
                    return;
                }
            }
        }

        selectedDecoration = null;
    }

    public boolean matches(Decoration deco) {
        return deco.getLocation().getTileX() == getTileX() && deco.getLocation().getTileY() == getTileY();
    }
}
