package com.salkcoding.tunalands.gui.render

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.data.lands.Lands
import com.salkcoding.tunalands.data.lands.Rank
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

class VisitGui(private val player: Player) : GuiInterface {
    private val landMap = landManager.getPlayerLandMap()
    private lateinit var landList: List<UUID>

    private val sortButton = (Material.HOPPER * 1).apply {
        this.displayName("${ChatColor.WHITE}정렬 방법 선택")
    }

    private val statisticsInfo = (Material.PAINTING * 1).apply {
        this.displayName("${ChatColor.WHITE}지역 통계")
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
                "${ChatColor.WHITE}지역: ${ChatColor.GOLD}${landMap.size}${ChatColor.WHITE}개",
                "${ChatColor.WHITE}공개된 지역: ${ChatColor.GREEN}${publicCount}${ChatColor.WHITE}개",
                "${ChatColor.WHITE}비공개된 지역: ${ChatColor.RED}${privateCount}${ChatColor.WHITE}개"
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
                }
                "기본"
            }
            1 -> {
                //Public sorting
                landList = landMap.keys.sortedByDescending {
                    landMap[it]!!.landHistory.createdMillisecond
                }
                "공개 지역"
            }
            2 -> {
                //Private sorting
                landList = landMap.keys.sortedByDescending {
                    landMap[it]!!.landHistory.createdMillisecond
                }
                "비공개 지역"
            }
            3 -> {
                //Solo sorting
                landList = landMap.keys.sortedByDescending {
                    landMap[it]!!.landHistory.createdMillisecond
                }.filter {
                    landMap[it]!!.memberMap.size == 1
                }
                "혼자"
            }
            4 -> {
                //Member count sorting
                landList = landMap.keys.sortedByDescending {
                    landMap[it]!!.memberMap.size
                }
                "멤버 수"
            }
            5 -> {
                //Visitor count sorting
                landList = landMap.keys.sortedByDescending {
                    landMap[it]!!.landHistory.visitorCount
                }
                "방문자 수"
            }
            else -> ""
        }

        sortButton.apply {
            this.lore = listOf(
                "${ChatColor.WHITE}현재 보기 상태: ${ChatColor.GOLD}$sortLore",
                "${ChatColor.WHITE}기본: 생성된 시간이 오래된 순서로 모든 지역의 목록을 봅니다.",
                "${ChatColor.WHITE}공개 지역: 공개로 설정된 지역의 목록을 생성된 시간이 오래된 순서로 봅니다.",
                "${ChatColor.WHITE}비공개 지역: 비공개로 설정된 지역의 목록을 생성된 시간이 오래된 순서로 봅니다.",
                "${ChatColor.WHITE}혼자:  혼자 살아가는 지역들을, 생성된 시간이 오래된 순서로 봅니다.",
                "${ChatColor.WHITE}멤버 수: 멤버가 많은 순으로 지역의 목록을 봅니다.",
                "${ChatColor.WHITE}방문자 수: 방문자 수가 많은 순으로 지역의 목록을 봅니다.",
                "",
                "${ChatColor.WHITE}클릭하여 정렬 방법을 변경할 수 있습니다."
            )
        }
        inv.setItem(3, sortButton)
        inv.setItem(5, sortButton)

        val start = currentPage * 36
        val length = min(landList.size - start, 36)
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands,Runnable{
            for (i in 0 until length) {
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
                        "${ChatColor.WHITE}공개 여부: ${
                            when (lands.open) {
                                true -> "${ChatColor.GREEN}공개"
                                false -> "${ChatColor.RED}비공개"
                            }
                        }",
                        "${ChatColor.WHITE}멤버 수: ${ChatColor.GOLD}${lands.memberMap.size}",
                        "${ChatColor.WHITE}방문자 수: ${ChatColor.GOLD}${lands.landHistory.visitorCount}",
                        "${ChatColor.WHITE}생성일: ${ChatColor.GRAY}${created.get(Calendar.YEAR)}/${created.get(Calendar.MONTH) + 1}/${
                            created.get(
                                Calendar.DATE
                            )
                        }",
                    )
                    (0 until lands.lore.size).forEach { i ->
                        lore.add(i, lands.lore[i])
                    }
                    if (lands.open) {
                        lore.add("")
                        lore.add("${ChatColor.WHITE}클릭하여 이동할 수 있습니다.")
                    }
                    meta.lore = lore
                    this.itemMeta = meta
                }
                //Start index is 18 because of decorations
                inv.setItem(i + 18, head)
            }
        })

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
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                player.closeInventory()
            }
            //Hopper(Sorting way change)
            3, 5 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                sortWay++
                if (sortWay > 5)
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
                if ((landList.size - start) > 36) {
                    currentPage++
                    player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                    pageRender(event.inventory)
                }
            }
            in 18..53 -> {
                val index = (currentPage * 36) + (event.rawSlot - 18)
                if (index >= landList.size) return

                val lands = landMap[landList[index]] ?: return
                if (!lands.open && !player.isOp) {
                    player.sendMessage("땅이 비공개 상태라 방문할 수 없습니다!".errorFormat())
                    return
                }

                val uuid = player.uniqueId
                if (uuid in lands.memberMap) {
                    val rank = lands.memberMap[uuid]!!.rank
                    if (rank != Rank.PARTTIMEJOB) {
                        player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
                        player.sendMessage("자신이 소속되어있는 땅에는 방문할 수 없습니다!".errorFormat())
                        return
                    }
                }

                if (uuid in lands.memberMap) {
                    val rank = lands.memberMap[uuid]!!.rank
                    if (rank != Rank.PARTTIMEJOB) {
                        player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
                        player.sendMessage("이미 방문 중입니다!".errorFormat())
                        return
                    }
                }

                if (!lands.banMap.containsKey(uuid)) {
                    if (lands.open || player.isOp) {
                        player.closeInventory()
                        lands.welcomeMessage.forEach { welcomeMessage ->
                            player.sendMessage(welcomeMessage)
                        }

                        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                        TeleportCooltime.addPlayer(
                            player,
                            lands.visitorSpawn,
                            configuration.command.visitCooldown,
                            {
                                if (uuid in lands.memberMap) {
                                    if (lands.memberMap[uuid]!!.rank == Rank.PARTTIMEJOB)
                                        return@addPlayer
                                }

                                lands.landHistory.visitorCount += 1
                                val current = System.currentTimeMillis()
                                lands.memberMap[uuid] = Lands.MemberData(
                                    uuid,
                                    Rank.VISITOR,
                                    current,
                                    current
                                )

                                lands.memberMap.forEach { (uuid, _) ->
                                    val member = Bukkit.getOfflinePlayer(uuid)
                                    if (member.isOnline) {
                                        member.player!!.sendMessage("${ChatColor.GREEN}${player.name}${ChatColor.WHITE}님이 땅에 방문했습니다.".infoFormat())
                                    } else {
                                        bungeeApi.sendMessage(
                                            member.name,
                                            "${ChatColor.GREEN}${player.name}${ChatColor.WHITE}님이 땅에 방문했습니다.".infoFormat()
                                        )
                                    }
                                }
                            },
                            false
                        )
                    }
                } else {
                    player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
                    player.sendMessage("${ChatColor.GREEN}${lands.ownerName}${ChatColor.WHITE}의 땅에서 밴되었기 때문에 방문할 수 없습니다!".errorFormat())
                }
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }
}

fun Player.openVisitGui() {
    val inventory = Bukkit.createInventory(null, 54, "지역 목록")
    val gui = VisitGui(this)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}