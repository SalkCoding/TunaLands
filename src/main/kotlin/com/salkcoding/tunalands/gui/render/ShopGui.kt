package com.salkcoding.tunalands.gui.render

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.economy
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class ShopGui(private val player: Player, private val lands: Lands, private val rank: Rank) : GuiInterface {

    companion object {

        val fuel = (Material.PAPER * 1).apply {
            this.setDisplayName("${ChatColor.WHITE}연료")
            this.lore = listOf(
                "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.fuel.price}캔",
                "${ChatColor.WHITE}마을의 규모에 따라 시간적 가치가 변하는 연료이다. (1개)"
            )
            this.amount = 1
        }

        val fuel8 = (Material.PAPER * 1).apply {
            this.setDisplayName("${ChatColor.WHITE}연료")
            this.lore = listOf(
                "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.fuel.price * 8}캔",
                "${ChatColor.WHITE}마을의 규모에 따라 시간적 가치가 변하는 연료이다. (8개)"
            )
            this.amount = 8
        }

        val fuel16 = (Material.PAPER * 1).apply {
            this.setDisplayName("${ChatColor.WHITE}연료")
            this.lore = listOf(
                "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.fuel.price * 16}캔",
                "${ChatColor.WHITE}마을의 규모에 따라 시간적 가치가 변하는 연료이다. (16개)"
            )
            this.amount = 16
        }

        val fuel32 = (Material.PAPER * 1).apply {
            this.setDisplayName("${ChatColor.WHITE}연료")
            this.lore = listOf(
                "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.fuel.price * 32}캔",
                "${ChatColor.WHITE}마을의 규모에 따라 시간적 가치가 변하는 연료이다. (32개)"
            )
            this.amount = 32
        }

        val fuel64 = (Material.PAPER * 1).apply {
            this.setDisplayName("${ChatColor.WHITE}연료")
            this.lore = listOf(
                "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.fuel.price * 64}캔",
                "${ChatColor.WHITE}마을의 규모에 따라 시간적 가치가 변하는 연료이다. (64개)"
            )
            this.amount = 64
        }

        val releaseFlag = (Material.RED_BANNER * 1).apply {
            this.setDisplayName("${ChatColor.RED}제거 ${ChatColor.WHITE}깃발")
            this.lore = listOf(
                "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.flag.releaseFlagPrice}캔",
                "${ChatColor.WHITE}제거하고 싶은 지역에 설치하여 제거할 수 있는 깃발입니다."
            )
        }

        val takeFlag = (Material.GREEN_BANNER * 1).apply {
            this.setDisplayName("${ChatColor.GREEN}점유 ${ChatColor.WHITE}깃발")
            this.lore = listOf(
                "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.flag.takeFlagPrice}캔",
                "${ChatColor.WHITE}늘리고 싶은 지역에 설치하여 점유할 수 있는 깃발입니다."
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

        // 연료
        inv.setItem(29, fuel)
        inv.setItem(30, fuel8)
        inv.setItem(31, fuel16)
        inv.setItem(32, fuel32)
        inv.setItem(33, fuel64)

        // 점유, 제거 깃발
        inv.setItem(20, takeFlag)
        inv.setItem(21, releaseFlag)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true

        when (event.rawSlot) {
            0 -> {
                player.openMainGui(lands, rank)
            }
            20 -> {
                val price = configuration.flag.takeFlagPrice
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${delta}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(takeFlag)
            }
            21 -> {
                val price = configuration.flag.releaseFlagPrice
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${delta}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(releaseFlag)
            }
            29 -> {
                val price = configuration.fuel.price
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${delta}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuel)
            }
            30 -> {
                val price = configuration.fuel.price * 8
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${delta}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuel8)
            }
            31 -> {
                val price = configuration.fuel.price * 16
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${delta}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuel16)
            }
            32 -> {
                val price = configuration.fuel.price * 32
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${delta}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuel32)
            }
            33 -> {
                val price = configuration.fuel.price * 64
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${delta}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuel64)
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