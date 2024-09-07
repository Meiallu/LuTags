package me.meiallu.lutags.command

import me.clip.placeholderapi.PlaceholderAPI
import me.meiallu.lutags.LuTags
import me.meiallu.lutags.data.Medal
import me.meiallu.lutags.data.Tag
import me.meiallu.lutags.manager.FormatManager
import me.meiallu.lutags.manager.TagManager
import me.meiallu.lutags.util.Util
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class MedalCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command?, label: String?, args: Array<String?>): Boolean {
        Util.log("Trying to execute \"/" + label + "\" to " + sender.name)

        if (sender is ConsoleCommandSender) {
            sender.sendMessage(LuTags.configuration.only_players)
            return false
        }

        val player = sender as Player
        val uuid = player.uniqueId.toString()

        if (args.size == 1) {
            val medal = Medal.getByName(args[0])
            val currentMedal = Medal.getPlayerMedal(uuid)

            if (medal == null) {
                Util.send(player, LuTags.configuration.medal_not_found)
                return false
            }

            if (!player.hasPermission(medal.permission)) {
                Util.send(player, LuTags.configuration.no_permission)
                return false
            }

            if (currentMedal == medal) {
                Util.send(player, LuTags.configuration.already_selected_medal)
                return false
            }

            Medal.setMedal(player.uniqueId, medal)
            TagManager.setCorePrefix(player)

            Util.send(player, LuTags.configuration.successfully_selected_medal)
        } else {
            val medals = LuTags.medals.ordered
            var hasPermission = false

            for (medal in medals) {
                if (player.hasPermission(medal.permission)) {
                    hasPermission = true
                    break
                }
            }

            if (!hasPermission) {
                Util.send(player, LuTags.configuration.no_medals)
                return false
            }

            val prefixMedalList = PlaceholderAPI.setPlaceholders(player, LuTags.configuration.prefix_medal_list)
            val msg = TextComponent(prefixMedalList)
            val tag = Tag.getPlayerTag(uuid)
            val suffixFormat = FormatManager.getSuffixFormat(player)
            val prefixFormat = FormatManager.getPrefixFormat(player)

            for (i in medals.indices) {
                val medal = medals[i]

                if (player.hasPermission(medal.permission)) {
                    val preVisualization = PlaceholderAPI.setPlaceholders(player, Util.getTagMedalPreview(player, tag, medal, suffixFormat, prefixFormat))
                    val textComponent = TextComponent(preVisualization)
                    val hoverText = arrayOf<BaseComponent>(textComponent)

                    val click = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/medal ${medal.name}")
                    val hover = HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)

                    val virgula = TextComponent(if (i == medals.size - 1) "§f." else "§f, ")
                    val medalName = TextComponent(medal.icon)

                    medalName.clickEvent = click
                    medalName.hoverEvent = hover

                    msg.addExtra(medalName)
                    msg.addExtra(virgula)
                }
            }
            player.spigot().sendMessage(msg)
        }

        Util.log("Successfully executed the command.")
        return true
    }
}