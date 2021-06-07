package com.salkcoding.tunalands.gui.render

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import br.com.devsrsouza.kotlinbukkitapi.extensions.player.playSound
import com.salkcoding.tunalands.data.lands.Lands
import com.salkcoding.tunalands.data.lands.Rank
import com.salkcoding.tunalands.displayManager
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.gui.render.settinggui.openSettingGui
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.scheduler.BukkitTask
import java.util.*

class MainGui(private val player: Player, private val lands: Lands, private val rank: Rank) : GuiInterface {

    //Dynamic
    private val totalInfoIcon = (Material.CAMPFIRE * 1).apply {
        this.displayName("${ChatColor.GOLD}${lands.ownerName}${ChatColor.WHITE}의 지역")
    }

    private val lockButton = (Material.IRON_DOOR * 1)

    private val renderList = listOf(
        //First row
        blackPane,
        blackPane,
        blackPane,
        blackPane,
        null,
        blackPane,
        blackPane,
        blackPane,
        blackPane,
        //Second row
        blackPane,
        settingButton,
        shopButton,
        blackPane,
        totalInfoIcon,
        blackPane,
        userListButton,
        banListButton,
        blackPane,
        //Third row
        blackPane,
        blackPane,
        blackPane,
        blackPane,
        lockButton,
        blackPane,
        blackPane,
        blackPane,
        blackPane
    )

    //Static
    companion object {
        private val settingButton = (Material.BONE * 1).apply {
            this.displayName("${ChatColor.WHITE}지역 관리")
            this.lore = listOf(
                "${ChatColor.WHITE}지역의 세부 설정을 변경합니다."
            )
        }

        private val shopButton = (Material.HEART_OF_THE_SEA * 1).apply {
            this.displayName("${ChatColor.WHITE}지역 상점")
            this.lore = listOf(
                "${ChatColor.WHITE}지역에 관련된 물품을 구매할 수 있습니다."
            )
        }

        private val userListButton = (Material.WRITABLE_BOOK * 1).apply {
            this.displayName("${ChatColor.WHITE}사용자 목록")
            this.lore = listOf(
                "${ChatColor.WHITE}지역의 사용자 목록을 확인합니다."
            )
        }

        private val banListButton = (Material.CRIMSON_SIGN * 1).apply {
            this.displayName("${ChatColor.WHITE}밴 목록")
            this.lore = listOf(
                "${ChatColor.WHITE}밴 목록을 확인합니다."
            )
        }

        private val timeRegex = Regex("[\\d]+")
        private val measureRegex = Regex("[가-힣]+")
    }

    lateinit var task: BukkitTask

