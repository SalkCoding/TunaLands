package com.salkcoding.tunalands.gui.render

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.backButton
import com.salkcoding.tunalands.util.blackPane
import com.salkcoding.tunalands.util.giveOrDrop
import com.salkcoding.tunalands.util.times
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class ShopGui(private val player: Player, private val rank: Rank) : GuiInterface {

    companion object {
        val takeFlag = (Material.GREEN_BANNER * 1).apply {
            this.displayName("점유 깃발")
            this.lore = listOf(
                "가격: ${configuration.flag.takeFlagPrice}캔",
                "늘리고 싶은 지역에 설치하여 점유할 수 있는 깃발입니다."
            )
        }

        val releaseFlag = (Material.RED_BANNER * 1).apply {
            this.displayName("제거 깃발")
            this.lore = listOf(
                "가격: ${configuration.flag.releaseFlagPrice}캔",
                "제거하고 싶은 지역에 설치하여 제거할 수 있는 깃발입니다."
            )
        }

        private val fuel30Minutes = (Material.PAPER * 1).apply {
            this.displayName("연료")
            this.lore = listOf(
                "가격: ${30}캔",
                "30분 동안 유지되는 연료이다."
            )
        }

        private val fuel1Hour = (Material.PAPER * 1).apply {
            this.displayName("연료")
            this.lore = listOf(
                "가격: ${50}캔",
                "1시간 동안 유지되는 연료이다."
            )
        }

        private val fuel6Hours = (Material.PAPER * 1).apply {
            this.displayName("연료")
            this.lore = listOf(
                "가격: ${290}캔",
                "6시간 동안 유지되는 연료이다."
            )
        }

        private val fuel12Hours = (Material.PAPER * 1).apply {
            this.displayName("연료")
            this.lore = listOf(
                "가격: ${570}캔",
                "12시간 동안 유지되는 연료이다."
            )
        }

        private val fuel24Hours = (Material.PAPER * 1).apply {
            this.displayName("연료")
            this.lore = listOf(
                "가격: ${1100}캔",
                "24시간 동안 유지되는 연료이다."
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
        inv.setItem(29, fuel30Minutes)
        inv.setItem(30, fuel1Hour)
        inv.setItem(31, fuel6Hours)
        inv.setItem(32, fuel12Hours)
        inv.setItem(33, fuel24Hours)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true

        when (event.rawSlot) {
            0 -> {
                player.openMainGui(rank)
            }
            //TODO paying code
            20 -> {
                when (rank) {
                    Rank.OWNER, Rank.DELEGATOR -> {
                        player.giveOrDrop(takeFlag)
                    }
                    else -> {
                        player.sendMessage("권한이 없습니다!")
                    }
                }
            }
            21 -> {
                when (rank) {
                    Rank.OWNER, Rank.DELEGATOR -> {
                        player.giveOrDrop(releaseFlag)
                    }
                    else -> {
                        player.sendMessage("권한이 없습니다!")
                    }
                }
            }
            29 -> {
                player.giveOrDrop(fuel30Minutes)
            }
            30 -> {
                player.giveOrDrop(fuel1Hour)
            }
            31 -> {
                player.giveOrDrop(fuel6Hours)
            }
            32 -> {
                player.giveOrDrop(fuel12Hours)
            }
            33 -> {
                player.giveOrDrop(fuel24Hours)
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