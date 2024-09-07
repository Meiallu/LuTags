package me.meiallu.lutags.command

import me.meiallu.lutags.LuTags
import me.meiallu.lutags.data.Medal
import me.meiallu.lutags.data.Tag
import me.meiallu.lutags.util.ItemBuilder
import me.meiallu.lutags.util.Util
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*

class TagGuiCommand : CommandExecutor, Listener {

    @EventHandler
    fun onMove(event: InventoryMoveItemEvent) {
        val menuTitle = LuTags.configuration.menus["titles"]!!["tag"]
        val inventory = event.destination

        if (inventory.name == menuTitle) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteract(event: InventoryInteractEvent) {
        val menuTitle = LuTags.configuration.menus["titles"]!!["tag"]
        val inventory = event.inventory

        if (inventory.name == menuTitle) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent) {
        val menuTitle = LuTags.configuration.menus["titles"]!!["tag"]
        val inventory = event.inventory

        if (inventory.name == menuTitle) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPickup(event: InventoryPickupItemEvent) {
        val menuTitle = LuTags.configuration.menus["titles"]!!["tag"]
        val inventory = event.inventory

        if (inventory.name == menuTitle) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val inventory = event.clickedInventory
        val menuTitle = LuTags.configuration.menus["titles"]!!["tag"]

        if (inventory.name == menuTitle) {
            event.isCancelled = true

            val materialName = LuTags.configuration.menus["items"]!!["tag"]!!
            val material = Material.getMaterial(materialName)

            val removeName = LuTags.configuration.menus["items"]!!["remove_tag"]!!
            val removeMaterial = Material.getMaterial(removeName)

            when (event.currentItem.type) {
                material -> {
                    val hashMap = HashMap<String, Int>()

                    val tags = LuTags.tags.ordered
                    var indice = 10

                    for (i in tags.indices) {
                        val tag = tags[i]

                        if (player.hasPermission(tag.permission)) {
                            if (indice == event.slot) {
                                player.performCommand("tag " + tag.name)
                                player.closeInventory()
                                return
                            }

                            hashMap[tag.name] = indice
                            indice++

                            if (indice == 17 || indice == 26 || indice == 35)
                                indice += 2
                        }
                    }
                }

                removeMaterial -> {
                    player.performCommand("tag " + Tag.defaultTag.name)
                    player.closeInventory()
                }

                else -> {}
            }
        }
    }

    override fun onCommand(sender: CommandSender, command: Command?, label: String?, args: Array<String?>): Boolean {
        Util.log("Trying to execute \"/" + label + "\" to " + sender.name)

        if (sender is ConsoleCommandSender) {
            sender.sendMessage(LuTags.configuration.only_players)
            return false
        }

        val player = sender as Player
        val uuid = player.uniqueId.toString()

        val menuTitle = LuTags.configuration.menus["titles"]!!["tag"]
        val inventory = Bukkit.createInventory(player, 6 * 9, menuTitle)

        val tags = LuTags.tags.ordered
        var hasPermission = false
        var indice = 10

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

        for (i in tags.indices) {
            val tag = tags[i]

            if (player.hasPermission(tag.permission)) {
                val materialName = LuTags.configuration.menus["items"]!!["tag"]!!
                val material = Material.getMaterial(materialName)

                val itemFormat = LuTags.configuration.menus["names"]!!["tag"]!!
                val itemLore = LuTags.configuration.menus["lore"]!!["tag"]!!
                val itemName = Util.parsePlaceholders(player, tag, Medal.getPlayerMedal(uuid), itemFormat)

                val builder = ItemBuilder(material, itemName, itemLore)
                inventory.setItem(indice, builder.getStack())

                indice++

                if (indice == 17 || indice == 26 || indice == 35)
                    indice += 2
            }
        }

        val removeTagMaterialName = LuTags.configuration.menus["items"]!!["remove_tag"]!!
        val removeTagName = LuTags.configuration.menus["names"]!!["remove_tag"]!!
        val removeTagLore = LuTags.configuration.menus["lore"]!!["remove_tag"]!!
        val removeTagSlot = LuTags.configuration.menus["position"]!!["remove_tag"]!!.toIntOrNull()

        val removeTagMaterial = Material.getMaterial(removeTagMaterialName)
        val removeTagBuilder = ItemBuilder(removeTagMaterial, removeTagName, removeTagLore)

        inventory.setItem(removeTagSlot ?: 0, removeTagBuilder.getStack())

        player.openInventory(inventory)
        Util.log("Successfully executed the command.")
        return true
    }
}