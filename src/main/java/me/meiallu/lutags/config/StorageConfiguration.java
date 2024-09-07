package me.meiallu.lutags.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import me.meiallu.lutags.util.Util;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;

public class StorageConfiguration {

    public String database_type;
    public HashMap<String, HashMap<String, String>> database;

    public String sql_table_creation;
    public String sql_write;
    public String sql_read_tag;
    public String sql_read_medal;

    public static StorageConfiguration load() {
        try {
            File configFile = new File("plugins/LuTags/storage.yml");
            Util.log("Loading storage configuration file...");

            if (!configFile.exists()) {
                URL url = Configuration.class.getResource("/storage.yml");
                FileUtils.copyURLToFile(Objects.requireNonNull(url), configFile);
            }

            InputStream inputStream = Files.newInputStream(configFile.toPath());
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            YamlReader configReader = new YamlReader(reader);
            return configReader.read(StorageConfiguration.class);
        } catch (IOException exception) {
            Bukkit.shutdown();
            throw new RuntimeException(exception);
        }
    }
}
