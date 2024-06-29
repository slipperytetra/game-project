package level.editor;

import block.Block;
import block.BlockTypes;
import level.Level;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LevelJFileChooserLoad extends JPanel implements ActionListener {
    JFileChooser chooser;
    Level level;

    public LevelJFileChooserLoad(Level level) {
        chooser = new JFileChooser();
        this.level = level;
    }

    public void actionPerformed(ActionEvent e) {
        chooser.setCurrentDirectory(new File("resources/levels"));
        chooser.setDialogTitle("Choose level txt file to load from . .");
        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            level.getManager().getEngine().setActiveLevel(chooser.getSelectedFile().getPath(), false);
        }
    }

    public Dimension getPreferredSize(){
        return new Dimension(200, 200);
    }
}