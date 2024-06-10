package main;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameMenuNew extends GameEngine {

    protected JPanel mainPanel;
    protected JPanel buttonsPanel;
    protected JPanel titlePanel;
    protected LevelLoadPanel dataPanel;
    protected Image backgroundImage;
    protected AudioClip menuMusic; // Clip for the menu music

    public void init() {
        this.setWindowSize(1280, 720);
        this.menuMusic = loadAudio("resources/sounds/menuMusic.wav");

        try {
            this.backgroundImage = ImageIO.read(new File("resources/images/backgrounds/title_background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setOpaque(false);

        loadTitlePanel();
        loadButtonsPanel();
        dataPanel = new LevelLoadPanel(this);
        dataPanel.setVisible(false);
        mainPanel.add(dataPanel);
        this.mPanel.add(mainPanel);

        if (menuMusic != null) {
            startAudioLoop(menuMusic);
        }
    }

    public void loadTitlePanel() {
        titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        JLabel label = new JLabel("<untitled game>");
        titlePanel.add(label);

        mainPanel.add(titlePanel);
    }

    public void loadButtonsPanel() {
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(5, 1));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(new JButton("New Game"));

        JButton buttonLoad = new JButton("Load Game");
        buttonLoad.addActionListener(e -> {
            titlePanel.setVisible(false);
            buttonsPanel.setVisible(false);
            dataPanel.setVisible(true);
        });

        buttonsPanel.add(buttonLoad);
        buttonsPanel.add(new JButton("Level Editor"));
        buttonsPanel.add(new JButton("Settings"));
        buttonsPanel.add(new JButton("Quit"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        mainPanel.add(buttonsPanel, gbc);
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void paintComponent() {
        if (backgroundImage != null) {
            drawImage(backgroundImage, 0, 0, width(), height());
        }
    }
}

class LevelLoadPanel extends JPanel {

    public LevelLoadPanel(GameMenuNew menu) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        JButton button = new JButton("Load Game");
        button.addActionListener(e -> {
            menu.stopAudioLoop(menu.menuMusic);
            // Get the reference to the frame containing the button
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(button);
            if (parentFrame != null) {
                parentFrame.dispose();
            }
            new Game().startGame();
        });
        add(button);
        add(new JButton("222"));
        add(new JButton("333"));

        JButton buttonBack = new JButton("Back");
        buttonBack.addActionListener(e -> {
            menu.titlePanel.setVisible(true);
            menu.buttonsPanel.setVisible(true);
            menu.dataPanel.setVisible(false);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(buttonBack, gbc);
    }

}