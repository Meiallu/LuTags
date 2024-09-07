package me.meiallu.lutags.command

import me.meiallu.lutags.LuTags
import me.meiallu.lutags.config.Configuration
import me.meiallu.lutags.config.MedalConfiguration
import me.meiallu.lutags.config.StorageConfiguration
import me.meiallu.lutags.config.TagConfiguration
import me.meiallu.lutags.data.Medal
import me.meiallu.lutags.data.Tag
import me.meiallu.lutags.util.Util
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class MainCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command?, label: String?, args: Array<String?>): Boolean {
        Util.log("Trying to execute \"/" + label + "\" to " + sender.name)

        if (!sender.hasPermission(LuTags.configuration.main_command_permission)) {
            sender.sendMessage(LuTags.configuration.no_permission)
            return false
        }

        if (args.isEmpty()) {
            for (message in LuTags.configuration.main_command_usage)
                sender.sendMessage(message)

            return false
        }

        when (args[0]!!.lowercase()) {
            "reload" -> {
                LuTags.configuration = Configuration.load()
                LuTags.sql = StorageConfiguration.load()
                LuTags.tags = TagConfiguration.load()
                LuTags.medals = MedalConfiguration.load()

                sender.sendMessage(LuTags.configuration.reloaded_successfully)
            }

            "version" -> {
                val message = LuTags.configuration.current_version.replace("{version}", "1.0")
                sender.sendMessage(message)
            }

            "set" -> {
                if (args.size < 4) {
                    sender.sendMessage(LuTags.configuration.usages["admin_tag_set"])
                    return false
                }

                val targetName = args[2]
                val target = Bukkit.getPlayer(targetName)

                if (target == null) {
                    sender.sendMessage(LuTags.configuration.not_online)
                    return false
                }

                when (args[1]!!.lowercase()) {
                    "tag" -> {
                        val tag = Tag.getByName(args[3])
                        var message = LuTags.configuration.successfully_set_tag

                        if (tag == null) {
                            sender.sendMessage(LuTags.configuration.tag_not_found)
                            return false
                        }

                        message = message
                            .replace("{target}", target.name)
                            .replace("{name}", tag.name)
                            .replace("{color}", tag.color)
                            .replace("{styled}", tag.styled)
                            .replace("{prefix}", tag.prefix)
                            .replace("{priority}", tag.priority.toString())
                            .replace("{permission}", tag.permission)

                        Tag.setTag(target.uniqueId, tag)
                        sender.sendMessage(message)
                    }

                    "medal" -> {
                        val medal = Medal.getByName(args[3])
                        var message = LuTags.configuration.successfully_set_medal

                        if (medal == null) {
                            sender.sendMessage(LuTags.configuration.medal_not_found)
                            return false
                        }

                        message = message
                            .replace("{target}", target.name)
                            .replace("{name}", medal.name)
                            .replace("{icon}", medal.icon)
                            .replace("{prefix}", medal.prefix)
                            .replace("{priority}", medal.priority.toString())
                            .replace("{permission}", medal.permission)

                        Medal.setMedal(target.uniqueId, medal)
                        sender.sendMessage(message)
                    }
                }
            }

            else -> {
                for (message in LuTags.configuration.main_command_usage)
                    sender.sendMessage(message)

                return false
            }
        }

        Util.log("Successfully executed the command.")
        return true
    }
}