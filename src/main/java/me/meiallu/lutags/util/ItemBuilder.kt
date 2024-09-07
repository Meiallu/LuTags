package me.meiallu.lutags.util

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

class ItemBuilder {

    private var stack: ItemStack? = null

    fun getStack(): ItemStack? {
        return stack
    }

    fun setMaterial(type: Material): ItemBuilder {
        stack = ItemStack(type)
        return this
    }

    fun setType(type: Material): ItemBuilder {
        stack = ItemStack(type)
        stack?.type = type
        return this
    }

    fun setAmount(amount: Int): ItemBuilder {
        stack?.amount = amount
        return this
    }

    fun setDurability(durability: Int): ItemBuilder {
        stack?.durability = durability.toShort()
        return this
    }

    fun setName(name: String): ItemBuilder {
        val meta = stack?.itemMeta
        meta?.displayName = name

        stack?.setItemMeta(meta)
        return this
    }

    fun setDescription(vararg desc: String): ItemBuilder {
        val meta = stack?.itemMeta
        meta?.lore = listOf(*desc)

        stack?.setItemMeta(meta)
        return this
    }

    fun setDescription(desc: List<String>): ItemBuilder {
        val meta = stack?.itemMeta
        meta?.lore = desc

        stack?.setItemMeta(meta)
        return this
    }

    fun setEnchant(enchant: Array<Enchantment>, level: IntArray): ItemBuilder {
        for (i in enchant.indices)
            stack?.addUnsafeEnchantment(enchant[i], level[i])

        return this
    }

    fun setEnchant(enchant: Enchantment, level: Int): ItemBuilder {
        stack?.addUnsafeEnchantment(enchant, level)
        return this
    }

    fun setBreakable(breakable: Boolean): ItemBuilder {
        val meta = stack?.itemMeta
        meta?.spigot()?.isUnbreakable = !breakable

        stack?.setItemMeta(meta)
        return this
    }

    fun build(player: Player, vararg slot: Int): ItemBuilder {
        build(player.inventory, *slot)
        player.updateInventory()
        return this
    }

    fun build(player: Player): ItemBuilder {
        player.inventory.addItem(stack)
        player.updateInventory()
        return this
    }

    fun build(inventory: Inventory, vararg slot: Int): ItemBuilder {
        for (slots in slot) inventory.setItem(slots, stack)

        return this
    }

    fun build(inventory: Inventory): ItemBuilder {
        inventory.addItem(stack)
        return this
    }

    fun setName(stack: ItemStack, name: String): ItemMeta {
        val meta = stack.itemMeta
        meta.displayName = name
        return meta
    }

    fun setSkull(owner: String): ItemBuilder {
        val meta = stack?.itemMeta as SkullMeta
        meta.setOwner(owner)

        stack?.setItemMeta(meta)
        return this
    }

    fun setSkullTexture(texture: String): ItemBuilder {
        val skullMeta = stack?.itemMeta as SkullMeta
        val profile = GameProfile(UUID.randomUUID(), null)

        try {
            profile.properties.put("textures", Property("textures", texture))
            val profileField = skullMeta.javaClass.getDeclaredField("profile")

            profileField.isAccessible = true
            profileField[skullMeta] = profile
        } catch (exception: NoSuchFieldException) {
            throw RuntimeException(exception)
        } catch (exception: IllegalAccessException) {
            throw RuntimeException(exception)
        }

        stack?.setItemMeta(skullMeta)
        return this
    }

    fun setColor(color: Color): ItemBuilder {
        val armorMeta = stack?.itemMeta as LeatherArmorMeta
        armorMeta.color = color

        stack?.setItemMeta(armorMeta)
        return this
    }

    fun chanceItemStack(itemStack: ItemStack?): ItemBuilder {
        this.stack = itemStack
        return this
    }

    fun checkItem(item: ItemStack, display: String): Boolean {
        return (item.type != Material.AIR && item.hasItemMeta() && item.itemMeta.hasDisplayName() && item.itemMeta.displayName.equals(
            display,
            ignoreCase = true
        ))
    }

    constructor()

    constructor(type: Material, name: String, vararg lores: String) {
        setMaterial(type)
        setName(name)
        setDescription(*lores)
    }

    constructor(type: Material, name: String, data: Int) {
        setMaterial(type)
        setName(name)
        setDurability(data)
    }

    constructor(type: Material) {
        setMaterial(type)
    }

    constructor(type: Material, name: String) {
        setMaterial(type)
        setName(name)
    }
}