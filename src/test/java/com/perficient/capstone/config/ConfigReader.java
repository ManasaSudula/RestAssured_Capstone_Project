package com.perficient.capstone.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads environment specific property files (config/qa.properties, config/uat.properties).
 * The environment is selected with  -Denv=qa  (default: qa).
 *
 * Lookup order for any key:  -Dkey (JVM)  ->  property file.
 * The token additionally supports the GOREST_TOKEN environment variable.
 */
public final class ConfigReader {

    private static final Properties PROPS = new Properties();
    private static final String ENV = resolveEnv();

    static {
        String file = "config/" + ENV + ".properties";
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new IllegalStateException("Property file not found on classpath: " + file);
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load " + file, e);
        }
    }

    private ConfigReader() { }

    private static String resolveEnv() {
        String env = System.getProperty("env");
        // "${env}" can appear if surefire passes an unresolved placeholder
        if (env == null || env.isBlank() || env.contains("${")) {
            return "qa";
        }
        return env.trim().toLowerCase();
    }

    public static String getEnv() {
        return ENV;
    }

    public static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override.trim();
        }
        return PROPS.getProperty(key, "").trim();
    }

    public static long getLong(String key, long defaultValue) {
        String v = get(key);
        return v.isEmpty() ? defaultValue : Long.parseLong(v);
    }

    /** Base URL without trailing slash (https://gorest.co.in/ -> https://gorest.co.in). */
    public static String getBaseUrl() {
        String url = get("baseUrl");
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public static String getBasePath() {
        return get("basePath");
    }

    /** Token precedence: -Dtoken  >  GOREST_TOKEN env var  >  property file. */
    public static String getToken() {
        String sys = System.getProperty("token");
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }
        String env = System.getenv("GOREST_TOKEN");
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return get("token");
    }
}
