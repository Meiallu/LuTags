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

class TagCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command?, label: String?, args: Array<String?>): Boolean {
        Util.log("Trying to execute \"/" + label + "\" to " + sender.name)

        if (sender is ConsoleCommandSender) {
            sender.sendMessage(LuTags.configuration.only_players)
            return false
        }

        val player = sender as Player
        val uuid = player.uniqueId.toString()

        if (args.size == 1) {
            val tag = Tag.getByName(args[0])
            val currentTag = Tag.getPlayerTag(uuid)

            if (tag == null) {
                Util.send(player, LuTags.configuration.tag_not_found)
                return false
            }

            if (!player.hasPermission(tag.permission)) {
                Util.send(player, LuTags.configuration.no_permission)
                return false
            }

            if (currentTag == tag) {
                Util.send(player, LuTags.configuration.already_selected_tag)
                return false
            }

            Tag.setTag(player.uniqueId, tag)
            TagManager.setCorePrefix(player)

            Util.send(player, LuTags.configuration.successfully_selected_tag)
        } else {
            val tags = LuTags.tags.ordered
            var hasPermission = false

            for (tag in tags) {
                if (player.hasPermission(tag.permission)) {
                    hasPermission = true
                    break
                }
            }

            if (!hasPermission) {
                Util.send(player, LuTags.configuration.no_tags)
                return false
            }

            val prefixTagList = PlaceholderAPI.setPlaceholders(player, LuTags.configuration.prefix_tag_list)
            val msg = TextComponent(prefixTagList)
            val medal = Medal.getPlayerMedal(uuid)
            val suffixFormat = FormatManager.getSuffixFormat(player)
            val prefixFormat = FormatManager.getPrefixFormat(player)

            for (i in tags.indices) {
                val tag = tags[i]

                if (player.hasPermission(tag.permission)) {
                    val preVisualization = PlaceholderAPI.setPlaceholders(player, Util.getTagMedalPreview(player, tag, medal, suffixFormat, prefixFormat))
                    val textComponent = TextComponent(preVisualization)
                    val hoverText = arrayOf<BaseComponent>(textComponent)

                    val click = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tag ${tag.name}")
                    val hover = HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)

                    val virgula = TextComponent(if (i == tags.size - 1) "§f." else "§f, ")
                    val tagName = TextComponent(tag.styled)

                    tagName.clickEvent = click
                    tagName.hoverEvent = hover

                    msg.addExtra(tagName)
                    msg.addExtra(virgula)
                }
            }
            player.spigot().sendMessage(msg)
        }

        Util.log("Successfully executed the command.")
        return true
    }
}