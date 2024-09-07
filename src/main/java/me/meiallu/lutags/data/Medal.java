package me.meiallu.lutags.data;

import me.meiallu.lutags.LuTags;
import me.meiallu.lutags.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Medal {

    public static HashMap<UUID, String> cachedMedals = new HashMap<>();
    public static Medal defaultMedal;

    public String name;
    public String icon;
    public String prefix;
    public int priority;
    public String permission;

    public static Medal setCacheMedal(UUID uuid) {
        Util.log("Putting " + uuid + "'s medal on cache.");

        String stringUUID = uuid.toString();
        String medalName = LuTags.storage.readMedal(stringUUID);

        Medal medal = defaultMedal;

        if (medalName != null)
            medal = getByName(medalName);

        if (medal != defaultMedal)
            cachedMedals.put(uuid, medal.name);

        return medal;
    }

    public static void setMedal(UUID uuid, Medal medal) {
        Util.log("Setting medal \"/" + medal.name + "\" to " + uuid.toString());
        LuTags.storage.writeMedal(uuid.toString(), medal.name);

        if (medal == defaultMedal)
            cachedMedals.remove(uuid);
        else
            cachedMedals.put(uuid, medal.name);
    }

    public static Medal getPlayerMedal(String uuid) {
        Util.log("Getting " + uuid + "'s medal...");
        UUID actualUUID = UUID.fromString(uuid);

        if (cachedMedals.containsKey(actualUUID))
            return getByName(cachedMedals.get(actualUUID));

        Player player = Bukkit.getPlayer(actualUUID);

        if (player == null) {
            String medalName = LuTags.storage.readMedal(uuid);

            if (medalName != null)
                return getByName(medalName);
        }

        return defaultMedal;
    }

    public static Medal getByName(String name) {
        Util.log("Getting medal by name, input is " + name);

        for (String key : LuTags.medals.medals.keySet()) {
            Medal medal = LuTags.medals.medals.get(key);

            if (key.equalsIgnoreCase(name) || medal.name.equalsIgnoreCase(name))
                return medal;
        }

        return defaultMedal;
    }
}
