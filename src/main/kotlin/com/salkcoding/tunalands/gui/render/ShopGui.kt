package com.salkcoding.tunalands.gui.render

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.economy
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class ShopGui(private val player: Player, private val lands: Lands, private val rank: Rank) : GuiInterface {

    //Dynamic
    private val fuel = (Material.PAPER * 1).apply {
        this.setDisplayName("${ChatColor.WHITE}연료")
        this.lore = listOf(
            "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.fuel.price}캔",
            "${ChatColor.WHITE}마을의 규모에 따라 시간적 가치가 변하는 연료이다. (1개)"
        )
        this.amount = 1
    }

    private val fuel8 = (Material.PAPER * 1).apply {
        this.setDisplayName("${ChatColor.WHITE}연료")
        this.lore = listOf(
            "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.fuel.price * 8}캔",
            "${ChatColor.WHITE}마을의 규모에 따라 시간적 가치가 변하는 연료이다. (8개)"
        )
        this.amount = 8
    }

    private val fuel16 = (Material.PAPER * 1).apply {
        this.setDisplayName("${ChatColor.WHITE}연료")
        this.lore = listOf(
            "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.fuel.price * 16}캔",
            "${ChatColor.WHITE}마을의 규모에 따라 시간적 가치가 변하는 연료이다. (16개)"
        )
        this.amount = 16
    }

    private val fuel32 = (Material.PAPER * 1).apply {
        this.setDisplayName("${ChatColor.WHITE}연료")
        this.lore = listOf(
            "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.fuel.price * 32}캔",
            "${ChatColor.WHITE}마을의 규모에 따라 시간적 가치가 변하는 연료이다. (32개)"
        )
        this.amount = 32
    }

    private val fuel64 = (Material.PAPER * 1).apply {
        this.setDisplayName("${ChatColor.WHITE}연료")
        this.lore = listOf(
            "${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.fuel.price * 64}캔",
            "${ChatColor.WHITE}마을의 규모에 따라 시간적 가치가 변하는 연료이다. (64개)"
        )
        this.amount = 64
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

        // 깃발
        inv.setItem(19, takeProtectFlagItem.clone().apply {
            val lore = this.lore!!
            lore.add("${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.flag.takeFarmFlagPrice}캔")
            this.lore = lore
        })
        inv.setItem(21, releaseProtectFlagItem.clone().apply {
            val lore = this.lore!!
            lore.add("${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.flag.releaseProtectFlagPrice}캔")
            this.lore = lore
        })
        inv.setItem(23, takeFarmFlagItem.clone().apply {
            val lore = this.lore!!
            lore.add("${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.flag.takeFarmFlagPrice}캔")
            this.lore = lore
        })
        inv.setItem(25, releaseFarmFlagItem.clone().apply {
            val lore = this.lore!!
            lore.add("${ChatColor.WHITE}가격: ${ChatColor.GOLD}${configuration.flag.takeFarmFlagPrice}캔")
            this.lore = lore
        })

        // 연료
        inv.setItem(29, fuel)
        inv.setItem(30, fuel8)
        inv.setItem(31, fuel16)
        inv.setItem(32, fuel32)
        inv.setItem(33, fuel64)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true

        when (event.rawSlot) {
            0 -> {
                player.openMainGui(lands, rank)
            }
            //Flag buy
            19 -> {
                val price = configuration.flag.takeProtectFlagPrice
                val item = takeProtectFlagItem
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${"%.2f".format(delta)}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(item)
                player.sendMessage("${item.displayName}${ChatColor.WHITE}을 구매하였습니다.".infoFormat())

                // DO NOT CHANGE MESSAGE FORMAT. LINKED WITH KIBANA
                tunaLands.logger.info("${player.name} (${player.uniqueId}) purchased take_protect_flag x1 for $price (${price} each)")
            }

            21 -> {
                val price = configuration.flag.releaseProtectFlagPrice
                val item = releaseProtectFlagItem
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${"%.2f".format(delta)}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(item)
                player.sendMessage("${item.displayName}${ChatColor.WHITE}을 구매하였습니다.".infoFormat())

                // DO NOT CHANGE MESSAGE FORMAT. LINKED WITH KIBANA
                tunaLands.logger.info("${player.name} (${player.uniqueId}) purchased release_protect_flag x1 for $price (${price} each)")
            }

            23 -> {
                val price = configuration.flag.takeFarmFlagPrice
                val item = takeFarmFlagItem
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${"%.2f".format(delta)}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(item)
                player.sendMessage("${item.displayName}${ChatColor.WHITE}을 구매하였습니다.".infoFormat())

                // DO NOT CHANGE MESSAGE FORMAT. LINKED WITH KIBANA
                tunaLands.logger.info("${player.name} (${player.uniqueId}) purchased take_farm_flag x1 for $price (${price} each)")
            }

            25 -> {
                val price = configuration.flag.releaseFarmFlagPrice
                val item = releaseFarmFlagItem
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${"%.2f".format(delta)}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(item)
                player.sendMessage("${item.displayName}${ChatColor.WHITE}을 구매하였습니다.".infoFormat())

                // DO NOT CHANGE MESSAGE FORMAT. LINKED WITH KIBANA
                tunaLands.logger.info("${player.name} (${player.uniqueId}) purchased release_farm_flag x1 for $price (${price} each)")
            }
            //Fuel buy
            29 -> {
                val price = configuration.fuel.price
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${"%.2f".format(delta)}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuelItem.apply {
                    amount = 1
                })
                player.sendMessage("${fuelItem.displayName}${ChatColor.WHITE} 1개를 구매하였습니다.".infoFormat())

                // DO NOT CHANGE MESSAGE FORMAT. LINKED WITH KIBANA
                tunaLands.logger.info("${player.name} (${player.uniqueId}) purchased fuel x1 for $price (${price / 1.0} each)")
            }

            30 -> {
                val price = configuration.fuel.price * 8
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${"%.2f".format(delta)}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuelItem.apply {
                    amount = 8
                })
                player.sendMessage("${fuelItem.displayName}${ChatColor.WHITE} 8개를 구매하였습니다.".infoFormat())

                // DO NOT CHANGE MESSAGE FORMAT. LINKED WITH KIBANA
                tunaLands.logger.info("${player.name} (${player.uniqueId}) purchased fuel x8 for $price (${price / 8.0} each)")
            }

            31 -> {
                val price = configuration.fuel.price * 16
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${"%.2f".format(delta)}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuelItem.apply {
                    amount = 16
                })
                player.sendMessage("${fuelItem.displayName}${ChatColor.WHITE} 16개를 구매하였습니다.".infoFormat())

                // DO NOT CHANGE MESSAGE FORMAT. LINKED WITH KIBANA
                tunaLands.logger.info("${player.name} (${player.uniqueId}) purchased fuel x16 for $price (${price / 16.0} each)")
            }

            32 -> {
                val price = configuration.fuel.price * 32
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${"%.2f".format(delta)}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuelItem.apply {
                    amount = 32
                })
                player.sendMessage("${fuelItem.displayName}${ChatColor.WHITE} 32개를 구매하였습니다.".infoFormat())

                // DO NOT CHANGE MESSAGE FORMAT. LINKED WITH KIBANA
                tunaLands.logger.info("${player.name} (${player.uniqueId}) purchased fuel x32 for $price (${price / 32.0} each)")
            }

            33 -> {
                val price = configuration.fuel.price * 64
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${"%.2f".format(delta)}캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                player.giveOrDrop(fuelItem.apply {
                    amount = 64
                })
                player.sendMessage("${fuelItem.displayName}${ChatColor.WHITE} 64개를 구매하였습니다.".infoFormat())

                // DO NOT CHANGE MESSAGE FORMAT. LINKED WITH KIBANA
                tunaLands.logger.info("${player.name} (${player.uniqueId}) purchased fuel x64 for $price (${price / 64.0} each)")
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