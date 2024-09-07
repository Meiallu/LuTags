package me.meiallu.lutags.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import me.meiallu.lutags.data.Tag;
import me.meiallu.lutags.util.Util;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TagConfiguration {

    public HashMap<String, Tag> tags = new HashMap<>();

    public static TagConfiguration load() {
        try {
            File configFile = new File("plugins/LuTags/tags.yml");
            Util.log("Loading tags configuration file...");

            if (!configFile.exists()) {
                URL url = Configuration.class.getResource("/tags.yml");
                FileUtils.copyURLToFile(Objects.requireNonNull(url), configFile);
            }

            InputStream inputStream = Files.newInputStream(configFile.toPath());
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            YamlReader configReader = new YamlReader(reader);
            TagConfiguration configuration = configReader.read(TagConfiguration.class);

            for (Tag tag : configuration.tags.values()) {
                tag.color = ChatColor.translateAlternateColorCodes('&', tag.color);
                tag.prefix = ChatColor.translateAlternateColorCodes('&', tag.prefix);
                tag.styled = ChatColor.translateAlternateColorCodes('&', tag.styled);
            }

            Tag.defaultTag = configuration.getOrdered().get(0);
            return configuration;
        } catch (IOException exception) {
            Bukkit.shutdown();
            throw new RuntimeException(exception);
        }
    }

    public ArrayList<Tag> getOrdered() {
        ArrayList<Tag> list = new ArrayList<>();

        for (int i = tags.values().size(); i > 0; i--)
            for (Tag loopedTag : tags.values())
                if (loopedTag.priority == i)
                    list.add(loopedTag);

        return list;
    }
}
