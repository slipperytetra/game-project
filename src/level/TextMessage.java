package level;

import utils.Location;

import java.awt.*;

public class TextMessage {

    private Location loc;
    private String text;
    private int fontSize;
    private boolean isBold;
    private boolean isStatic;
    private Color color;

    public TextMessage(Location loc, String text, int fontSize) {
        this.loc = loc;
        this.text = text;
        this.fontSize = fontSize;
        this.color = Color.WHITE;
    }

    public TextMessage(Location loc, String text, int fontSize, boolean isBold) {
        this.loc = loc;
        this.text = text;
        this.fontSize = fontSize;
        this.color = Color.WHITE;
    }

    public TextMessage(Location loc, String text, int fontSize, boolean isBold, Color color) {
        this.loc = loc;
        this.text = text;
        this.fontSize = fontSize;
        this.isBold = isBold;
        this.color = color;
    }

    public Location getLocation() {
        return loc;
    }

    public String getText() {
        return text;
    }

    public int getFontSize() {
        return fontSize;
    }

    public boolean isBold() {
        return isBold;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public Color getColor() {
        return color;
    }
}
