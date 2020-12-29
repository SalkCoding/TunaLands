package com.salkcoding.tunalands.gui.render.settinggui

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.gui.render.openMainGui
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.backButton
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.times
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory

class SettingGui(private val player: Player, private val rank: Rank) : GuiInterface {

    companion object {
        val visitorSettingButton = (Material.OAK_SIGN * 1).apply {
            this.displayName("방문자 설정")
            this.lore = listOf("방문자의 권한을 설정합니다.")
        }

        val memberSettingButton = (Material.PAINTING * 1).apply {
            this.displayName("멤버 설정")
            this.lore = listOf("멤버의 권한을 설정합니다.")
        }

        val delegatorSettingButton = (Material.ITEM_FRAME * 1).apply {
            this.displayName("방문자 설정")
            this.lore = listOf("관리대리인의 권한을 설정합니다.")
        }

        val partTimeJobSettingButton = (Material.NAME_TAG * 1).apply {
            this.displayName("알바 설정")
            this.lore = listOf("알바의 권한을 설정합니다.")
        }

        val regionSettingButton = (Material.GRASS_BLOCK * 1).apply {
            this.displayName("지역 설정")
            this.lore = listOf("업데이트 예정입니다.")
        }
    }

    override fun render(inv: Inventory) {
        inv.setItem(0, backButton)
        inv.setItem(2, visitorSettingButton)
        inv.setItem(3, memberSettingButton)
        inv.setItem(4, delegatorSettingButton)
        inv.setItem(6, partTimeJobSettingButton)
        inv.setItem(7, regionSettingButton)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        when (event.rawSlot) {
            0 -> {
                player.openMainGui(rank)
            }
            2 -> {
                player.openVisitorSettingGui(rank)
            }
            3 -> {
                player.openMemberSettingGui(rank)
            }
            4 -> {
                when (rank) {
                    Rank.OWNER, Rank.DELEGATOR -> player.openDelegatorSettingGui(rank)
                    else -> player.sendMessage("You don't have a permission to access".errorFormat())
                }
            }
            6 -> {
                player.openPartTimeJobSettingGui(rank)
            }
            /*7 -> {
                TODO("NOT implemented")
            }*/
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }
}

fun Player.openSettingGui(rank: Rank) {
    val inventory = Bukkit.createInventory(null, 9, "설정")
    val gui = SettingGui(this, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}