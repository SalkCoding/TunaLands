package com.salkcoding.tunalands.gui.render

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.economy
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class ShopGui(private val player: Player, private val lands: Lands, private val rank: Rank) : GuiInterface {

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
                "가격: ${configuration.fuel.m30}캔",
                "30분 동안 유지되는 연료이다."
            )
        }

        private val fuel1Hour = (Material.PAPER * 1).apply {
            this.displayName("연료")
            this.lore = listOf(
                "가격: ${configuration.fuel.h1}캔",
                "1시간 동안 유지되는 연료이다."
            )
        }

        private val fuel6Hours = (Material.PAPER * 1).apply {
            this.displayName("연료")
            this.lore = listOf(
                "가격: ${configuration.fuel.h6}캔",
                "6시간 동안 유지되는 연료이다."
            )
        }

        private val fuel12Hours = (Material.PAPER * 1).apply {
            this.displayName("연료")
            this.lore = listOf(
                "가격: ${configuration.fuel.h12}캔",
                "12시간 동안 유지되는 연료이다."
            )
        }

        private val fuel24Hours = (Material.PAPER * 1).apply {
            this.displayName("연료")
            this.lore = listOf(
                "가격: ${configuration.fuel.h24}캔",
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
                player.openMainGui(lands, rank)
            }
            20 -> {
                when (rank) {
                    Rank.OWNER, Rank.DELEGATOR -> {
                        val price = configuration.flag.takeFlagPrice.toDouble()
                        if (player.hasEnoughMoney(price)) {
                            player.sendMessage("캔이 부족합니다.".errorFormat())
                            return
                        }
                        economy.withdrawPlayer(player, price)

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
                        val price = configuration.flag.releaseFlagPrice.toDouble()
                        if (player.hasEnoughMoney(price)) {
                            player.sendMessage("캔이 부족합니다.".errorFormat())
                            return
                        }
                        economy.withdrawPlayer(player, price)

                        player.giveOrDrop(releaseFlag)
                    }
                    else -> {
                        player.sendMessage("권한이 없습니다!")
                    }
                }
            }
            29 -> {
                val price = configuration.fuel.m30.toDouble()
                if (player.hasEnoughMoney(price)) {
                    player.sendMessage("캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuel30Minutes)
            }
            30 -> {
                val price = configuration.fuel.h1.toDouble()
                if (player.hasEnoughMoney(price)) {
                    player.sendMessage("캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuel1Hour)
            }
            31 -> {
                val price = configuration.fuel.h6.toDouble()
                if (player.hasEnoughMoney(price)) {
                    player.sendMessage("캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuel6Hours)
            }
            32 -> {
                val price = configuration.fuel.h12.toDouble()
                if (player.hasEnoughMoney(price)) {
                    player.sendMessage("캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuel12Hours)
            }
            33 -> {
                val price = configuration.fuel.h24.toDouble()
                if (player.hasEnoughMoney(price)) {
                    player.sendMessage("캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuel24Hours)
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }

}

fun Player.openShopGui(lands: Lands, rank: Rank) {
    val inventory = Bukkit.createInventory(null, 54, "상점")
    val gui = ShopGui(this, lands, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}