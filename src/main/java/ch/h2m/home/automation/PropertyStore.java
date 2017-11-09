package ch.h2m.home.automation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyStore {


    private static PropertyStore instance;
    private Map<String, String> properties;

    private PropertyStore() {
        properties = new ConcurrentHashMap<>();
        loadPropertiesFile();
    }

    /**
     * Lazy Loading is fine
     */
    public static PropertyStore getInstance() {
        if (instance == null) {
            instance = new PropertyStore();
        }
        return instance;
    }


    public String getValue(String key) {
        return properties.get(key);
    }

    private void loadPropertiesFile() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream("config.properties")) {

            Properties props = new Properties();
            props.load(is);

            props.forEach((key, value) -> {
                properties.put(key.toString(), value.toString());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
