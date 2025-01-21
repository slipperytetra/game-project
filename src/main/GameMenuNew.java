package main;

import level.Level;
import level.LevelManager;
import utils.Location;
import utils.WaveEffect;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import static com.sun.java.accessibility.util.AWTEventMonitor.addActionListener;


public class GameMenuNew extends GameEngine {

    public CardLayout cl;
    private Clip menuMusic; // Clip for the menu music

    protected JPanel mainPanel;
    protected JPanel buttonsPanel;
    protected JPanel titlePanel;
    public SettingsMenu sMenu;
    public JPanel contextPanel;
    private JPanel levelEditorPanel;
    private JPanel selectLevel;
    protected LevelLoadPanel dataPanel;
    protected Image backgroundImage;
    protected BufferedImage backgroundTree;
    BufferedImage distortedImage;
    private JPanel titleSequencePanel; // Panel for title sequence
    private Timer titleTimer;
    private JLabel titleTextLabel;
    private JLabel est;

    private float opacity = 0f;

    WaveEffect waveEffect;
    double treeWaveTime;


    //protected AudioClip menuMusic;
    // Clip for the menu music


    public ArrayList<BufferedImage> icons = new ArrayList<>();

    @Override
    public void keyPressed(KeyEvent event) {


        if (event.getKeyCode() == 27) {
            cl.show(this.contextPanel, "Home");


        }}




    public void init() {
        this.setWindowSize(1280, 720);
        waveEffect = new WaveEffect(2, 20, 0, 1, 40, Math.PI / 4, 0.5, 1);

        try {
            this.backgroundImage = ImageIO.read(new File("resources/images/backgrounds/title_background.png"));
            this.backgroundTree = ImageIO.read(new File("resources/images/backgrounds/title_background_tree.png"));
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
        loadTitleSequencePanel();
        cl.show(contextPanel, "TitleSequence");

        //loadTitlePanel();
        loadButtonsPanel();
        SelectMenu selectMenu = new SelectMenu(this);
        JButton selectLevelButton = new JButton("Select Level");
        selectLevelButton.addActionListener(e -> cl.show(contextPanel, "Select"));
        buttonsPanel.add(selectLevelButton);

        // Add SelectMenu to contextPanel
        contextPanel.add(selectMenu, "Select");


        sMenu = new SettingsMenu(this);
        sMenu.showContainer("Settings");
        contextPanel.add(sMenu, "Settings");



        dataPanel = new LevelLoadPanel(this);
        dataPanel.setVisible(false);
        contextPanel.add(dataPanel, "Load");

      //  selectLevel = new SelectMenu(this);
      //  selectLevel.setVisible(false);
       // contextPanel.add(selectLevel, "Selector");

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

        JLabel label = new JLabel("<G game>");
        titlePanel.add(label);

        mainPanel.add(titlePanel);
    }

    private void loadTitleSequencePanel() {
        // Create the title sequence panel
        titleSequencePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw a black background
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Draw a background image (optional)
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        titleSequencePanel.setLayout(null); // Absolute positioning for flexibility
        titleSequencePanel.setOpaque(true);

        // Set panel size to 1280 x 720
        titleSequencePanel.setPreferredSize(new Dimension(1280, 720));
        titleSequencePanel.setMinimumSize(new Dimension(1280, 720));
        titleSequencePanel.setMaximumSize(new Dimension(1280, 720));

        // Add image as a JLabel
        JLabel imageLabel = new JLabel();
        try {

            BufferedImage logoImage = ImageIO.read(new File("resources/images/snowB.png")); // Replace with your image path
            imageLabel.setIcon(new ImageIcon(logoImage));

            // Dynamically center the logo
            int logoWidth = logoImage.getWidth();
            int logoHeight = logoImage.getHeight();
            int logoX = (1280 - logoWidth) / 2;
            int logoY = (720 - logoHeight) / 2 - 50; // Adjust Y for spacing between logo and text
            imageLabel.setBounds(logoX, logoY, logoWidth, logoHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
        titleSequencePanel.add(imageLabel);

        // Add text label for the title
        titleTextLabel = new JLabel("Retard Studios", SwingConstants.CENTER);
        est = new JLabel("© 1969", SwingConstants.CENTER);

        titleTextLabel.setFont(new Font("Arial", Font.BOLD, 64));
        titleTextLabel.setForeground(Color.WHITE); // Set text color to white
        titleTextLabel.setBounds(0, 360, 1280, 100); // Center text horizontally
        titleSequencePanel.add(titleTextLabel);
        est.setFont(new Font("Arial", Font.BOLD, 64));
        est.setForeground(Color.WHITE); // Set text color to white
        est.setBounds(0, 560, 1280, 100); // Center text horizontally
        titleSequencePanel.add(est);

        // Add the title sequence panel to the contextPanel
        contextPanel.add(titleSequencePanel, "TitleSequence");

        // Start the animation
        startTitleSequence();
        playAudioOnce();
    }



    private void startTitleSequence() {
        titleTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Gradually increase the opacity
                opacity += 0.02f;
                if (opacity > 1f) {
                    opacity = 1f;
                    ((Timer) e.getSource()).stop(); // Stop the timer
                    // Show the main menu after a short delay
                    Timer delayTimer = new Timer(3000, event -> cl.show(contextPanel, "Home"));
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                }
                titleTextLabel.setForeground(new Color(255, 255, 255, (int) (opacity * 255)));
                titleTextLabel.repaint();
            }
        });
        titleTimer.start();
    }
    private void playAudioOnce() {
        try {
            // Load the audio file
            File menuMusicFile = new File("resources/sounds/intro.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(menuMusicFile);
            menuMusic = AudioSystem.getClip();
            menuMusic.open(audioIn);

            // Play the audio once
            menuMusic.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (backgroundTree != null && this.sMenu.shaders.isSelected()) {
            int width = backgroundTree.getWidth();
            int height = backgroundTree.getHeight();
            distortedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double offset = waveEffect.getOffset(x, (double)y / height, treeWaveTime);  // Pass y as a fraction
                    int newY = (int) Math.round(y + offset);
                    if (newY >= 0 && newY < height) {
                        distortedImage.setRGB(x, y, backgroundTree.getRGB(x, newY));
                    }
                }
            }

            treeWaveTime += 1 * dt;
        }
    }

