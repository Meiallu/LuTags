package me.meiallu.lutags.data;

import me.meiallu.lutags.LuTags;
import me.meiallu.lutags.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Tag {

    public static HashMap<UUID, String> cachedTags = new HashMap<>();
    public static Tag defaultTag;

    public String name;
    public String color;
    public String styled;
    public String prefix;
    public int priority;
    public String permission;
    public String[] aliases;

    public static Tag setCacheTag(UUID uuid) {
        Util.log("Putting " + uuid + "'s tag on cache.");

        String stringUUID = uuid.toString();
        String tagName = LuTags.storage.readTag(stringUUID);

        Tag tag = defaultTag;

        if (tagName != null)
            tag = getByName(tagName);

        if (tag != defaultTag)
            cachedTags.put(uuid, tag.name);

        return tag;
    }

    public static void setTag(UUID uuid, Tag tag) {
        Util.log("Setting tag \"/" + tag.name + "\" to " + uuid.toString());
        LuTags.storage.writeTag(uuid.toString(), tag.name);

        if (tag == defaultTag)
            cachedTags.remove(uuid);
        else
            cachedTags.put(uuid, tag.name);
    }

    public static Tag getMaxTag(Player player) {
        Util.log("Getting " + player.getName() + "'s maximum tag...");
        Tag tag = null;

        for (Tag loopTag : LuTags.tags.getOrdered())
            if (player.hasPermission(loopTag.permission))
                tag = loopTag;

        return tag == null ? LuTags.tags.getOrdered().get(0) : tag;
    }

    public static Tag getPlayerTag(String uuid) {
        Util.log("Getting " + uuid + "'s tag...");
        UUID actualUUID = UUID.fromString(uuid);

        if (cachedTags.containsKey(actualUUID))
            return getByName(cachedTags.get(actualUUID));

        Player player = Bukkit.getPlayer(actualUUID);

        if (player == null) {
            String tagName = LuTags.storage.readTag(uuid);

            if (tagName != null)
                return getByName(tagName);
        }

        return defaultTag;
    }

    public static Tag getByName(String name) {
        Util.log("Getting tag by name, input is " + name);

        for (String key : LuTags.tags.tags.keySet()) {
            Tag tag = LuTags.tags.tags.get(key);

            if (key.equalsIgnoreCase(name) || tag.name.equalsIgnoreCase(name))
                return tag;

            for (String alias : tag.aliases)
                if (alias.equalsIgnoreCase(name))
                    return tag;
        }

        return defaultTag;
    }
}
