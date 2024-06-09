package main;

import block.BlockTypes;

import javax.swing.*;

public class ComboBoxItem {

    private String type;
    private ImageIcon icon;

    public ComboBoxItem(String type, ImageIcon icon) {
        this.type = type;
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return type; // This will be used by the default renderer
    }
}