package me.meiallu.lutags.config;

import com.esotericsoftware.yamlbeans.YamlReader;
import me.meiallu.lutags.data.Medal;
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

public class MedalConfiguration {

    public HashMap<String, Medal> medals = new HashMap<>();

    public static MedalConfiguration load() {
        try {
            File configFile = new File("plugins/LuTags/medal.yml");
            Util.log("Loading medals configuration file...");

            if (!configFile.exists()) {
                URL url = Configuration.class.getResource("/medal.yml");
                FileUtils.copyURLToFile(Objects.requireNonNull(url), configFile);
            }

            InputStream inputStream = Files.newInputStream(configFile.toPath());
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            YamlReader configReader = new YamlReader(reader);
            MedalConfiguration configuration = configReader.read(MedalConfiguration.class);

            for (Medal tag : configuration.medals.values()) {
                tag.icon = ChatColor.translateAlternateColorCodes('&', tag.icon);
                tag.prefix = ChatColor.translateAlternateColorCodes('&', tag.prefix);
            }

            Medal.defaultMedal = configuration.getOrdered().get(0);
            return configuration;
        } catch (IOException exception) {
            Bukkit.shutdown();
            throw new RuntimeException(exception);
        }
    }

    public ArrayList<Medal> getOrdered() {
        ArrayList<Medal> list = new ArrayList<>();

        for (int i = medals.values().size(); i > 0; i--)
            for (Medal loopedMedal : medals.values())
                if (loopedMedal.priority == i)
                    list.add(loopedMedal);

        return list;
    }
}
