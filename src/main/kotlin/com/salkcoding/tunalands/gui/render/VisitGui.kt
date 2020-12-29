package com.salkcoding.tunalands.gui.render

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
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
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.math.min

class VisitGui(private val player: Player) : GuiInterface {
    private val landMap = landManager.getPlayerLandMap()
    private lateinit var landList: MutableList<UUID>

    private val sortButton = (Material.HOPPER * 1).apply {
        this.displayName("정렬 방법 선택")
    }

    private val statisticsInfo = (Material.PAINTING * 1).apply {
        this.displayName("지역 통계")
    }

    private var sortWay = 0
    private var currentPage = 0
    override fun render(inv: Inventory) {
        statisticsInfo.apply {
            var publicCount = 0
            var privateCount = 0
            landMap.forEach { (_, lands) ->
                if (lands.open) publicCount++
                else privateCount++
            }
            this.lore = listOf(
                "지역: ${landMap.size}개",
                "공개된 지역: ${publicCount}개",
                "비공개된 지역: ${privateCount}개"
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
                landList = landMap.keys.sortedByDescending {
                    landMap[it]!!.landHistory.createdMillisecond
                }.toMutableList()
                "기본"
            }
            1 -> {
                //Public sorting
                landList = landMap.keys.sortedByDescending {
                    landMap[it]!!.landHistory.createdMillisecond
                }.filter {
                    landMap[it]!!.enable
                }.toMutableList()
                "공개 지역"
            }
            2 -> {
                //Private sorting
                landList = landMap.keys.sortedByDescending {
                    landMap[it]!!.landHistory.createdMillisecond
                }.filter {
                    !landMap[it]!!.enable
                }.toMutableList()
                "비공개 지역"
            }
            3 -> {
                //Solo sorting
                landList = landMap.keys.sortedByDescending {
                    landMap[it]!!.landHistory.createdMillisecond
                }.filter {
                    landMap[it]!!.memberMap.size == 1
                }.toMutableList()
                "혼자"
            }
            4 -> {
                //Member count sorting
                landList = landMap.keys.sortedByDescending {
                    landMap[it]!!.memberMap.size
                }.toMutableList()
                "멤버 수"
            }
            5 -> {
                //Visitor count sorting
                landList = landMap.keys.sortedByDescending {
                    landMap[it]!!.landHistory.visitorCount
                }.toMutableList()
                "방문자 수"
            }
            else -> ""
        }

        sortButton.apply {
            this.lore = listOf(
                "현재 보기 상태: $sortLore",
                "기본: 생성된 시간이 오래된 순서로 모든 지역의 목록을 봅니다.",
                "공개 지역: 공개로 설정된 지역의 목록을 생성된 시간이 오래된 순서로 봅니다.",
                "비공개 지역: 비공개로 설정된 지역의 목록을 생성된 시간이 오래된 순서로 봅니다.",
                "혼자:  혼자 살아가는 지역들을, 생성된 시간이 오래된 순서로 봅니다.",
                "멤버 수: 멤버가 많은 순으로 지역의 목록을 봅니다.",
                "방문자 수: 방문자 수가 많은 순으로 지역의 목록을 봅니다.",
                "",
                "클릭하여 정렬 방법을 변경할 수 있습니다."
            )
        }
        inv.setItem(3, sortButton)
        inv.setItem(5, sortButton)

        val start = currentPage * 36
        val length = min(landList.size - start, 36)

        for (i in start until length) {
            val uuid = landList[start + i]
            val entry = Bukkit.getOfflinePlayer(uuid)
            val lands = landMap[uuid]!!

            val head = (Material.PLAYER_HEAD * 1).apply {
                val meta = this.itemMeta as SkullMeta
                val created = Calendar.getInstance()
                created.timeInMillis = lands.landHistory.createdMillisecond
                meta.owningPlayer = entry
                meta.setDisplayName(entry.name)
                val lore = mutableListOf(
                    "공개 여부: ${
                        when (lands.open) {
                            true -> "공개"
                            false -> "비공개"
                        }
                    }",
                    "멤버 수: ${lands.memberMap.size}",
                    "방문자 수: ${lands.landHistory.visitorCount}",
                    "생성일: ${created.get(Calendar.YEAR)}/${created.get(Calendar.MONTH) + 1}/${created.get(Calendar.DATE)}",
                )
                (0 until lands.lore.size).forEach { i ->
                    lore.add(i, lands.lore[i])
                }
                if (lands.enable) {
                    lore.add("")
                    lore.add("클릭하여 이동할 수 있습니다.")
                }
                meta.lore = lore
                this.itemMeta = meta
            }
            //Start index is 18 because of decorations
            inv.setItem(i + 18, head)
        }

        if (currentPage < 1)
            inv.setItem(9, blackPane)
        else
            inv.setItem(9, previousPageButton)

        if ((landList.size - start) > 36)
            inv.setItem(17, nextPageButton)
        else
            inv.setItem(17, blackPane)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        when (event.rawSlot) {
            //Back button
            0, 8 -> {
                player.closeInventory()
            }
            //Hopper(Sorting way change)
            3, 5 -> {
                sortWay++
                if (sortWay > 5)
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
                if ((landList.size - start) > 36) {
                    currentPage++
                    pageRender(event.inventory)
                }
            }
            in 18..53 -> {
                val index = (currentPage * 36) + (event.rawSlot - 18)
                if (index >= landList.size) return

                val lands = landMap[landList[index]] ?: return
                val uuid = player.uniqueId
                if (uuid in lands.memberMap) {
                    player.sendMessage("자신이 소속되어있는 땅에는 방문할 수 없습니다.".errorFormat())
                    return
                }

                if (uuid in lands.memberMap) {
                    player.sendMessage("이미 방문 중입니다.".errorFormat())
                    return
                }

                if (!lands.banMap.containsKey(uuid)) {
                    if (lands.open || player.isOp) {
                        val current = System.currentTimeMillis()
                        lands.memberMap[uuid] = Lands.MemberData(
                            uuid,
                            Rank.VISITOR,
                            current,
                            current
                        )

                        player.closeInventory()
                        lands.welcomeMessage.forEach { welcomeMessage ->
                            player.sendMessage(welcomeMessage)
                        }
                        player.teleportAsync(lands.visitorSpawn)
                        lands.landHistory.visitorCount++
                    }
                } else {
                    player.sendMessage("${lands.ownerName}의 땅에서 밴되었기 때문에 방문할 수 없습니다.".errorFormat())
                }
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }
}

fun Player.openVisitGui() {
    val inventory = Bukkit.createInventory(null, 54, "User list GUI")
    val gui = VisitGui(this)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}