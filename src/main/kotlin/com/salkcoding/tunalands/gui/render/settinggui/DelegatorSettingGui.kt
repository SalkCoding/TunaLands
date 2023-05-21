package com.salkcoding.tunalands.gui.render.settinggui


import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.gui.render.backButton
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.times
import com.salkcoding.tunalands.util.toColoredText
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class DelegatorSettingGui(private val player: Player, private val lands: Lands, private val rank: Rank) : GuiInterface {

    private val canSetVisitorSetting = (Material.NETHER_STAR * 1).apply { this.setDisplayName("${ChatColor.WHITE}방문자 권한 설정 권한") }
    private val canSetPartTimeJobSetting = (Material.BOOK * 1).apply { this.setDisplayName("${ChatColor.WHITE}알바 권한 설정 권한") }
    private val canSetMemberSetting = (Material.PAINTING * 1).apply { this.setDisplayName("${ChatColor.WHITE}멤버 권한 설정 권한") }
    private val canSetRegionSetting = (Material.GRASS_BLOCK * 1).apply { this.setDisplayName("${ChatColor.WHITE}지역 설정 권한") }
    private val canSetSpawn = (Material.IRON_DOOR * 1).apply { this.setDisplayName("${ChatColor.WHITE}스폰 설정 권한") }
    private val canBan = (Material.CRIMSON_SIGN * 1).apply { this.setDisplayName("${ChatColor.WHITE}밴 권한") }
    override fun render(inv: Inventory) {
        val setting = lands.delegatorSetting

        //First row

        canSetVisitorSetting.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSetVisitorSetting.toColoredText()}") }
        canSetPartTimeJobSetting.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSetPartTimeJobSetting.toColoredText()}") }
        canSetMemberSetting.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSetMemberSetting.toColoredText()}") }
        canSetRegionSetting.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSetRegionSetting.toColoredText()}") }
        canSetSpawn.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSetSpawn.toColoredText()}") }
        canBan.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canBan.toColoredText()}") }

        inv.setItem(0, backButton)
        inv.setItem(8, backButton)

        inv.setItem(9, canSetVisitorSetting)
        inv.setItem(10, canSetPartTimeJobSetting)
        inv.setItem(11, canSetMemberSetting)
        inv.setItem(12, canSetRegionSetting)
        inv.setItem(13, canSetSpawn)
        inv.setItem(14, canBan)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        if (event.action != InventoryAction.PICKUP_ALL && event.action != InventoryAction.PICKUP_HALF)
            return

        val setting = lands.delegatorSetting
        val inv = event.inventory
        when (event.rawSlot) {
            0, 8 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                player.openSettingGui(lands, rank)//Back button
            }
            //First row
            9 -> {
                setting.canSetVisitorSetting = !setting.canSetVisitorSetting
                canSetVisitorSetting.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSetVisitorSetting.toColoredText()}")
                    inv.setItem(9, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            10 -> {
                setting.canSetPartTimeJobSetting = !setting.canSetPartTimeJobSetting
                canSetPartTimeJobSetting.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSetPartTimeJobSetting.toColoredText()}")
                    inv.setItem(10, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            11 -> {
                setting.canSetMemberSetting = !setting.canSetMemberSetting
                canSetMemberSetting.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSetMemberSetting.toColoredText()}")
                    inv.setItem(11, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            12 -> {
                setting.canSetRegionSetting = !setting.canSetRegionSetting
                canSetRegionSetting.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSetRegionSetting.toColoredText()}")
                    inv.setItem(12, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            13 -> {
                setting.canSetSpawn = !setting.canSetSpawn
                canSetSpawn.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSetSpawn.toColoredText()}")
                    inv.setItem(13, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            14 -> {
                setting.canBan = !setting.canBan
                canBan.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canBan.toColoredText()}")
                    inv.setItem(14, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }
}

fun Player.openDelegatorSettingGui(lands: Lands, rank: Rank) {
    val inventory = Bukkit.createInventory(null, 18, "관리대리인 설정")
    val gui = DelegatorSettingGui(this, lands, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}