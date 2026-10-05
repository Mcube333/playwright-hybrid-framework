package com.playwright.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Resolves settings in this order: -Dkey, env-prefixed key in config.properties (e.g. qa.web.baseUrl),
 * plain key in config.properties.
 */
public final class Config {
    private static final Properties PROPS = load();

    private Config() {}

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) throw new IllegalStateException("config.properties not found on classpath");
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load config.properties", e);
        }
        return p;
    }

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;
        String env = System.getProperty("env", PROPS.getProperty("env", "qa"));
        String scoped = PROPS.getProperty(env + "." + key);
        return scoped != null ? scoped : PROPS.getProperty(key);
    }

    public static String get(String key, String fallback) {
        String v = get(key);
        return v == null ? fallback : v;
    }

    public static int getInt(String key, int fallback) {
        String v = get(key);
        return v == null ? fallback : Integer.parseInt(v.trim());
    }

    public static boolean getBool(String key, boolean fallback) {
        String v = get(key);
        return v == null ? fallback : Boolean.parseBoolean(v.trim());
    }

    public static String browser()      { return get("browser", "chromium"); }
    public static boolean headless()    { return getBool("headless", true); }
    public static int slowMo()          { return getInt("slowMoMs", 0); }
    public static int timeoutMs()       { return getInt("timeoutMs", 15000); }
    public static String webBaseUrl()   { return get("web.baseUrl"); }
    public static String apiBaseUrl()   { return get("api.baseUrl"); }
    public static String artifactsDir() { return get("artifactsDir", "artifacts"); }
}
