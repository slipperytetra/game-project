package utils;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private Map<ConfigAttribute, Object> configMap;

    public ConfigManager() {
        this.configMap = new HashMap<>();
    }

    public void addItem(ConfigAttribute attribute, Object value) {
        configMap.put(attribute, value);
    }

    public Object getItem(ConfigAttribute attribute) {
        return configMap.get(attribute);
    }

    @SuppressWarnings("unchecked")
    public <T> T getItem(ConfigAttribute key, Class<T> type) {
        Object value = configMap.get(key);
        if (value == null) {
            return null;
        }

        if (type.isInstance(value)) {
            return (T) value;
        } else {
            throw new IllegalArgumentException("Config value is not of type " + type.getName());
        }
    }
}