    @Override
    public void paintComponent() {
        if (backgroundImage != null) {
            drawImage(backgroundImage, 0, 0, width(), height());
        }

        if (distortedImage != null && sMenu.shaders.isSelected()) {
            //mGraphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            //mGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawImage(distortedImage, 0, 0, width(), height());
        } else {
            drawImage(backgroundTree, 0, 0, width(), height());
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

 class SelectMenu extends JPanel {
    private GameMenuNew menu;
    private JPanel levelsPanel;
    private JComboBox<String> levelsComboBox;

    public SelectMenu(GameMenuNew menu) {
        this.menu = menu;
        setLayout(new GridBagLayout());
        setOpaque(false);

        levelsPanel = new JPanel(new GridBagLayout());
        levelsPanel.setOpaque(false);

        // Add levelsPanel to this SelectMenu panel
        GridBagConstraints gbcLevels = new GridBagConstraints();
        gbcLevels.gridy = 1;
        add(levelsPanel, gbcLevels);

        // Initialize JComboBox for levels selection (if needed)
        levelsComboBox = new JComboBox<>();
        levelsComboBox.setPreferredSize(new Dimension(200, 30));
        GridBagConstraints gbcComboBox = new GridBagConstraints();
        gbcComboBox.gridy = 0;

        add(levelsComboBox, gbcComboBox);

        loadLevels();

    }

     public void loadLevels() {
         levelsPanel.removeAll();

         // Get list of files in "saves/levels" directory
         File folder = new File("saves/levels");
         File[] listOfFiles = folder.listFiles();

         BufferedImage defaultImg = loadImage("saves/levels/default_icon.png");

         if (listOfFiles != null) {
             for (File file : listOfFiles) {
                 if (!file.getName().endsWith(".txt")) {
                     continue; // Skip files that are not level files
                 }

                 levelsComboBox.addItem(file.getName());

                 BufferedImage levelImg = defaultImg;
                 File levelImgFile = new File(folder.getPath() + "/" + file.getName().replaceAll(".txt", "") + "_icon.png");
                 if (levelImgFile.exists()) {
                     levelImg = loadImage(levelImgFile.getPath());
                 }

                 JButton button = new JButton();
                 button.setIcon(new ImageIcon(levelImg));
                 button.setOpaque(false);
                 button.setContentAreaFilled(false);
                 button.setBorderPainted(false);
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

                         game.setActiveLevel(folder.getPath() + "/" + file.getName(), false);
                         game.camera.setFocusPoint(new Location(game.getActiveLevel().getActualWidth() / 2, game.getActiveLevel().getActualHeight() / 2));
                         game.getActiveLevel().setEditMode(false);
                     }
                 });
                 levelsPanel.add(button);


                 //menu.icons.add(GameUtils.makeRoundedCorner(levelImg, 30));



                 // Add button to levelsPanel
             }
         }

         add(new BackButton(menu.cl, menu.contextPanel, "Home"));






         // Refresh the UI to reflect changes
         revalidate();
         repaint();
     }





     private BufferedImage loadImage(String imagePath) {
        try {
            return ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}



class SettingsMenu extends JPanel {

    protected CardLayout cl;

    private JPanel buttonsPanel;
    private JPanel volumePanel;
    private JPanel graphicsPanel;
    public JCheckBox shaders;

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
        shaders = new JCheckBox("Shaders");
        graphicsPanel.add(shaders);
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