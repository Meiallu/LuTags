package me.meiallu.lutags.listener

import me.meiallu.lutags.data.Medal
import me.meiallu.lutags.data.Tag
import me.meiallu.lutags.manager.FormatManager
import me.meiallu.lutags.manager.TagManager
import me.meiallu.lutags.util.Util
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.WorldInitEvent

class PlayerListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val uuid = event.player.uniqueId

        Util.log("Starting join process to " + player.name + "...")

        val currentTag = Tag.setCacheTag(uuid)
        val currentMedal = Medal.setCacheMedal(uuid)

        if (!player.hasPermission(currentTag.permission)) {
            Util.log(player.name + " doesn't have permission for its current tag...")
            Tag.setTag(player.uniqueId, Tag.getMaxTag(player))
        }

        if (!player.hasPermission(currentMedal.permission)) {
            Util.log(player.name + " doesn't have permission for its current medal...")
            Medal.setMedal(player.uniqueId, Medal.defaultMedal)
        }

        TagManager.setCorePrefix(player)
        TagManager.updateFor(player)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onLeave(event: PlayerQuitEvent) {
        FormatManager.cachedFormats.remove(event.player.uniqueId)
        Tag.cachedTags.remove(event.player.uniqueId)
        Medal.cachedMedals.remove(event.player.uniqueId)
        TagManager.teams.remove(event.player)
        TagManager.prefixes.remove(event.player)
        TagManager.suffixes.remove(event.player)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onChangeWorld(event: PlayerChangedWorldEvent) {
        FormatManager.calculateSetUpdate(event.player)
    }

    @EventHandler
    fun onWorldInit(event: WorldInitEvent) {
        TagManager.setup()
        FormatManager.setup()
    }
}