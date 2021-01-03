package com.salkcoding.tunalands.gui.render.settinggui

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.backButton
import com.salkcoding.tunalands.util.times
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class DelegatorSettingGui(private val player: Player, private val lands: Lands, private val rank: Rank) : GuiInterface {

    private val canSetVisitorSetting = (Material.NETHER_STAR * 1).apply { this.displayName("방문자 권한 설정 권한") }
    private val canSetPartTimeJobSetting = (Material.BOOK * 1).apply { this.displayName("알바 권한 설정 권한") }
    private val canSetMemberSetting = (Material.PAINTING * 1).apply { this.displayName("멤버 권한 설정 권한") }
    private val canSetRegionSetting = (Material.GRASS_BLOCK * 1).apply { this.displayName("지역 설정 권한") }
    private val canSetSpawn = (Material.IRON_DOOR * 1).apply { this.displayName("스폰 설정 권한") }
    private val canBan = (Material.CRIMSON_SIGN * 1).apply { this.displayName("밴 권한") }
    override fun render(inv: Inventory) {
        val setting = lands.delegatorSetting

        //First row

        canSetVisitorSetting.apply { this.lore = listOf("상태: ${setting.canSetVisitorSetting}") }
        canSetPartTimeJobSetting.apply { this.lore = listOf("상태: ${setting.canSetPartTimeJobSetting}") }
        canSetMemberSetting.apply { this.lore = listOf("상태: ${setting.canSetMemberSetting}") }
        canSetRegionSetting.apply { this.lore = listOf("상태: ${setting.canSetRegionSetting}") }
        canSetSpawn.apply { this.lore = listOf("상태: ${setting.canSetSpawn}") }
        canBan.apply { this.lore = listOf("상태: ${setting.canBan}") }

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
                    this.lore = listOf("상태: ${setting.canSetVisitorSetting}")
                    inv.setItem(9, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            10 -> {
                setting.canSetPartTimeJobSetting = !setting.canSetPartTimeJobSetting
                canSetPartTimeJobSetting.apply {
                    this.lore = listOf("상태: ${setting.canSetPartTimeJobSetting}")
                    inv.setItem(10, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            11 -> {
                setting.canSetMemberSetting = !setting.canSetMemberSetting
                canSetMemberSetting.apply {
                    this.lore = listOf("상태: ${setting.canSetMemberSetting}")
                    inv.setItem(11, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            12 -> {
                setting.canSetRegionSetting = !setting.canSetRegionSetting
                canSetRegionSetting.apply {
                    this.lore = listOf("상태: ${setting.canSetRegionSetting}")
                    inv.setItem(12, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            13 -> {
                setting.canSetSpawn = !setting.canSetSpawn
                canSetSpawn.apply {
                    this.lore = listOf("상태: ${setting.canSetSpawn}")
                    inv.setItem(13, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            14 -> {
                setting.canBan = !setting.canBan
                canBan.apply {
                    this.lore = listOf("상태: ${setting.canBan}")
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