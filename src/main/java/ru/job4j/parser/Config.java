package ru.job4j.parser;

import java.io.InputStream;
import java.util.Properties;

public class Config {

    private final String properties;

    public Config(String properties) {
        this.properties = properties;
    }

    private final Properties values = new Properties();

    public void init() {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(properties)) {
            values.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public String get(String key) {
        return this.values.getProperty(key);
    }
}
