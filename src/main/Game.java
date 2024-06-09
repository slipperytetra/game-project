package main;

import block.BlockTypes;
import block.decorations.DecorationTypes;
import entity.EntityType;
import level.*;
import level.editor.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class Game extends GameEngine {
    public static int BLOCK_SIZE = 32;
    private boolean gameOver;
    public boolean isPaused;

    public long timeSinceLastFrame;
    public long lastTime;
    public long currentTime;
    public HashMap<String, Texture> imageBank;
    public Set<Integer> keysPressed = new HashSet();
    public double mouseX, mouseY;

    public JPanel editingPanel;
    public JComboBox<ComboBoxItem> cBoxBlocks;
    public JComboBox<ComboBoxItem> cBoxEntities;
    public JComboBox<ComboBoxItem> cBoxDecorations;

    public LevelJFileChooserSave levelSaver;
    public LevelJFileChooserLoad levelLoader;

    private LevelItem levelEditor;
    int objectCounter;
    int maxObjects;

    LevelManager lvlManager;
    private Level activeLevel;
    Camera camera;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameMenu::new);
    }

    public void startGame(){
        createGame(this,  60);
    }

    @Override
    public void setupMenu() {
        initComboBoxes();
    }

    public void init() {
        this.imageBank = new HashMap<>();
        loadDecorationImages();
        loadBlockImages();
        loadCharacterImages();

        this.setWindowSize(1280, 720);

        this.lvlManager = new LevelManager(this);
        setActiveLevel(lvlManager.FOREST);
        this.levelEditor = new LevelEditorBlocks(getActiveLevel());
    }

    public Level getActiveLevel() {
        return activeLevel;
    }

    public void setActiveLevel(Level level) {
        if (activeLevel != null) {
            if (activeLevel.getBackgroundMusic() != null) {
                stopAudioLoop(activeLevel.getBackgroundMusic());
            }
        }

        this.activeLevel = level;
        this.activeLevel.init();
        level.load();
        this.camera = new Camera(this, level.getPlayer());
        getCamera().setFocusObject(level.getPlayer());
    }


    public void setActiveLevel(String levelPath) {
        File file = new File("resources/levels/" + levelPath);
        if (!file.exists()) {
            System.out.println("Error creating temp level: file " + levelPath + " not found.");
            return;
        }

        Level level = new Level(lvlManager, 999, "resources/levels/" + levelPath);
        if (activeLevel != null) {
            if (activeLevel.getBackgroundMusic() != null) {
                stopAudioLoop(activeLevel.getBackgroundMusic());
            }
        }

        this.activeLevel = level;
        this.activeLevel.init();
        level.load();
        this.camera = new Camera(this, level.getPlayer());
        getCamera().setFocusObject(level.getPlayer());
    }

    public void update(double dt) {
        lastTime = currentTime;
        currentTime = System.currentTimeMillis();
        timeSinceLastFrame = currentTime - lastTime;
        camera.update();


        if (!isPaused) {
            getActiveLevel().update(dt);
        }

        for (Texture texture : imageBank.values()) {
            if (texture instanceof TextureAnimated) {
                ((TextureAnimated) texture).update(dt);
            }
        }
    }

    public void paintComponent() {
        this.clearBackground(width(), height());
        /*if (isLoading()) {
            drawText((width() / 2) - 100, height() / 2, "Loading: " + getActiveLevel().loadCompletion + "%", 75);
            return;
        }*/

        camera.draw();
    }

    @Override
    public void keyPressed(KeyEvent event) {
        this.keysPressed.add(event.getKeyCode());

        if (event.getKeyCode() == 27) { //ESCAPE
            isPaused = !isPaused;
        }

        if(event.getKeyCode() == 81 && isPaused){
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        this.keysPressed.remove(event.getKeyCode());
        if (event.getKeyCode() == 72) {
            camera.debugMode = !camera.debugMode;
        }

        if (event.getKeyCode() == 65 || event.getKeyCode() == 68) {
            activeLevel.getPlayer().setDirectionX(0);
        }

        if (event.getKeyCode() == 83 || event.getKeyCode() == 87) {
            activeLevel.getPlayer().setDirectionY(0);
        }

        if (event.getKeyCode() == 69) {
            activeLevel.setEditMode(!activeLevel.isEditMode());
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        super.mouseReleased(event);
        levelEditor.mouseReleased(event);
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        super.mouseMoved(event);
        levelEditor.mouseMoved(event);
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        super.mouseDragged(event);
        levelEditor.mouseDragged(event);
    }

    @Override
    public void mousePressed(MouseEvent event) {
        super.mousePressed(event);
        levelEditor.mousePressed(event);
    }

    public void loadDecorationImages() {
        for (DecorationTypes type : DecorationTypes.values()) {
            if (type.getFrames() > 0 && type.getFrameRate() > 0) {
                BufferedImage[] frames = new BufferedImage[type.getFrames()];
                for (int i = 0; i < type.getFrames(); i++) {
                    frames[i] = (BufferedImage) loadImage(type.getFilePath() + i + ".png");
                }
                imageBank.put(type.toString().toLowerCase(), new TextureAnimated(frames, type.getFrameRate(), true));
            } else {
                imageBank.put(type.toString().toLowerCase(), new Texture((BufferedImage) loadImage(type.getFilePath())));
            }
        }

        for (ParticleTypes particleType : ParticleTypes.values()) {
            imageBank.put(particleType.toString().toLowerCase(), new Texture((BufferedImage) loadImage(particleType.getFilePath())));
        }
    }

    public void loadBlockImages() {
        for (BlockTypes type : BlockTypes.values()) {
            if (type.getBlockSetAmount() > 0) {
                for (int i = 0; i < type.getBlockSetAmount(); i++) {
                    String suffix = "_" + i;
                    imageBank.put(type.toString() + suffix, new Texture((BufferedImage) loadImage(type.getFilePath() + suffix + ".png")));
                }
            } else {
                imageBank.put(type.toString(),  new Texture((BufferedImage) loadImage(type.getFilePath())));
            }
        }
    }

    /*
    *   This is where the image bank is loaded. Basically how it works is it uses a HashMap<String, Image> and it assigns
    *   a string to an image object.
    *
    *   By doing it
    * */
    public void  loadCharacterImages() {
        imageBank.put("player_fall", new Texture((BufferedImage) loadImage("resources/images/characters/jump3.png")));

        imageBank.put("player_jump",  new TextureAnimated(new BufferedImage[]{
                (BufferedImage) loadImage("resources/images/characters/jump0.png"),
                (BufferedImage) loadImage("resources/images/characters/jump1.png"),
                (BufferedImage) loadImage("resources/images/characters/jump2.png"),
                (BufferedImage) loadImage("resources/images/characters/jump3.png")
        }, 16, false));

        imageBank.put("player_run",  new TextureAnimated(new BufferedImage[]{
                (BufferedImage) loadImage("resources/images/characters/run0.png"),
                (BufferedImage) loadImage("resources/images/characters/run1.png"),
                (BufferedImage) loadImage("resources/images/characters/run2.png"),
                (BufferedImage) loadImage("resources/images/characters/run3.png")
        }, 12));

        imageBank.put("player_attack",  new TextureAnimated(new BufferedImage[]{
                (BufferedImage) loadImage("resources/images/characters/attack0.png"),
                (BufferedImage) loadImage("resources/images/characters/attack1.png"),
                (BufferedImage) loadImage("resources/images/characters/attack2.png"),
                (BufferedImage) loadImage("resources/images/characters/attack3.png"),
                (BufferedImage) loadImage("resources/images/characters/attack4.png")
        }, 12, false));

        imageBank.put("plant_monster_attack",  new TextureAnimated(new BufferedImage[]{
                (BufferedImage) loadImage("resources/images/characters/plant/plant_attack_0.png"),
                (BufferedImage) loadImage("resources/images/characters/plant/plant_attack_1.png"),
                (BufferedImage) loadImage("resources/images/characters/plant/plant_attack_2.png"),
                (BufferedImage) loadImage("resources/images/characters/plant/plant_attack_3.png"),
                (BufferedImage) loadImage("resources/images/characters/plant/plant_attack_4.png"),
                (BufferedImage) loadImage("resources/images/characters/plant/plant_attack_5.png"),
                (BufferedImage) loadImage("resources/images/characters/plant/plant_attack_6.png")
        }, 12, false));

        imageBank.put("ui_heart",  new Texture((BufferedImage) loadImage("resources/images/ui/health_bar_heart.png")));

        imageBank.put("spot_light",  new Texture((BufferedImage) loadImage("resources/images/blocks/decorations/spot_light.png")));

            for (EntityType type : EntityType.values()) {
                if (type.getFrames() > 0 && type.getFrameRate() > 0) {
                    BufferedImage[] frames = new BufferedImage[type.getFrames()];
                    for (int i = 0; i < type.getFrames(); i++) {
                        frames[i] = (BufferedImage) loadImage(type.getFilePath() + i + ".png");
                    }
                    imageBank.put(type.toString().toLowerCase(), new TextureAnimated(frames, type.getFrameRate(), true));
                } else {
                    imageBank.put(type.toString().toLowerCase(), new Texture((BufferedImage) loadImage(type.getFilePath())));
                }
            }
    }

    public Texture getTexture(String textureName) {
        return imageBank.get(textureName);
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean isLoading() {
        if (getActiveLevel() == null) {
            return false;
        }

        return getActiveLevel().loadCompletion < 100;
    }

    public LevelItem getEditor() {
        return levelEditor;
    }

    private void initComboBoxes() {
        ComboBoxItem[] choicesBlocks = new ComboBoxItem[BlockTypes.values().length];
        BlockTypes[] valBlocks = BlockTypes.values();
        for (int i = 0; i < valBlocks.length; i++) {
            String type = BlockTypes.values()[i].toString();
            if (BlockTypes.values()[i].isBlockSet()) {
                type += "_0";
            }

            ImageIcon icon = new ImageIcon(getTexture(type).getImage().getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH));
            choicesBlocks[i] = new ComboBoxItem(valBlocks[i].toString(), icon);

        }

        ComboBoxItem[] choicesDecorations = new ComboBoxItem[DecorationTypes.values().length];
        DecorationTypes[] valDecos = DecorationTypes.values();
        for (int i = 0; i < valDecos.length; i++) {
            ImageIcon icon = new ImageIcon(getTexture(DecorationTypes.values()[i].toString().toLowerCase()).getImage().getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH));
            choicesDecorations[i] = new ComboBoxItem(valDecos[i].toString(), icon);

        }
/*
        ComboBoxItem[] choicesEntities = new ComboBoxItem[EntityType.values().length];
        EntityType[] valEnts = EntityType.values();
        for (int i = 0; i < valEnts.length; i++) {
            ImageIcon icon = new ImageIcon(getTexture(EntityType.values()[i].toString().toLowerCase()).getImage().getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH));
            choicesEntities[i] = new ComboBoxItem(valEnts[i].toString(), icon);

        }*/

        cBoxBlocks = new JComboBox<ComboBoxItem>(choicesBlocks);
        cBoxDecorations = new JComboBox<ComboBoxItem>(choicesDecorations);
        //cBoxEntities = new JComboBox<ComboBoxItem>(choicesEntities);


        cBoxBlocks.setRenderer(new ComboBoxRenderer());
        cBoxBlocks.setFocusable(false);
        cBoxBlocks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                levelEditor = new LevelEditorBlocks(getActiveLevel());
                JComboBox<ComboBoxItem> source = (JComboBox<ComboBoxItem>) e.getSource();
                ComboBoxItem item = (ComboBoxItem) source.getSelectedItem();
                if (item != null) {
                    getEditor().setSelectedItem(BlockTypes.valueOf(item.getType()));
                }
            }
        });


        cBoxDecorations.setRenderer(new ComboBoxRenderer());
        cBoxDecorations.setFocusable(false);
        cBoxDecorations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                levelEditor = new LevelEditorDecorations(getActiveLevel());
                JComboBox<ComboBoxItem> source = (JComboBox<ComboBoxItem>) e.getSource();
                ComboBoxItem item = (ComboBoxItem) source.getSelectedItem();
                if (item != null) {
                    getEditor().setSelectedItem(DecorationTypes.valueOf(item.getType()));
                }
            }
        });

