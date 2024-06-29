package main;

import entity.Player;
import level.Level;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SaveGameListener implements WindowListener {

    private Level level;

    public SaveGameListener(Level level) {
        this.level = level;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        ArrayList<String> lines = new ArrayList<>();
        File file = new File("saves/player_save.txt");
        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        Player p = level.getPlayer();
        lines.add("coins: " + p.getCoins());

        try {
            FileWriter myWriter = new FileWriter(file.getPath());
            for (String line : lines) {
                myWriter.write(line + "\n");
            }
            myWriter.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
