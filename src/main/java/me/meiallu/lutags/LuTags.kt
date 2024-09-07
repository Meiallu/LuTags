package me.meiallu.lutags

import me.meiallu.lutags.command.*
import me.meiallu.lutags.config.Configuration
import me.meiallu.lutags.config.MedalConfiguration
import me.meiallu.lutags.config.StorageConfiguration
import me.meiallu.lutags.config.TagConfiguration
import me.meiallu.lutags.data.*
import me.meiallu.lutags.listener.PlayerListener
import me.meiallu.lutags.util.Placeholder
import me.meiallu.lutags.util.Util
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class LuTags : JavaPlugin() {

    companion object {
        lateinit var plugin: Plugin
        lateinit var configuration: Configuration
        lateinit var sql: StorageConfiguration
        lateinit var storage: Storage
        lateinit var tags: TagConfiguration
        lateinit var medals: MedalConfiguration
    }

    override fun onEnable() {
        val y = "\u001B[33m"
        val w = "\u001B[37m"

        print("$y ___       ___  ___  _________  ________  ________  ________      $w")
        print("$y|\\  \\     |\\  \\|\\  \\|\\___   ___\\\\   __  \\|\\   ____\\|\\   ____\\     $w")
        print("$y\\ \\  \\    \\ \\  \\\\\\  \\|___ \\  \\_\\ \\  \\|\\  \\ \\  \\___|\\ \\  \\___|_    $w")
        print("$y \\ \\  \\    \\ \\  \\\\\\  \\   \\ \\  \\ \\ \\   __  \\ \\  \\  __\\ \\_____  \\   $w")
        print("$y  \\ \\  \\____\\ \\  \\\\\\  \\   \\ \\  \\ \\ \\  \\ \\  \\ \\  \\|\\  \\|____|\\  \\  $w")
        print("$y   \\ \\_______\\ \\_______\\   \\ \\__\\ \\ \\__\\ \\__\\ \\_______\\____\\_\\  \\ $w")
        print("$y    \\|_______|\\|_______|    \\|__|  \\|__|\\|__|\\|_______|\\_________\\$w")
        print("$y                                                      \\|_________|$w")
        print(w)

        plugin = this
        configuration = Configuration.load()
        sql = StorageConfiguration.load()
        tags = TagConfiguration.load()
        medals = MedalConfiguration.load()

        Util.log("Starting database connection...")

        storage = when (sql.database_type) {
            "mysql" -> MySQL()
            "redis" -> Redis()
            "mongodb" -> MongoDB()
            else -> SQLite()
        }

        Util.log("PlaceholderAPI registration started...")
        Placeholder().register()

        Util.log("Loading commands...")

        if (configuration.tag_system) {
            Util.log("Registering tag command...")
            server.getPluginCommand("tag").executor = TagCommand()

            if (configuration.tag_interface) {
                Util.log("Registering tag interface...")

                server.getPluginCommand("taggui").executor = TagGuiCommand()
                server.pluginManager.registerEvents(TagGuiCommand(), this)
            }
        }

        if (configuration.medal_system) {
            Util.log("Registering medal command...")
            server.getPluginCommand("medal").executor = MedalCommand()

            if (configuration.medal_interface) {
                Util.log("Registering medal interface...")

                server.getPluginCommand("medalgui").executor = MedalGuiCommand()
                server.pluginManager.registerEvents(MedalGuiCommand(), this)
            }
        }

        Util.log("Registering main command...")
        server.getPluginCommand("lutags").executor = MainCommand()

        server.pluginManager.registerEvents(PlayerListener(), this)
    }
}