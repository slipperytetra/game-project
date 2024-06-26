package main;

import block.BlockTypes;
import block.decorations.DecorationTypes;
import level.*;
import level.editor.*;
import level.item.Inventory;
import level.item.InventoryItemSlot;
import utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;


public class Game extends GameEngine {
    public static int BLOCK_SIZE = 32;
    private boolean gameOver;
    public static boolean isPaused;
    private AudioBank audioBank;
    private TextureBank textureBank;

    public long timeSinceLastFrame;
    public long lastTime;
    public long currentTime;
    public Set<Integer> keysPressed = new HashSet();
    public CollisionBox mouseBox;
    public double mouseX, mouseY;

    public JPanel editingPanel;
    public JComboBox<ComboBoxItem> cBoxBlocks;
    public JComboBox<ComboBoxItem> cBoxEntities;
    public JComboBox<ComboBoxItem> cBoxDecorations;

    private ConfigManager configManager;
    private LevelEditor levelEditor;

    LevelManager lvlManager;
    private Level activeLevel;
    Camera camera;

    Location tempLoc;

    public static void main(String[] args) {
        createGame(new GameMenuNew(),  60);
        //SwingUtilities.invokeLater(GameMenuNew::new);
    }

    public void startGame(){
        createGame(this,  60);
        initComboBoxes();
    }

    @Override
    public void setupMenu() {
        mPanel.add(editingPanel);
        this.mFrame.addWindowListener(new SaveGameListener(getActiveLevel()));
    }

    public void init() {
        this.textureBank = new TextureBank(this);
        this.audioBank = new AudioBank(this);
        this.setWindowSize(1280, 720);

        this.lvlManager = new LevelManager(this);
        setActiveLevel(lvlManager.FOREST);
        this.levelEditor = new LevelEditorBlocks(getActiveLevel());
        tempLoc = new Location(getActiveLevel().getActualWidth() / 2, getActiveLevel().getActualWidth() / 2);
        this.mouseBox = new CollisionBox(mouseX, mouseY, 2, 2);

        this.configManager = new ConfigManager();
        for (ConfigAttribute att : ConfigAttribute.values()) {
            configManager.addItem(att, att.getDefaultValue());
        }
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


    public void setActiveLevel(String levelPath, boolean editMode) {
        File file = new File(levelPath);
        if (!file.exists()) {
            System.out.println("Error creating temp level: file " + levelPath + " not found.");
            return;
        }

        Level level = new Level(lvlManager, 999, levelPath);
        if (activeLevel != null) {
            if (activeLevel.getBackgroundMusic() != null) {
                stopAudioLoop(activeLevel.getBackgroundMusic());
            }
        }

        this.activeLevel = level;
        this.activeLevel.init();
        level.load();
        this.camera = new Camera(this, level.getPlayer());
    }

    public void update(double dt) {
        lastTime = currentTime;
        currentTime = System.currentTimeMillis();
        timeSinceLastFrame = currentTime - lastTime;
        camera.update(dt);
        //System.out.println(mouseX + ", " + mouseY);

        if (!isPaused) {
            getActiveLevel().update(dt);
        }

        if (textureBank != null) {
            textureBank.update(dt);
        }
    }

    public void paintComponent() {
        camera.draw();
    }

    @Override
    public void keyPressed(KeyEvent event) {
        this.keysPressed.add(event.getKeyCode());

        if (event.getKeyCode() == 27) { //ESCAPE
            isPaused = !isPaused;
        }

        if(event.getKeyCode() == 81 && isPaused) { //Q
            System.exit(0);
        }

        if (getActiveLevel().isEditMode()) {
            if (keysPressed.contains(87)) {//W
                camera.tempLocY = camera.tempLocY - Game.BLOCK_SIZE;
            }
            if (keysPressed.contains(65)) {//A
                camera.tempLocX = camera.tempLocX - Game.BLOCK_SIZE;
            }
            if (keysPressed.contains(83)) {//S
                camera.tempLocY = camera.tempLocY + Game.BLOCK_SIZE;
            }
            if (keysPressed.contains(68)) {//D
                camera.tempLocX = camera.tempLocX + Game.BLOCK_SIZE;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        this.keysPressed.remove(event.getKeyCode());
        if (event.getKeyCode() == 72) {
            camera.debugMode = !camera.debugMode;
        }

        if (event.getKeyCode() == 69) {
            activeLevel.setEditMode(!activeLevel.isEditMode());
            if (activeLevel.isEditMode()) {
                camera.setFocusPoint(new Location(activeLevel.getActualWidth() / 2, activeLevel.getActualHeight() / 2));
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        super.mouseReleased(event);
        if (levelEditor != null) {
            levelEditor.mouseReleased(event);
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        super.mouseMoved(event);
        if (levelEditor != null) {
            levelEditor.mouseMoved(event);
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        super.mouseDragged(event);
        if (levelEditor != null) {
            levelEditor.mouseDragged(event);
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        super.mousePressed(event);
        if (levelEditor != null) {
            levelEditor.mousePressed(event);
        }

        if (getActiveLevel() != null) {
            for (Inventory inv : getActiveLevel().getOpenInventories()) {
                for (InventoryItemSlot slot : inv.getItems()) {
                    if (slot.getItem() == null) {
                        continue;
                    }
                    if (mouseBox.collidesWith(slot.getCollisionBox())) {
                        inv.setSelectedSlot(slot.getSlot());
                        getActiveLevel().playSound(SoundType.MENU_NAVIGATE);
                    }
                }
            }
        }
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

    public LevelEditor getEditor() {
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

            ImageIcon icon = new ImageIcon(getTextureBank().getTexture(type).getImage().getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH));
            choicesBlocks[i] = new ComboBoxItem(valBlocks[i].toString(), icon);

        }

        ComboBoxItem[] choicesDecorations = new ComboBoxItem[DecorationTypes.values().length];
        DecorationTypes[] valDecos = DecorationTypes.values();
        for (int i = 0; i < valDecos.length; i++) {
            ImageIcon icon = new ImageIcon(getTextureBank().getTexture(DecorationTypes.values()[i].toString().toLowerCase()).getImage().getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH));
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
    }

    public TextureBank getTextureBank() {
        return textureBank;
    }
    public AudioBank getAudioBank() {
        return audioBank;
    }

    public ConfigManager getConfig() {
        return configManager;
    }
}
