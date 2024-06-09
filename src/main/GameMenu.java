package main;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GameMenu {
    private JFrame frame;
    private JLabel backgroundLabel;
    private Clip menuMusic; // Clip for the menu music

    public GameMenu() {
        frame = new JFrame("Game Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);

        JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setOpaque(false);

        //TextureAnimated bgTextture = new TextureAnimated();
        ImageIcon backgroundIcon = new ImageIcon("resources/images/bgidea.gif");
        backgroundLabel = new JLabel(backgroundIcon);
        frame.setContentPane(backgroundLabel);
        backgroundLabel.setLayout(new GridBagLayout());

        JButton startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(e -> {
            stopMenuMusic();
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

        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 0;
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
        Insets insets = buttonConstraints.insets;
        new Insets(10, 10, 10, 10);

        backgroundLabel.add(titleLabel, buttonConstraints);
        backgroundLabel.add(startGameButton, buttonConstraints);
        backgroundLabel.add(infoButton, buttonConstraints);
        backgroundLabel.add(quitButton, buttonConstraints);

        // Load menu music
        try {
            File menuMusicFile = new File("resources/sounds/menuMusic.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(menuMusicFile);
            menuMusic = AudioSystem.getClip();
            menuMusic.open(audioIn);
            menuMusic.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void stopMenuMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.close();
        }
    }



    private void showInfo(){
        if (Desktop.isDesktopSupported()){
            try {
                File readmeFile = new File("src/gameInfo.txt");
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
