package ui;

import java.awt.*;

public class CustomCardLayout extends CardLayout {

    private String lastMenu;
    private String currentMenu;

    @Override
    public void show(Container parent, String name) {
        super.show(parent, name);
        setLastMenu(currentMenu);
        setCurrentMenu(name);
    }

    public String getLastMenu() {
        return lastMenu;
    }

    public void setLastMenu(String name) {
        if (name == null) {
            return;
        }

        this.lastMenu = name;
    }

    public String getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(String name) {
        if (name == null) {
            return;
        }

        this.currentMenu = name;
    }
}
