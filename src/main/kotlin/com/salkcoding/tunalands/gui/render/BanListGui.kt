package com.salkcoding.tunalands.gui.render

import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.math.min

class BanListGui(
    private val player: Player,
    private val lands: Lands,
    private val callByCommand: Boolean,
    private val rank: Rank
) : GuiInterface {

    private lateinit var playerList: List<UUID>

    private val sortButton = (Material.HOPPER * 1).apply {
        this.setDisplayName("${ChatColor.WHITE}정렬 방법 선택")
    }

    private val statisticsInfo = (Material.PAINTING * 1).apply {
        this.setDisplayName("${ChatColor.WHITE}통계")
    }

    private var sortWay = 0
    private var currentPage = 0
    override fun render(inv: Inventory) {
        statisticsInfo.apply {
            this.lore = listOf(
                "${ChatColor.WHITE}밴 된 유저: ${ChatColor.RED}${lands.banMap.size}${ChatColor.WHITE}명"
            )
        }

        inv.setItem(0, backButton)
        inv.setItem(3, sortButton)
        inv.setItem(4, statisticsInfo)
        inv.setItem(5, sortButton)
        inv.setItem(8, backButton)

        for (i in 10..16)
            inv.setItem(i, blackPane)

        pageRender(inv)
    }

    private fun pageRender(inv: Inventory) {
        val sortLore = when (sortWay) {
            0 -> {
                //Default sorting
                playerList = lands.banMap.keys.sortedBy {
                    lands.banMap[it]!!.banned
                }
                "기본"
            }
            1 -> {
                //Descending sorting
                playerList = lands.banMap.keys.sortedByDescending {
                    lands.banMap[it]!!.banned
                }
                "오래된 순"
            }
            else -> ""
        }

        sortButton.apply {
            this.lore = listOf(
                "${ChatColor.WHITE}현재 보기 상태: ${ChatColor.GOLD}$sortLore",
                "${ChatColor.WHITE}기본: 최근에 밴 된 순서로 봅니다.",
                "${ChatColor.WHITE}오래된 순: 오래된 순서대로 봅니다.",
                "",
                "${ChatColor.WHITE}클릭하여 정렬 방법을 변경할 수 있습니다."
            )
        }
        inv.setItem(3, sortButton)
        inv.setItem(5, sortButton)

        val start = currentPage * 36
        val length = min(playerList.size - start, 36)
        for (i in 0 until length) {
            Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                val head = (Material.PLAYER_HEAD * 1).apply {
                    val meta = this.itemMeta as SkullMeta
                    val entry = Bukkit.getOfflinePlayer(playerList[start + i])
                    val banData = lands.banMap[entry.uniqueId]!!
                    val date = Calendar.getInstance()
                    date.timeInMillis = banData.banned
                    meta.owningPlayer = entry
                    meta.setDisplayName(entry.name)
                    meta.lore = listOf(
                        "${ChatColor.WHITE}UUID: ${ChatColor.GRAY}${banData.uuid}",
                        "${ChatColor.WHITE}추방 일자: ${ChatColor.GRAY}${
                            date.get(Calendar.YEAR)
                        }/${
                            date.get(Calendar.MONTH) + 1
                        }/${
                            date.get(Calendar.DATE)
                        }"
                    )
                    this.itemMeta = meta
                }
                //Start index is 18 because of decorations
                inv.setItem(i + 18, head)
            })
        }

        if (currentPage < 1)
            inv.setItem(9, blackPane)
        else
            inv.setItem(9, previousPageButton)

        if ((playerList.size - start) > 36)
            inv.setItem(17, nextPageButton)
        else
            inv.setItem(17, blackPane)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true

        when (event.rawSlot) {
            //Back button
            0, 8 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                if (callByCommand)
                    player.closeInventory()
                else
                    player.openMainGui(lands, rank)
            }
            //Hopper(Sorting way change)
            3, 5 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                sortWay++
                if (sortWay > 1)
                    sortWay = 0
                pageRender(event.inventory)
            }
            //Previous
            9 -> {
                if (currentPage > 0) {
                    currentPage--
                    player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                    pageRender(event.inventory)
                }
            }
            //Next
            17 -> {
                val start = currentPage * 36
                if ((playerList.size - start) > 36) {
                    currentPage++
                    player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                    pageRender(event.inventory)
                }
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }
}

fun Player.openBanListGui(lands: Lands, callByCommand: Boolean, rank: Rank) {
    val inventory = Bukkit.createInventory(null, 54, "밴 목록")
    val gui = BanListGui(this, lands, callByCommand, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}