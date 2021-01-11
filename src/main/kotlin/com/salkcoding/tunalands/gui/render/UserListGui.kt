package com.salkcoding.tunalands.gui.render

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.math.min

class UserListGui(private val player: Player, private val lands: Lands, private val rank: Rank) : GuiInterface {

    private lateinit var playerList: List<UUID>

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
            var onlineCount = 0
            var delegatorCount = 0
            var memberCount = 0
            var partTimeJobCount = 0
            var visitorCount = 0
            lands.memberMap.forEach { (uuid, data) ->
                if (Bukkit.getOfflinePlayer(uuid).isOnline)
                    onlineCount++
                when (data.rank) {
                    Rank.DELEGATOR -> delegatorCount++
                    Rank.MEMBER -> memberCount++
                    Rank.PARTTIMEJOB -> partTimeJobCount++
                    Rank.VISITOR -> visitorCount++
                    else -> return@forEach
                }
            }
            this.lore = listOf(
                "총 인원: ${onlineCount}/${lands.memberMap.size}",
                "관리 대리인: ${delegatorCount}명",
                "멤버: ${memberCount}명",
                "알바: ${partTimeJobCount}명",
                "방문자: ${visitorCount}명"
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
                playerList = lands.memberMap.keys.sortedByDescending {
                    lands.memberMap[it]!!.lastLogin
                }
                "기본"
            }
            1 -> {
                //Delegator sorting
                playerList = lands.memberMap.keys.sortedByDescending {
                    val data = lands.memberMap[it]!!
                    if (data.rank == Rank.DELEGATOR) {
                        lands.memberMap[it]!!.lastLogin
                    } else {
                        0
                    }
                }
                "관리 대리인"
            }
            2 -> {
                //Member sorting
                playerList = lands.memberMap.keys.sortedByDescending {
                    val data = lands.memberMap[it]!!
                    if (data.rank == Rank.MEMBER) {
                        lands.memberMap[it]!!.lastLogin
                    } else {
                        0
                    }
                }
                "멤버"
            }
            3 -> {
                //Part time job sorting
                playerList = lands.memberMap.keys.sortedByDescending {
                    val data = lands.memberMap[it]!!
                    if (data.rank == Rank.PARTTIMEJOB) {
                        lands.memberMap[it]!!.lastLogin
                    } else {
                        0
                    }
                }
                "알바"
            }
            4 -> {
                //Visitor sorting
                playerList = lands.memberMap.keys.sortedByDescending {
                    val data = lands.memberMap[it]!!
                    if (data.rank == Rank.VISITOR) {
                        lands.memberMap[it]!!.lastLogin
                    } else {
                        0
                    }
                }
                "방문자"
            }
            else -> ""
        }

        sortButton.apply {
            this.lore = listOf(
                "현재 보기 상태: $sortLore",
                "기본: 모든 사용자를 최근에 입장한 순서로 봅니다.",
                "관리 대리인: 가장 최근에 입장한 순서로 봅니다.",
                "멤버: 가장 최근에 입장한 순서로 봅니다.",
                "알바: 가장 최근에 입장한 순서로 봅니다.",
                "방문자: 가장 최근에 방문한 순서로 봅니다.",
                "",
                "클릭하여 정렬 방법을 변경할 수 있습니다."
            )
        }
        inv.setItem(3, sortButton)
        inv.setItem(5, sortButton)

        val start = currentPage * 36
        val length = min(playerList.size - start, 36)

        for (i in 0 until length) {
            val head = (Material.PLAYER_HEAD * 1).apply {
                val meta = this.itemMeta as SkullMeta
                val entry = Bukkit.getOfflinePlayer(playerList[start + i])
                val memberData = lands.memberMap[entry.uniqueId]!!
                val date = Calendar.getInstance()
                date.timeInMillis = memberData.lastLogin
                meta.owningPlayer = entry
                meta.setDisplayName(entry.name)
                meta.lore = listOf(
                    "권한: ${memberData.rank}",
                    "최근 방문일: ${
                        date.get(Calendar.YEAR)
                    }/${
                        date.get(Calendar.MONTH) + 1
                    }/${
                        date.get(Calendar.DATE)
                    } ${
                        date.get(Calendar.HOUR_OF_DAY)
                    }:${
                        date.get(Calendar.MINUTE)
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
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                player.openMainGui(lands, rank)
            }
            //Hopper(Sorting way change)
            3, 5 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                sortWay++
                if (sortWay > 4)
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

fun Player.openUserListGui(lands: Lands, rank: Rank) {
    val inventory = Bukkit.createInventory(null, 54, "User list GUI")
    val gui = UserListGui(this, lands, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}