/*
        cBoxEntities.setRenderer(new ComboBoxRenderer());
        cBoxEntities.setFocusable(false);
        cBoxEntities.setVisible(false);
        cBoxEntities.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<ComboBoxItem> source = (JComboBox<ComboBoxItem>) e.getSource();
                ComboBoxItem item = (ComboBoxItem) source.getSelectedItem();
                if (item != null) {
                    getEditor().setSelectedEntityType(EntityType.valueOf(item.getType()));
                }
            }
        });*/

        editingPanel = new JPanel();
        editingPanel.setBackground(new Color(150, 150, 150, 0));
        editingPanel.setVisible(false);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBackground(new Color(150, 150, 150, 150));
        topPanel.add(cBoxBlocks);
        topPanel.add(cBoxDecorations);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(150, 150, 150, 150));

        JButton buttonSave = new JButton("Save");
        LevelJFileChooserSave levelSaver = new LevelJFileChooserSave(this);
        buttonSave.addActionListener(levelSaver);
        buttonSave.setFocusable(false);

        JButton buttonLoad = new JButton("Load");
        LevelJFileChooserLoad levelLoad = new LevelJFileChooserLoad(getActiveLevel());
        buttonLoad.addActionListener(levelLoad);
        buttonLoad.setFocusable(false);

        rightPanel.add(buttonSave);
        rightPanel.add(buttonLoad);

        editingPanel.add(topPanel, BorderLayout.NORTH);
        editingPanel.add(rightPanel, BorderLayout.EAST);
        mPanel.add(editingPanel);

        //mPanel.add(editingPanel);
        //mPanel.add(cBoxEntities);
    }
}
