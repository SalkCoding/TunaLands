package com.salkcoding.tunalands.gui.render

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.backButton
import com.salkcoding.tunalands.util.blackPane
import com.salkcoding.tunalands.util.times
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory

class ShopGui(private val player: Player, private val rank: Rank) : GuiInterface {

    companion object {
        private val takeFlag = (Material.PAPER * 1).apply {
            this.displayName("점유 깃발")
            this.lore = listOf(
                "가격: ${configuration.flag.takeFlagPrice}캔",
                "늘리고 싶은 지역에 설치하여 점유할 수 있는 깃발입니다."
            )
        }

        private val releaseFlag = (Material.PAPER * 1).apply {
            this.displayName("제거 깃발")
            this.lore = listOf(
                "가격: ${configuration.flag.takeFlagPrice}캔",
                "제거하고 싶은 지역에 설치하여 제거할 수 있는 깃발입니다."
            )
        }

        private val fuel = (Material.GLOBE_BANNER_PATTERN * 1).apply {
            this.displayName("연료")
            this.lore = listOf(
                "가격: ${15}캔",
                "30분 동안 유지되는 연료이다."
            )
        }
    }

    override fun render(inv: Inventory) {
        for (i in 1..9) {
            inv.setItem(i, blackPane)
        }

        for (i in 17..45 step 9) {
            inv.setItem(i, blackPane)
            inv.setItem(i + 1, blackPane)
        }

        for (i in 45..53) {
            inv.setItem(i, blackPane)
        }

        inv.setItem(0, backButton)

        inv.setItem(20, takeFlag)
        inv.setItem(21, releaseFlag)
        inv.setItem(29, fuel)
        inv.setItem(30, fuel)
        inv.setItem(31, fuel)
        inv.setItem(32, fuel)
        inv.setItem(33, fuel)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true

        when (event.rawSlot) {
            0 -> {
                player.openMainGui(rank)
            }
            //TODO buying code
            20 -> {

            }
            21 -> {

            }
            29 -> {

            }
            30 -> {

            }
            31 -> {

            }
            32 -> {

            }
            33 -> {

            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }

}

fun Player.openShopGui(rank: Rank) {
    val inventory = Bukkit.createInventory(null, 54, "상점")
    val gui = ShopGui(this, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}