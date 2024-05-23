package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class GameMenu {

    private JFrame frame;

    public GameMenu() {
        frame = new JFrame("Game Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(3, 1));
        frame.setVisible(true);

        System.out.println("Add button");
        JButton startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(e -> {
            // Get the reference to the frame containing the button
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(startGameButton);
            if (parentFrame != null) {
                parentFrame.dispose();
            }
            new Game().startGame();
        });


        JButton infoButton = new JButton("Game Info");
        infoButton.addActionListener(e -> showInfo());

        JButton quitButton = new JButton("Quit Game");
        quitButton.addActionListener(e -> System.exit(0));

        frame.add(startGameButton);
        frame.add(infoButton);
        frame.add(quitButton);

        frame.setLocationRelativeTo(null);
    }

    private void showInfo(){
        if (Desktop.isDesktopSupported()){
            try {
                File readmeFile = new File("README.txt");
                if (readmeFile.exists()) {
                    Desktop.getDesktop().open(readmeFile);
                } else {
                    JOptionPane.showMessageDialog(frame, "README file not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Failed to open README file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Desktop class is not supported on this platform.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
