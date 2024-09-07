package me.meiallu.lutags.manager

import me.clip.placeholderapi.PlaceholderAPI
import me.meiallu.lutags.LuTags
import me.meiallu.lutags.manager.TagManager.setCorePrefix
import me.meiallu.lutags.util.Format
import me.meiallu.lutags.util.Util
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object FormatManager {

    val cachedFormats: HashMap<UUID, Format?> = HashMap()

    fun setup() {
        Util.log("Starting setup for the format manager...")

        if (LuTags.configuration.placeholder_update_enabled) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(LuTags.plugin, {
                for (player in Bukkit.getOnlinePlayers())
                    calculateSetUpdate(player)
            }, 0L, LuTags.configuration.placeholder_update_interval)
        }
    }

    fun getSuffixFormat(player: Player): String {
        Util.log("Getting suffix without format for " + player.name + "...")
        val format = getFormat(player) ?: return ""

        return format.suffix
    }

    fun getPrefixFormat(player: Player): String {
        Util.log("Getting prefix without format for " + player.name + "...")
        val format = getFormat(player) ?: return ""

        return format.prefix
    }

    fun getFormatPriority(player: Player): Char {
        Util.log("Getting priority for " + player.name + "...")

        val format = getFormat(player) ?: return 'z'
        var asString = format.priority

        asString = PlaceholderAPI.setPlaceholders(player, asString)

        val priority = asString.toIntOrNull() ?: return 'z'
        return priority.toChar()
    }

    fun calculateSetUpdate(player: Player) {
        val format = calculate(player)
        cachedFormats[player.uniqueId] = format
        setCorePrefix(player)
    }

    private fun getFormat(player: Player): Format? {
        Util.log("Getting format for " + player.name + "...")
        val uuid = player.uniqueId

        if (cachedFormats.containsKey(uuid))
            return cachedFormats[uuid]

        val format = calculate(player)
        cachedFormats[player.uniqueId] = format

        return format
    }

    private fun calculate(player: Player): Format? {
        Util.log("Calculating format for " + player.name + "...")

        for (format in LuTags.configuration.formats) {
            if (!format.value.enabled)
                continue

            val worldName = player.world.name

            for (world in format.value.worlds)
                if (worldName.matches(world.toRegex()))
                    return format.value

            for (placeholder in format.value.conditional) {
                val actualValue = PlaceholderAPI.setPlaceholders(player, "%${placeholder.key}%")

                for (match in placeholder.value)
                    if (actualValue.matches(match.toRegex()))
                        return format.value
            }
        }
        return null
    }
}