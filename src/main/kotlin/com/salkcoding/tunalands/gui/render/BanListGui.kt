package com.salkcoding.tunalands.gui.render

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.commands.sub.BanList
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.math.min

class BanListGui(private val player: Player, private val callByCommand: Boolean, private val rank: Rank) :
    GuiInterface {

    private val lands: Lands = landManager.getPlayerLands(player.uniqueId)!!
    private lateinit var playerList: MutableList<UUID>

    private val sortButton = (Material.HOPPER * 1).apply {
        this.displayName("정렬 방법 선택")
    }

    private val statisticsInfo = (Material.PAINTING * 1).apply {
        this.displayName("통계")
    }

    private var sortWay = 0
    private var currentPage = 0
    override fun render(inv: Inventory) {
        statisticsInfo.apply {
            this.lore = listOf(
                "밴 된 유저: ${lands.banMap.size}명"
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
                }.toMutableList()
                "기본"
            }
            1 -> {
                //Descending sorting
                playerList = lands.banMap.keys.sortedByDescending {
                    lands.banMap[it]!!.banned
                }.toMutableList()
                "오래된 순"
            }
            else -> ""
        }

        sortButton.apply {
            this.lore = listOf(
                "현재 보기 상태: $sortLore",
                "기본: 최근에 밴 된 순서로 봅니다.",
                "오래된 순: 오래된 순서대로 봅니다.",
                "",
                "클릭하여 정렬 방법을 변경할 수 있습니다."
            )
        }
        inv.setItem(3, sortButton)
        inv.setItem(5, sortButton)

        val start = currentPage * 36
        val length = min(playerList.size - start, 36)

        for (i in start until length) {
            val head = (Material.PLAYER_HEAD * 1).apply {
                val meta = this.itemMeta as SkullMeta
                val entry = Bukkit.getOfflinePlayer(playerList[start + i])
                val banData = lands.banMap[entry.uniqueId]!!
                val date = Calendar.getInstance()
                date.timeInMillis = banData.banned
                meta.owningPlayer = entry
                meta.setDisplayName(entry.name)
                meta.lore = listOf(
                    "UUID: ${banData.uuid}",
                    "추방 일자: ${
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
                if (callByCommand)
                    player.closeInventory()
                else
                    player.openMainGui(rank)
            }
            //Hopper(Sorting way change)
            3, 5 -> {
                sortWay++
                if (sortWay > 1)
                    sortWay = 0
                pageRender(event.inventory)
            }
            //Previous
            9 -> {
                if (currentPage > 0) {
                    currentPage--
                    pageRender(event.inventory)
                }
            }
            //Next
            17 -> {
                val start = currentPage * 36
                if ((playerList.size - start) > 36) {
                    currentPage++
                    pageRender(event.inventory)
                }
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }
}

fun Player.openBanListGui(callByCommand: Boolean, rank: Rank) {
    val inventory = Bukkit.createInventory(null, 54, "Ban list GUI")
    val gui = BanListGui(this, callByCommand, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}