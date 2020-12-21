package com.salkcoding.tunalands.gui.render.settinggui

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.gui.render.openMainGui
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.backButton
import com.salkcoding.tunalands.util.times
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory

class DelegatorSettingGui(private val player: Player, private val rank: Rank) : GuiInterface {

    private val setSpawnVisitor = (Material.NETHER_STAR * 1).apply { this.displayName("방문자 스폰 설정") }
    private val setPartTimeJobSetting = (Material.BOOK * 1).apply { this.displayName("알바 설정 수정") }
    private val setMemberSetting = (Material.PAINTING * 1).apply { this.displayName("멤버 설정 수정") }
    private val setVisitorSetting = (Material.GRASS_BLOCK * 1).apply { this.displayName("방문자 설정 수정") }
    private val setRegionSetting = (Material.IRON_DOOR * 1).apply { this.displayName("지역 설정 수정") }
    private val setVisitorBan = (Material.CRIMSON_SIGN * 1).apply { this.displayName("방문자 밴 관리") }
    private val setRegionSpawn = (Material.EMERALD * 1).apply { this.displayName("지역 스폰 설정") }

    override fun render(inv: Inventory) {
        val setting = landManager.getLandDelegatorSetting(player.uniqueId)!!

        //First row
        setSpawnVisitor.apply { this.lore = listOf("상태: ${setting.setSpawnVisitor}") }
        setPartTimeJobSetting.apply { this.lore = listOf("상태: ${setting.setPartTimeJobSetting}") }
        setMemberSetting.apply { this.lore = listOf("상태: ${setting.setMemberSetting}") }
        setVisitorSetting.apply { this.lore = listOf("상태: ${setting.setVisitorSetting}") }
        setRegionSetting.apply { this.lore = listOf("상태: ${setting.setRegionSetting}") }
        setVisitorBan.apply { this.lore = listOf("상태: ${setting.setVisitorBan}") }
        setRegionSpawn.apply { this.lore = listOf("상태: ${setting.setRegionSpawn}") }

        inv.setItem(0, backButton)
        inv.setItem(8, backButton)

        inv.setItem(9, setSpawnVisitor)
        inv.setItem(10, setPartTimeJobSetting)
        inv.setItem(11, setMemberSetting)
        inv.setItem(12, setVisitorSetting)
        inv.setItem(13, setRegionSetting)
        inv.setItem(14, setVisitorBan)
        inv.setItem(15, setRegionSpawn)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val setting = landManager.getLandDelegatorSetting(player.uniqueId)!!
        val inv = event.inventory
        when (event.rawSlot) {
            0, 8 -> player.openSettingGui(rank)//Back button
            //First row
            9 -> {
                setting.setSpawnVisitor = !setting.setSpawnVisitor
                setSpawnVisitor.apply {
                    this.lore = listOf("상태: ${setting.setSpawnVisitor}")
                    inv.setItem(9, this)
                }
            }
            10 -> {
                setting.setPartTimeJobSetting = !setting.setPartTimeJobSetting
                setPartTimeJobSetting.apply {
                    this.lore = listOf("상태: ${setting.setPartTimeJobSetting}")
                    inv.setItem(10, this)
                }
            }
            11 -> {
                setting.setMemberSetting = !setting.setMemberSetting
                setMemberSetting.apply {
                    this.lore = listOf("상태: ${setting.setMemberSetting}")
                    inv.setItem(11, this)
                }
            }
            12 -> {
                setting.setVisitorSetting = !setting.setVisitorSetting
                setVisitorSetting.apply {
                    this.lore = listOf("상태: ${setting.setVisitorSetting}")
                    inv.setItem(12, this)
                }
            }
            13 -> {
                setting.setRegionSetting = !setting.setRegionSetting
                setRegionSetting.apply {
                    this.lore = listOf("상태: ${setting.setRegionSetting}")
                    inv.setItem(13, this)
                }
            }
            14 -> {
                setting.setVisitorBan = !setting.setVisitorBan
                setVisitorBan.apply {
                    this.lore = listOf("상태: ${setting.setVisitorBan}")
                    inv.setItem(14, this)
                }
            }
            15 -> {
                setting.setRegionSpawn = !setting.setRegionSpawn
                setRegionSpawn.apply {
                    this.lore = listOf("상태: ${setting.setRegionSpawn}")
                    inv.setItem(15, this)
                }
            }
        }
    }

    override fun onDrag(event: InventoryDragEvent) {
        event.isCancelled = true
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }
}

fun Player.openDelegatorSettingGui(rank: Rank) {
    val inventory = Bukkit.createInventory(null, 18, "관리대리인 설정")
    val gui = DelegatorSettingGui(this, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}