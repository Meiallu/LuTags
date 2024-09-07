package me.meiallu.lutags.util

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.meiallu.lutags.data.Medal
import me.meiallu.lutags.data.Tag
import org.bukkit.entity.Player

class Placeholder : PlaceholderExpansion() {

    override fun getAuthor(): String {
        return "Meiallu"
    }

    override fun getIdentifier(): String {
        return "lutags"
    }

    override fun getVersion(): String {
        return "1.0"
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (player == null)
            return null

        val paramsArray = params.split("_")

        when (paramsArray[0].lowercase()) {
            "maxtag" -> {
                when (paramsArray[1].lowercase()) {
                    "name" -> return Tag.getMaxTag(player).name
                    "color" -> return Tag.getMaxTag(player).color
                    "styled" -> return Tag.getMaxTag(player).styled
                    "prefix" -> return Tag.getMaxTag(player).prefix
                    "priority" -> return Tag.getMaxTag(player).priority.toString()
                    "permission" -> return Tag.getMaxTag(player).permission
                }
            }

            "tag" -> {
                val uuid = player.uniqueId.toString()

                when (paramsArray[1].lowercase()) {
                    "name" -> return Tag.getPlayerTag(uuid).name
                    "color" -> return Tag.getPlayerTag(uuid).color
                    "styled" -> return Tag.getPlayerTag(uuid).styled
                    "prefix" -> return Tag.getPlayerTag(uuid).prefix
                    "priority" -> return Tag.getPlayerTag(uuid).priority.toString()
                    "permission" -> return Tag.getPlayerTag(uuid).permission
                    else -> {
                        val tag = Tag.getByName(paramsArray[1])

                        if (tag != null) {
                            when (paramsArray[2].lowercase()) {
                                "name" -> return tag.name
                                "color" -> return tag.color
                                "styled" -> return tag.styled
                                "prefix" -> return tag.prefix
                                "priority" -> return tag.priority.toString()
                                "permission" -> return tag.permission
                            }
                        }
                    }
                }
            }

            "medal" -> {
                val uuid = player.uniqueId.toString()

                when (paramsArray[1].lowercase()) {
                    "name" -> return Medal.getPlayerMedal(uuid).name
                    "icon" -> return Medal.getPlayerMedal(uuid).icon
                    "prefix" -> return Medal.getPlayerMedal(uuid).prefix
                    "priority" -> return Medal.getPlayerMedal(uuid).priority.toString()
                    "permission" -> return Medal.getPlayerMedal(uuid).permission
                    else -> {
                        val medal = Medal.getByName(paramsArray[1])

                        if (medal != null) {
                            when (paramsArray[2].lowercase()) {
                                "name" -> return medal.name
                                "icon" -> return medal.icon
                                "prefix" -> return medal.prefix
                                "priority" -> return medal.priority.toString()
                                "permission" -> return medal.permission
                            }
                        }
                    }
                }
            }
        }
        return null
    }
}