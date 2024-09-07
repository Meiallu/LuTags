package me.meiallu.lutags.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import me.meiallu.lutags.util.Format;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;

public class Configuration {

    public boolean update_enabled;
    public int update_interval;

    public boolean verification_enabled;
    public int verification_interval;

    public boolean placeholder_update_enabled;
    public long placeholder_update_interval;

    public boolean custom_nametags;
    public boolean debug;

    public boolean tag_system;
    public boolean tag_interface;
    public boolean medal_system;
    public boolean medal_interface;

    public HashMap<String, HashMap<String, String>> menus;

    public String no_tags;
    public String no_medals;

    public String already_selected_medal;
    public String already_selected_tag;

    public String tag_not_found;
    public String medal_not_found;

    public String successfully_selected_medal;
    public String successfully_selected_tag;
    public String successfully_set_medal;
    public String successfully_set_tag;

    public String only_players;
    public String no_permission;
    public String reloaded_successfully;
    public String current_version;
    public String not_online;

    public String prefix_medal_list;
    public String prefix_tag_list;

    public String preview;
    public HashMap<String, Format> formats;

    public String main_command_permission;
    public String[] main_command_usage;

    public HashMap<String, String> usages;

    public static Configuration load() {
        try {
            File folder = new File("plugins/LuTags");
            File configFile = new File("plugins/LuTags/config.yml");

            folder.mkdir();

            if (!configFile.exists()) {
                URL url = Configuration.class.getResource("/config.yml");
                FileUtils.copyURLToFile(Objects.requireNonNull(url), configFile);
            }

            InputStream inputStream = Files.newInputStream(configFile.toPath());
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            YamlReader configReader = new YamlReader(reader);
            return configReader.read(Configuration.class);
        } catch (IOException exception) {
            Bukkit.shutdown();
            throw new RuntimeException(exception);
        }
    }
}
