package ui;

import javax.swing.*;
import java.awt.*;

public class GameMenuTemp extends JPanel {

    protected CustomCardLayout cl;

    public GameMenuTemp() {
        this.cl = new CustomCardLayout();
        setLayout(cl);
    }
}

class BackButton extends JButton {

    public BackButton(GameMenuTemp menu) {
        super("Back");

        addActionListener(e -> menu.cl.show(menu, menu.cl.getLastMenu()));
    }

}
