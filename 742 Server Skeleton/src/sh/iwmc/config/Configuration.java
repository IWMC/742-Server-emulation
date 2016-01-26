package sh.iwmc.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Brent on 01/24/2016.
 */
public class Configuration {

    private static Yaml yaml = new Yaml();

    private HashMap<String, Object> data = new HashMap<>();

    private Path location;

    public Configuration() {

    }

    public Configuration(Path configPath) {
        try {
            load(configPath);
        } catch (IOException e) {

        }
    }

    private Object getObjectAtPath(String path) {
        return getObjectAtPath(path, data, null, 0, Object.class);
    }

    private <E> E getObjectAtPath(String path, Class<E> c) {
        return getObjectAtPath(path, data, null, 0, c);
    }

    private <E> E getObjectAtPath(String path, Map<String, Object> base, String[] split, int index, Class<E> c) {
        if (split == null) {
            return getObjectAtPath(path, base, path.split("\\."), 0, c);
        }

        String key = split[index];
        if (!base.containsKey(key)) {
            return null;
        }

        boolean last = split.length == index - 1;
        Object candidate = base.get(key);
        if (last) {
            return (E) candidate;
        }

        if (candidate instanceof Map) {
            Map<String, Object> newBase = (Map<String, Object>) candidate;
            return getObjectAtPath(path, newBase, split, ++index, c);
        } else if (candidate instanceof List) {

        }

        return (E) candidate;
    }

    public void load(Path path) throws IOException {
        location = path;
        InputStream ios = new FileInputStream(path.toFile());
        data = (HashMap<String, Object>) yaml.load(ios);
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        String o = getObjectAtPath(key, String.class);
        return o == null ? defaultValue : o;
    }

    public int getInt(String key) {
        Object o = getObjectAtPath(key);
        return Integer.parseInt(String.valueOf(o));
    }

    public void put(String key, Object val) {
        data.put(key, val);
    }

    public Map<String, Object> getRawData() {
        return data;
    }

    public void reload() {
        try {
            data.clear();
            load(location);
        } catch (IOException e) {

        }
    }

}
