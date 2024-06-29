package utils;

public enum ConfigAttribute {

    TITLE_LEVEL_EDITOR_DIRECTORY("saves/levels"),
    TITLE_SETTINGS_GRAPHICS_FPS(60),
    TITLE_SETTINGS_VOLUME_EFFECTS(100),
    TITLE_SETTINGS_VOLUME_MUSIC(100);

    private final Object defaultValue;

    ConfigAttribute(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
