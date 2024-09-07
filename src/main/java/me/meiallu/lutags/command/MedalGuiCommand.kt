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

class MedalGuiCommand : CommandExecutor, Listener {

    @EventHandler
    fun onMove(event: InventoryMoveItemEvent) {
        val menuTitle = LuTags.configuration.menus["titles"]!!["medal"]
        val inventory = event.destination

        if (inventory.name == menuTitle) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteract(event: InventoryInteractEvent) {
        val menuTitle = LuTags.configuration.menus["titles"]!!["medal"]
        val inventory = event.inventory

        if (inventory.name == menuTitle) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent) {
        val menuTitle = LuTags.configuration.menus["titles"]!!["medal"]
        val inventory = event.inventory

        if (inventory.name == menuTitle) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPickup(event: InventoryPickupItemEvent) {
        val menuTitle = LuTags.configuration.menus["titles"]!!["medal"]
        val inventory = event.inventory

        if (inventory.name == menuTitle) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val inventory = event.clickedInventory
        val menuTitle = LuTags.configuration.menus["titles"]!!["medal"]

        if (inventory.name == menuTitle) {
            event.isCancelled = true

            val materialName = LuTags.configuration.menus["items"]!!["medal"]!!
            val material = Material.getMaterial(materialName)

            val removeName = LuTags.configuration.menus["items"]!!["remove_medal"]!!
            val removeMaterial = Material.getMaterial(removeName)

            when (event.currentItem.type) {
                material -> {
                    val hashMap = HashMap<String, Int>()

                    val medals = LuTags.medals.ordered
                    var indice = 10

                    for (i in medals.indices) {
                        val medal = medals[i]

                        if (player.hasPermission(medal.permission)) {
                            if (indice == event.slot) {
                                player.performCommand("medal " + medal.name)
                                player.closeInventory()
                                return
                            }

                            hashMap[medal.name] = indice
                            indice++

                            if (indice == 17 || indice == 26 || indice == 35)
                                indice += 2
                        }
                    }
                }

                removeMaterial -> {
                    player.performCommand("medal " + Medal.defaultMedal.name)
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

        val menuTitle = LuTags.configuration.menus["titles"]!!["medal"]
        val inventory = Bukkit.createInventory(player, 6 * 9, menuTitle)

        val medals = LuTags.medals.ordered
        var hasPermission = false
        var indice = 10

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

        for (i in medals.indices) {
            val medal = medals[i]

            if (player.hasPermission(medal.permission)) {
                val materialName = LuTags.configuration.menus["items"]!!["medal"]!!
                val material = Material.getMaterial(materialName)

                val itemFormat = LuTags.configuration.menus["names"]!!["medal"]!!
                val itemLore = LuTags.configuration.menus["lore"]!!["medal"]!!
                val itemName = Util.parsePlaceholders(player, Tag.getPlayerTag(uuid), medal, itemFormat)

                val builder = ItemBuilder(material, itemName, itemLore)
                inventory.setItem(indice, builder.getStack())

                indice++

                if (indice == 17 || indice == 26 || indice == 35)
                    indice += 2
            }
        }

        val removeMedalMaterialName = LuTags.configuration.menus["items"]!!["remove_medal"]!!
        val removeMedalName = LuTags.configuration.menus["names"]!!["remove_medal"]!!
        val removeMedalLore = LuTags.configuration.menus["lore"]!!["remove_medal"]!!
        val removeMedalSlot = LuTags.configuration.menus["position"]!!["remove_medal"]!!.toIntOrNull()

        val removeMedalMaterial = Material.getMaterial(removeMedalMaterialName)
        val removeMedalBuilder = ItemBuilder(removeMedalMaterial, removeMedalName, removeMedalLore)

        inventory.setItem(removeMedalSlot ?: 0, removeMedalBuilder.getStack())

        player.openInventory(inventory)
        Util.log("Successfully executed the command.")
        return true
    }
}