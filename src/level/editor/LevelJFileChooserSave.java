package level.editor;

import block.Block;
import block.BlockTypes;
import block.decorations.Decoration;
import main.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class LevelJFileChooserSave extends JPanel implements ActionListener {
    JFileChooser chooser;
    Game game;

    ArrayList<String> lines;

    public LevelJFileChooserSave(Game game) {
        chooser = new JFileChooser();
        this.game = game;
        this.lines = new ArrayList<>();
    }

    public void actionPerformed(ActionEvent e) {
        chooser.setCurrentDirectory(new java.io.File("./resources/levels"));
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("Enter file name and select location . .");
        // null should be the name of the parent JFrame
        int returnVal = chooser.showSaveDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            saveLevelToFile(path);
        }
    }

    public void saveLevelToFile(String path) {
        lines.clear();
        File file = new File(path);
        // redundancy check, feel free to remove
        if(!file.getPath().isEmpty()) {
            // create parent directories if they don't exist
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                System.out.println("File created");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to create file");
            }
        }

        lines.add("name: " + file.getName().replaceAll(".txt", "").toUpperCase());
        lines.add("background: " + game.getActiveLevel().getBackgroundImgFilePath());
        lines.add("midground: " + game.getActiveLevel().getMidgroundImgFilePath());
        lines.add("foreground: " + game.getActiveLevel().getForegroundImgFilePath());
        lines.add("background_music: " + game.getActiveLevel().getBackgroundmusicFilePath());
        lines.add("overlay: " + game.getActiveLevel().getOverlay());
        lines.add("next_level: " + game.getActiveLevel().getNextLevel());
        lines.add("level_data:");

        for (int y = 0; y < game.getActiveLevel().getBlockGrid().getHeight(); y++) {
            StringBuilder line = new StringBuilder("  ");
            for (int x = 0; x < game.getActiveLevel().getBlockGrid().getWidth(); x++) {
                String charKey = ".";
                Block block = game.getActiveLevel().getBlockGrid().getBlocks()[x][y];
                if (game.getActiveLevel().blockKeyMap.containsValue(block.getType())) {
                    for (char key : game.getActiveLevel().blockKeyMap.keySet()) {
                        if (game.getActiveLevel().blockKeyMap.get(key) == block.getType()) {
                            charKey = key + "";
                        }
                    }
                }

                /*if (x == game.getActiveLevel().getSpawnPoint().getTileX() && y == game.getActiveLevel().getSpawnPoint().getTileY()) {
                    charKey = "P";
                }*/

                line.append(charKey);
            }
            lines.add(line.toString());
        }

        for (Decoration deco : game.getActiveLevel().getDecorations()) {
            int tileX = deco.getLocation().getTileX();
            int tileY = deco.getLocation().getTileY() + 1;

            StringBuilder strBuilder = new StringBuilder(lines.get(7 + tileY));
            if (game.getActiveLevel().decorationKeyMap.containsValue(deco.getType())) {
                for (char key : game.getActiveLevel().decorationKeyMap.keySet()) {
                    if (game.getActiveLevel().decorationKeyMap.get(key) == deco.getType()) {
                        strBuilder.setCharAt(tileX + 2, key);
                        break;
                    }
                }
            }
            lines.set(7 + tileY, strBuilder.toString());
        }

        lines.add("keymap:");
        for (char key : game.getActiveLevel().blockKeyMap.keySet()) {
            lines.add("  " + key + ":" + game.getActiveLevel().blockKeyMap.get(key).toString());
        }
        for (char key : game.getActiveLevel().decorationKeyMap.keySet()) {
            lines.add("  " + key + ":" + game.getActiveLevel().decorationKeyMap.get(key).toString());
        }
        for (char key : game.getActiveLevel().entityKeyMap.keySet()) {
            lines.add("  " + key + ":" + game.getActiveLevel().entityKeyMap.get(key).toString());
        }


        try {
            FileWriter myWriter = new FileWriter(file.getPath());
            for (String line : lines) {
                myWriter.write(line + "\n");
            }
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Dimension getPreferredSize(){
        return new Dimension(200, 200);
    }
}