    override fun render(inv: Inventory) {
        val landHistory = lands.landHistory
        val created = Calendar.getInstance()
        created.timeInMillis = landHistory.createdMillisecond

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, Runnable {
            val expired = lands.expiredMillisecond - System.currentTimeMillis()
            //Expired
            if (expired < 1000) {//Just close, DO NOT DELETE DATA OR BLOCK HERE
                player.sendMessage("보호 기간이 만료되어, 지역 보호가 비활성화됩니다!".warnFormat())
                task.cancel()
                Bukkit.getScheduler().runTask(tunaLands, Runnable(player::closeInventory))
                return@Runnable
            }
            //Not expired
            totalInfoIcon.apply {
                val days = expired / 86400000
                val hours = (expired / 3600000) % 24
                val minutes = (expired / 60000) % 60
                val seconds = (expired / 1000) % 60
                val fuel = when {
                    days > 0 -> "${ChatColor.WHITE}남은 연료: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초"
                    hours > 0 -> "${ChatColor.WHITE}남은 연료: ${hours}시간 ${minutes}분 ${seconds}초"
                    minutes > 0 -> "${ChatColor.WHITE}남은 연료: ${minutes}분 ${seconds}초"
                    seconds > 0 -> "${ChatColor.WHITE}남은 연료: ${seconds}초"
                    else -> "${ChatColor.WHITE}NULL"
                }
                this.lore = listOf(
                    fuel,
                    "${ChatColor.WHITE}점유한 지역: ${ChatColor.GOLD}${lands.landList.size}${ChatColor.WHITE}개",
                    "${ChatColor.WHITE}멤버 수: ${ChatColor.GOLD}${lands.memberMap.size}${ChatColor.WHITE}명",
                    "${ChatColor.WHITE}생성일: ${ChatColor.GRAY}${
                        created.get(Calendar.YEAR)
                    }/${
                        created.get(Calendar.MONTH) + 1
                    }/${
                        created.get(Calendar.DATE)
                    }"
                )
            }
            Bukkit.getScheduler().runTask(tunaLands, Runnable {
                inv.setItem(13, totalInfoIcon)
            })
        }, 0, 20)

        lockButton.apply {
            this.displayName(
                "${ChatColor.WHITE}지역을 ${
                    when (lands.open) {
                        true -> "${ChatColor.RED}비공개"
                        false -> "${ChatColor.GREEN}공개"
                    }
                }${ChatColor.WHITE}로 전환"
            )
            this.lore = listOf(
                "${ChatColor.WHITE}지역의 공개 여부를 변경합니다.",
                "${ChatColor.WHITE}현재 상태: ${
                    when (lands.open) {
                        true -> "${ChatColor.GREEN}공개"
                        false -> "${ChatColor.RED}비공개"
                    }
                }"
            )
        }

        for (i in renderList.indices)
            inv.setItem(i, renderList[i])
    }

    override fun onClick(event: InventoryClickEvent) {
        if (event.isCancelled) return

        event.isCancelled = true

        val clicked = event.rawSlot
        when (clicked) {
            4 -> {
                event.isCancelled = false
                if (event.action != InventoryAction.PLACE_ALL
                    && event.action != InventoryAction.PLACE_ONE
                    && event.action != InventoryAction.PLACE_SOME
                )
                    return
                Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                    val fuel = event.inventory.getItem(4) ?: return@Runnable
                    if (!fuel.isSimilar(ShopGui.fuel30Minutes) &&
                        !fuel.isSimilar(ShopGui.fuel1Hour) &&
                        !fuel.isSimilar(ShopGui.fuel6Hours) &&
                        !fuel.isSimilar(ShopGui.fuel12Hours) &&
                        !fuel.isSimilar(ShopGui.fuel24Hours)
                    ) return@Runnable
                    val lore = fuel.lore ?: return@Runnable
                    val timeLore = lore[1].split(" ")[0]
                    val time = timeRegex.find(timeLore)!!.value.toInt()
                    val measure = measureRegex.find(timeLore)!!.value

                    lands.expiredMillisecond += (fuel.amount * time * when (measure) {
                        "분" -> 60000
                        "시간" -> 3600000
                        else -> 0
                    })

                    player.sendMessage("${ChatColor.GOLD}${time * fuel.amount}${ChatColor.WHITE}${measure}이 추가되었습니다!".infoFormat())
                    player.playSound(player.location, Sound.BLOCK_BLASTFURNACE_FIRE_CRACKLE, 2.5f, 1f)
                    event.inventory.setItem(4, null)
                }, 2)
            }
            10 -> {
                when (rank) {
                    Rank.OWNER, Rank.DELEGATOR -> {
                        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                        player.openSettingGui(lands, rank)
                    }
                    else -> {
                        player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
                        player.sendMessage("권한이 없습니다!".errorFormat())
                    }
                }
            }
            11 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                player.openShopGui(lands, rank)
            }
            15 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                player.openUserListGui(lands, rank)
            }
            16 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                player.openBanListGui(lands, false, rank)
            }
            22 -> {
                when (rank) {
                    Rank.OWNER, Rank.DELEGATOR -> {
                        lockButton.apply {
                            this.displayName(
                                "${ChatColor.WHITE}지역을 ${
                                    when (lands.open) {
                                        true -> {
                                            lands.open = false
                                            player.playSound(Sound.BLOCK_WOODEN_DOOR_CLOSE, .5f, 1f)
                                            "${ChatColor.GREEN}공개"
                                        }
                                        false -> {
                                            lands.open = true
                                            player.playSound(Sound.BLOCK_WOODEN_DOOR_OPEN, .5f, 1f)
                                            "${ChatColor.RED}비공개"
                                        }
                                    }
                                }로 전환"
                            )
                            this.lore = listOf(
                                "${ChatColor.WHITE}지역의 공개 여부를 변경합니다.",
                                "${ChatColor.WHITE}현재 상태: ${
                                    when (lands.open) {
                                        true -> "${ChatColor.GREEN}공개"
                                        false -> "${ChatColor.RED}비공개"
                                    }
                                }"
                            )
                        }
                        event.inventory.setItem(22, lockButton)
                    }
                    else -> {
                        player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
                        player.sendMessage("권한이 없습니다!".errorFormat())
                    }
                }
            }
        }

        if (clicked >= 27) {
            event.isCancelled = false

            if (event.action != InventoryAction.MOVE_TO_OTHER_INVENTORY)
                return
            Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                val fuel = event.inventory.getItem(4) ?: return@Runnable
                if (!fuel.isSimilar(ShopGui.fuel30Minutes) &&
                    !fuel.isSimilar(ShopGui.fuel1Hour) &&
                    !fuel.isSimilar(ShopGui.fuel6Hours) &&
                    !fuel.isSimilar(ShopGui.fuel12Hours) &&
                    !fuel.isSimilar(ShopGui.fuel24Hours)
                ) return@Runnable
                val lore = fuel.lore ?: return@Runnable
                val timeLore = lore[1].split(" ")[0]
                val time = timeRegex.find(timeLore)!!.value.toInt()
                val measure = measureRegex.find(timeLore)!!.value

                lands.expiredMillisecond += (fuel.amount * time * when (measure) {
                    "분" -> 60000
                    "시간" -> 3600000
                    else -> 0
                })

                player.sendMessage("${ChatColor.GOLD}${time * fuel.amount}${ChatColor.WHITE}${measure}이 추가되었습니다!".infoFormat())
                player.playSound(player.location, Sound.BLOCK_BLASTFURNACE_FIRE_CRACKLE, 2.5f, 1f)
                event.inventory.setItem(4, null)
            }, 2)
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        task.cancel()
        guiManager.guiMap.remove(event.view)
    }
}

fun Player.openMainGui(lands: Lands, rank: Rank) {
    val inventory = Bukkit.createInventory(null, 27, "TunaLands")
    val gui = MainGui(this, lands, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}
