package me.meiallu.lutags.util;

import me.clip.placeholderapi.PlaceholderAPI;
import me.meiallu.lutags.LuTags;
import me.meiallu.lutags.data.Medal;
import me.meiallu.lutags.data.Tag;
import org.bukkit.entity.Player;

public class Util {

    public static void send(Player player, String message) {
        player.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
    }

    public static void log(String message) {
        if (LuTags.configuration.debug)
            System.out.print("[LuTags] [DEBUG] " + message);
    }

    public static String parsePlaceholders(Player player, Tag tag, Medal medal, String message) {
        String newMessage = message
                .replace("%lutags_medal_name%", medal.name)
                .replace("%lutags_medal_icon%", medal.icon)
                .replace("%lutags_medal_prefix%", medal.prefix)
                .replace("%lutags_medal_priority%", String.valueOf(medal.priority))
                .replace("%lutags_medal_permission%", medal.permission)
                .replace("%lutags_tag_name%", tag.name)
                .replace("%lutags_tag_color%", tag.color)
                .replace("%lutags_tag_styled%", tag.styled)
                .replace("%lutags_tag_prefix%", tag.prefix)
                .replace("%lutags_tag_priority%", String.valueOf(tag.priority))
                .replace("%lutags_tag_permission%", tag.permission)
                .replace("%player_name%", player.getName());

        return PlaceholderAPI.setPlaceholders(player, newMessage);
    }

    public static String getTagMedalPreview(Player player, Tag tag, Medal medal, String suffixFormat, String prefixFormat) {
        String suffix = parsePlaceholders(player, tag, medal, suffixFormat);
        String prefix = parsePlaceholders(player, tag, medal, prefixFormat);

        return LuTags.configuration.preview.replace("{preview}", prefix + player.getName() + suffix);
    }
}
