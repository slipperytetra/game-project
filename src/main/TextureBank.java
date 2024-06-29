package main;

import block.BlockTypes;
import block.decorations.DecorationTypes;
import entity.EntityType;
import level.ParticleTypes;
import level.item.ItemType;
import utils.Texture;
import utils.TextureAnimated;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class TextureBank {

    private Game game;
    private HashMap<String, Texture> textures;

    public TextureBank(Game game) {
        this.game = game;
        this.textures = new HashMap<String, Texture>();

        init();
    }

    public void init() {
        for (DecorationTypes type : DecorationTypes.values()) {
            if (type.getFrames() > 0 && type.getFrameRate() > 0) {
                BufferedImage[] frames = new BufferedImage[type.getFrames()];
                for (int i = 0; i < type.getFrames(); i++) {
                    frames[i] = (BufferedImage) game.loadImage(type.getFilePath() + i + ".png");
                }
                addTexture(type.toString().toLowerCase(), new TextureAnimated(frames, type.getFrameRate(), true));
            } else {
                addTexture(type.toString().toLowerCase(), new Texture((BufferedImage) game.loadImage(type.getFilePath())));
            }
        }

        for (ParticleTypes particleType : ParticleTypes.values()) {
            addTexture(particleType.toString().toLowerCase(), new Texture((BufferedImage) game.loadImage(particleType.getFilePath())));
        }

        for (ItemType itemType : ItemType.values()) {
            addTexture("item_" + itemType.toString().toLowerCase(), new Texture((BufferedImage) game.loadImage(itemType.getFilePath())));
        }


        for (BlockTypes type : BlockTypes.values()) {
            if (type.getBlockSetAmount() > 0) {
                for (int i = 0; i < type.getBlockSetAmount(); i++) {
                    String suffix = "_" + i;
                    addTexture(type.toString() + suffix, new Texture((BufferedImage) game.loadImage(type.getFilePath() + suffix + ".png")));
                }
            } else {
                addTexture(type.toString(),  new Texture((BufferedImage) game.loadImage(type.getFilePath())));
            }
        }

        addTexture("player_fall", new Texture((BufferedImage) game.loadImage("resources/images/characters/jump3.png")));

        addTexture("player_jump",  new TextureAnimated(new BufferedImage[]{
                (BufferedImage) game.loadImage("resources/images/characters/jump0.png"),
                (BufferedImage) game.loadImage("resources/images/characters/jump1.png"),
                (BufferedImage) game.loadImage("resources/images/characters/jump2.png"),
                (BufferedImage) game.loadImage("resources/images/characters/jump3.png")
        }, 16, false));

        addTexture("player_run",  new TextureAnimated(new BufferedImage[]{
                (BufferedImage) game.loadImage("resources/images/characters/run0.png"),
                (BufferedImage) game.loadImage("resources/images/characters/run1.png"),
                (BufferedImage) game.loadImage("resources/images/characters/run2.png"),
                (BufferedImage) game.loadImage("resources/images/characters/run3.png")
        }, 12));

        addTexture("player_attack",  new TextureAnimated(new BufferedImage[]{
                (BufferedImage) game.loadImage("resources/images/characters/attack0.png"),
                (BufferedImage) game.loadImage("resources/images/characters/attack1.png"),
                (BufferedImage) game.loadImage("resources/images/characters/attack2.png"),
                (BufferedImage) game.loadImage("resources/images/characters/attack3.png"),
                (BufferedImage) game.loadImage("resources/images/characters/attack4.png")
        }, 12, false));

        addTexture("plant_monster_attack",  new TextureAnimated(new BufferedImage[]{
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_0.png"),
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_1.png"),
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_2.png"),
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_3.png"),
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_4.png"),
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_5.png"),
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_6.png"),
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_7.png"),
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_8.png"),
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_9.png"),
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_10.png"),
                (BufferedImage) game.loadImage("resources/images/characters/plant/plant_attack_11.png")
        }, 11, false));

        addTexture("bee_attack",  new TextureAnimated(new BufferedImage[]{
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame0.png"),
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame1.png"),
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame2.png"),
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame3.png"),
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame4.png"),
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame5.png"),
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame6.png"),
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame7.png"),
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame8.png"),
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame9.png"),
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame10.png"),
                (BufferedImage) game.loadImage("resources/images/characters/bee/bee_attack_frame11.png"),
        }, 11, false));

        addTexture("ui_heart",  new Texture((BufferedImage) game.loadImage("resources/images/ui/health_bar_heart.png")));
        addTexture("ui_inventory_0",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_0.png")));
        addTexture("ui_inventory_1",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_1.png")));
        addTexture("ui_inventory_2",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_2.png")));
        addTexture("ui_inventory_3",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_3.png")));
        addTexture("ui_inventory_4",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_4.png")));
        addTexture("ui_inventory_5",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_5.png")));
        addTexture("ui_inventory_6",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_6.png")));
        addTexture("ui_inventory_7",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_7.png")));
        addTexture("ui_inventory_8",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_8.png")));
        addTexture("ui_inventory_filled",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_filled.png")));
        addTexture("ui_inventory_filled_selected",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_filled_selected.png")));
        addTexture("ui_inventory_icon",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_icon.png")));
        addTexture("ui_inventory_selected",  new Texture((BufferedImage) game.loadImage("resources/images/ui/menus/inventory_selected.png")));

        addTexture("spot_light",  new Texture((BufferedImage) game.loadImage("resources/images/blocks/decorations/spot_light.png")));

        for (EntityType type : EntityType.values()) {
            if (type.getFrames() > 0 && type.getFrameRate() > 0) {
                BufferedImage[] frames = new BufferedImage[type.getFrames()];
                for (int i = 0; i < type.getFrames(); i++) {
                    frames[i] = (BufferedImage) game.loadImage(type.getFilePath() + i + ".png");
                }
                addTexture(type.toString().toLowerCase(), new TextureAnimated(frames, type.getFrameRate(), true));
            } else {
                addTexture(type.toString().toLowerCase(), new Texture((BufferedImage) game.loadImage(type.getFilePath())));
            }
        }
    }

    public void update(double dt) {
        for (Texture texture : getTextures().values()) {
            if (texture instanceof TextureAnimated) {
                ((TextureAnimated) texture).update(dt);
            }
        }
    }

    public HashMap<String, Texture> getTextures() {
        return textures;
    }

    public void addTexture(String name, Texture texture) {
        this.textures.put(name.toLowerCase(), texture);
    }

    public void removeTexture(String name) {
        this.textures.remove(name);
    }

    public Texture getTexture(String name) {
        return textures.get(name.toLowerCase());
    }
}
