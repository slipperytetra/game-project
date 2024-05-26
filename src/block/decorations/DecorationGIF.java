package block.decorations;

import main.Game;
import main.Location;

import java.awt.*;

public class DecorationGIF extends Decoration {
    public DecorationGIF(DecorationTypes type, Location loc, double width, double height) {
        super(type, loc, width, height);
    }

    public Image getImage(Game game) {
        return game.getTexture(getType().toString());
    }
}
