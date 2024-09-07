package me.meiallu.lutags.manager

import me.clip.placeholderapi.PlaceholderAPI
import me.meiallu.lutags.LuTags
import me.meiallu.lutags.data.Medal
import me.meiallu.lutags.data.Tag
import me.meiallu.lutags.util.Util
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam
import net.minecraft.server.v1_8_R3.ScoreboardTeam
import org.apache.commons.lang3.RandomStringUtils
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard
import org.bukkit.entity.Player

object TagManager {

    private lateinit var mainScoreboard: CraftScoreboard
    val teams = HashMap<Player, String>()
    val prefixes = HashMap<Player, String>()
    val suffixes = HashMap<Player, String>()

    fun setup() {
        Util.log("Starting setup for the tag manager...")
        mainScoreboard = Bukkit.getScoreboardManager().mainScoreboard as CraftScoreboard

        if (LuTags.configuration.update_enabled) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(LuTags.plugin, {
                Util.log("Updating all players tags...")

                for (player in Bukkit.getOnlinePlayers())
                    setCorePrefix(player)
            }, 0L, LuTags.configuration.update_interval * 20L)
        }

        if (LuTags.configuration.verification_enabled) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(LuTags.plugin, {
                Util.log("Checking all players permissions...")

                for (player in Bukkit.getOnlinePlayers()) {
                    val uuid = player.uniqueId.toString()
                    val currentTag = Tag.getPlayerTag(uuid)
                    val currentMedal = Medal.getPlayerMedal(uuid)

                    if (!player.hasPermission(currentTag.permission))
                        Tag.setTag(player.uniqueId, Tag.getMaxTag(player))

                    if (!player.hasPermission(currentMedal.permission))
                        Medal.setMedal(player.uniqueId, Medal.defaultMedal)

                    FormatManager.calculateSetUpdate(player)
                }
            }, 0L, LuTags.configuration.verification_interval * 20L)
        }
    }

    fun updateFor(player: Player) {
        Util.log("Updating all players tags for " + player.name + "...")
        Bukkit.getScheduler().runTaskAsynchronously(LuTags.plugin) {
            if (LuTags.configuration.custom_nametags) {
                val craftTarget = player as CraftPlayer
                val connection = craftTarget.handle.playerConnection

                for (loopPlayer in Bukkit.getOnlinePlayers()) {
                    val team = ScoreboardTeam(mainScoreboard.handle, teams[loopPlayer] ?: RandomStringUtils.random(6, true, true))

                    team.prefix = prefixes[loopPlayer] ?: ""
                    team.suffix = suffixes[loopPlayer] ?: ""

                    connection.sendPacket(PacketPlayOutScoreboardTeam(team, 1))
                    connection.sendPacket(PacketPlayOutScoreboardTeam(team, 0))
                    connection.sendPacket(PacketPlayOutScoreboardTeam(team, listOf(loopPlayer.name), 3))
                }
            }
        }
    }

    fun setCorePrefix(player: Player) {
        Util.log("Setting " + player.name + "'s prefix and suffix...")
        Bukkit.getScheduler().runTaskAsynchronously(LuTags.plugin) {
            if (LuTags.configuration.custom_nametags) {
                var suffix = PlaceholderAPI.setPlaceholders(player, FormatManager.getSuffixFormat(player))
                var prefix = PlaceholderAPI.setPlaceholders(player, FormatManager.getPrefixFormat(player))

                if (prefix.length > 16)
                    prefix = prefix.substring(0, 15)

                if (suffix.length > 16)
                    suffix = suffix.substring(0, 15)

                val teamName = FormatManager.getFormatPriority(player) + RandomStringUtils.random(6, true, true)
                val team = ScoreboardTeam(mainScoreboard.handle, teamName)

                team.prefix = prefix
                team.suffix = suffix

                teams[player] = teamName
                prefixes[player] = prefix
                suffixes[player] = suffix

                val version = Bukkit.getServer().javaClass.getPackage().name.split(".")[3]
                val packetPlayOutScoreboardTeamClass = Class.forName("net.minecraft.server.$version.PacketPlayOutScoreboardTeam")

                val constructorDefault = packetPlayOutScoreboardTeamClass.getConstructor()
                val constructorWithParams = packetPlayOutScoreboardTeamClass.getConstructor(ScoreboardTeam::class.java, Int::class.java)
                val constructorWithPlayerList = packetPlayOutScoreboardTeamClass.getConstructor(ScoreboardTeam::class.java, Collection::class.java, Int::class.java)

                val packetCreateTeam = constructorDefault.newInstance()
                val packetRegisterTeam = constructorWithParams.newInstance(team, 0)
                val packetAddPlayerToTeam = constructorWithPlayerList.newInstance(team, listOf(player.name), 3)

                val craftPlayerClass = Class.forName("org.bukkit.craftbukkit.$version.entity.CraftPlayer")
                val entityPlayerClass = Class.forName("net.minecraft.server.$version.EntityPlayer")
                val playerConnectionClass = Class.forName("net.minecraft.server.$version.PlayerConnection")
                val packetClass = Class.forName("net.minecraft.server.$version.Packet")

                for (target in player.world.players) {
                    val craftPlayer = craftPlayerClass.cast(target)
                    val handleMethod = craftPlayerClass.getMethod("getHandle")
                    val entityPlayer = handleMethod.invoke(craftPlayer)

                    val playerConnectionField = entityPlayerClass.getDeclaredField("playerConnection")
                    playerConnectionField.isAccessible = true
                    val playerConnection = playerConnectionField.get(entityPlayer)

                    val sendPacketMethod = playerConnectionClass.getMethod("sendPacket", packetClass)
                    sendPacketMethod.invoke(playerConnection, packetCreateTeam)
                    sendPacketMethod.invoke(playerConnection, packetRegisterTeam)
                    sendPacketMethod.invoke(playerConnection, packetAddPlayerToTeam)
                }
            }
        }
    }
}