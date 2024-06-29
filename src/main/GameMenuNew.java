package main;

import utils.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class GameMenuNew extends GameEngine {

    public CardLayout cl;
    protected JPanel mainPanel;
    protected JPanel buttonsPanel;
    protected JPanel titlePanel;
    public JPanel contextPanel;
    private JPanel levelEditorPanel;
    protected LevelLoadPanel dataPanel;
    protected Image backgroundImage;
    //protected AudioClip menuMusic; // Clip for the menu music

    public ArrayList<BufferedImage> icons = new ArrayList<>();

    public void init() {
        this.setWindowSize(1280, 720);
        //this.menuMusic = loadAudio("resources/sounds/menuMusic.wav");

        try {
            this.backgroundImage = ImageIO.read(new File("resources/images/backgrounds/title_background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setOpaque(false);

        cl = new CardLayout();
        contextPanel = new JPanel(cl);
        contextPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        mainPanel.add(contextPanel, gbc);

        loadTitlePanel();
        loadButtonsPanel();

        SettingsMenu sMenu = new SettingsMenu(this);
        sMenu.showContainer("Settings");
        contextPanel.add(sMenu, "Settings");

        dataPanel = new LevelLoadPanel(this);
        dataPanel.setVisible(false);
        contextPanel.add(dataPanel, "Load");

        this.mPanel.add(mainPanel);

        levelEditorPanel = new LevelEditorPanel(this);
        contextPanel.add(levelEditorPanel, "Editor");

        /*if (menuMusic != null) {
            startAudioLoop(menuMusic);
        }*/
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
        buttonLoad.addActionListener(e -> cl.show(contextPanel, "Load"));
        buttonsPanel.add(buttonLoad);

        JButton buttonEditor = new JButton("Level Editor");
        buttonEditor.addActionListener(e -> cl.show(contextPanel, "Editor"));
        buttonsPanel.add(buttonEditor);

        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> cl.show(contextPanel, "Settings"));

        buttonsPanel.add(settingsButton);
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showOptionDialog(buttonsPanel, "Do you want to exit the game?",
                        "Exit game?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, new String[]{"No", "Yes"}, "No");

                if (result == 1) {
                    System.exit(0);
                }
            }
        });
        buttonsPanel.add(quitButton);

        contextPanel.add(buttonsPanel, "Home");
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void paintComponent() {
        if (backgroundImage != null) {
            drawImage(backgroundImage, 0, 0, width(), height());
        }

        if (levelEditorPanel.isVisible()) {
            for (int i = 0; i < icons.size(); i++) {
                BufferedImage img = icons.get(i);
                int y = i / 3;
                drawImage(img, 100 + (i * img.getWidth()) + (i * 50), y * img.getHeight() + 175, img.getWidth(), img.getHeight());
            }
        }
    }
}

class LevelLoadPanel extends JPanel {

    public LevelLoadPanel(GameMenuNew menu) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        JButton button = new JButton("Load Game");
        button.addActionListener(e -> {
            //menu.stopAudioLoop(menu.menuMusic);
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;

        add(new BackButton(menu.cl, menu.contextPanel, "Home"), gbc);
    }
}

class SettingsMenu extends JPanel {

    protected CardLayout cl;

    private JPanel buttonsPanel;
    private JPanel volumePanel;
    private JPanel graphicsPanel;

    public SettingsMenu(GameMenuNew menu) {
        cl = new CardLayout();
        setLayout(cl);
        setOpaque(false);

        JButton volumeButton = new JButton("Volume");
        JButton graphicsButton = new JButton("Graphics");
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> menu.cl.show(menu.contextPanel, "Home"));

        buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(volumeButton);
        buttonsPanel.add(graphicsButton);
        buttonsPanel.add(backButton);
        add(buttonsPanel, "Settings");

        volumePanel = new JPanel();
        volumePanel.setLayout(new GridBagLayout());
        volumePanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        volumePanel.add(new JLabel("Music"));
        volumePanel.add(new JSlider(JSlider.HORIZONTAL, 0, 100, 100));
        gbc.gridy = 1;
        volumePanel.add(new JLabel("Effects"), gbc);
        volumePanel.add(new JSlider(JSlider.HORIZONTAL, 0, 100, 100), gbc);
        gbc.gridy = 2;
        volumePanel.add(new BackButton(cl, this, "Settings"), gbc);
        add(volumePanel, "Volume");

        graphicsPanel = new JPanel();
        graphicsPanel.setOpaque(false);
        graphicsPanel.add(new JButton("Enable RTX"));
        graphicsPanel.add(new BackButton(cl, this, "Settings"));
        add(graphicsPanel, "Graphics");


        volumeButton.addActionListener(e -> cl.show(this, "Volume"));
        graphicsButton.addActionListener(e -> cl.show(this, "Graphics"));
    }

    public void showContainer(String name) {
        cl.show(this, name);
    }
}

class LevelEditorPanel extends JPanel {

    private GameMenuNew menu;
    private JButton refresh;
    private JLabel dirLabel;
    private JTextField dirTextField;

    private JPanel optionsPanel;
    private JPanel levelsPanel;


    public LevelEditorPanel(GameMenuNew menu) {
        this.menu = menu;
        setLayout(new GridBagLayout());
        setOpaque(false);

        levelsPanel = new JPanel();
        levelsPanel.setOpaque(false);
        levelsPanel.setLayout(new GridBagLayout());
        refresh = new JButton("Refresh");
        dirLabel = new JLabel("Levels Location:");
        dirTextField = new JTextField("saves/levels");
        dirTextField.setForeground(Color.GRAY);

        optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridBagLayout());
        optionsPanel.setOpaque(false);
        optionsPanel.add(dirLabel);
        optionsPanel.add(dirTextField);
        optionsPanel.add(refresh);
        optionsPanel.add(new BackButton(menu.cl, menu.contextPanel, "Home"));

        GridBagConstraints gbc = new GridBagConstraints();
        add(optionsPanel);
        gbc.gridy = 1;
        add(levelsPanel, gbc);

        loadLevels();
    }

    public void loadLevels() {
        File folder = new File("saves/levels");
        File[] listOfFiles = folder.listFiles();
        BufferedImage defaultImg = (BufferedImage) menu.loadImage("saves/levels/default_icon.png");
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.getName().contains(".png") || file.getName().startsWith(".")) {
                    continue;
                }

                BufferedImage levelImg = defaultImg;
                File levelImgFile = new File(folder.getPath() + "/" + file.getName().replaceAll(".txt", "") + "_icon.png");
                if (levelImgFile.exists()) {
                    levelImg = (BufferedImage) menu.loadImage(levelImgFile.getPath());
                } else {
                    System.out.println(levelImgFile.getPath());
                }

                JButton button = new JButton();
                button.setOpaque(false);
                button.setIcon(new ImageIcon(levelImg));
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //menu.stopAudioLoop(menu.menuMusic);
                        // Get the reference to the frame containing the button
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(button);
                        if (parentFrame != null) {
                            parentFrame.dispose();
                        }

                        Game game = new Game();
                        game.startGame();

                        game.setActiveLevel(folder.getPath() + "/" + file.getName(), true);
                        game.camera.setFocusPoint(new Location(game.getActiveLevel().getActualWidth() / 2, game.getActiveLevel().getActualHeight() / 2));
                        game.getActiveLevel().setEditMode(true);
                    }
                });
                levelsPanel.add(button);
                //menu.icons.add(GameUtils.makeRoundedCorner(levelImg, 30));
            }
        }
    }
}


class BackButton extends JButton {
    public BackButton(CardLayout cl, JPanel parent, String menuName) {
        super("Back");

        addActionListener(e -> cl.show(parent, menuName));
    }
}