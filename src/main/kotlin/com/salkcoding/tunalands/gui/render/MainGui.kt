package com.salkcoding.tunalands.gui.render

import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.api.event.LandFuelAddEvent
import com.salkcoding.tunalands.api.event.LandGUIOpenEvent
import com.salkcoding.tunalands.gui.*
import com.salkcoding.tunalands.gui.render.settinggui.openSettingGui
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
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
    private val totalInfoIcon = (Material.CAMPFIRE * 1)

    private val lockButton = (Material.IRON_DOOR * 1)

    private val renderList = listOf(
        blackPane,
        blackPane,
        blackPane,
        blackPane,
        null,
        blackPane,
        blackPane,
        blackPane,
        blackPane,
        blackPane,
        settingButton,
        shopButton,
        blackPane,
        totalInfoIcon,
        blackPane,
        userListButton,
        banListButton,
        blackPane,
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

    lateinit var task: BukkitTask

    override fun render(inv: Inventory) {
        val landHistory = lands.landHistory
        val created = Calendar.getInstance()
        created.timeInMillis = landHistory.createdMillisecond

        task = Bukkit.getScheduler().runTaskTimer(tunaLands, Runnable {
            //Expired
            if (lands.enable && lands.fuelSecLeft <= 0) {//Just close, DO NOT DELETE DATA OR BLOCK HERE
                player.sendMessage("보호 기간이 만료되어, 지역 보호가 비활성화됩니다!".warnFormat())
                task.cancel()
//                Bukkit.getScheduler().runTask(tunaLands, Runnable(player::closeInventory))
                return@Runnable
            }
            //Not expired or already disabled
            totalInfoIcon.apply {
                val timeLeftInSeconds = lands.fuelSecLeft
                val timeLeft = "${ChatColor.WHITE}${
                    when {
                        timeLeftInSeconds > 0 -> {
                            val days = timeLeftInSeconds / 86400
                            val hours = (timeLeftInSeconds / 3600) % 24
                            val minutes = (timeLeftInSeconds / 60) % 60
                            val seconds = timeLeftInSeconds % 60

                            when {
                                days > 0 -> "예상: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초 남음"
                                hours > 0 -> "예상: ${hours}시간 ${minutes}분 ${seconds}초 남음"
                                minutes > 0 -> "예상: ${minutes}분 ${seconds}초 남음"
                                seconds > 0 -> "예상: ${seconds}초 남음"
                                else -> "예상: 0초 남음"
                            }
                        }

                        else -> "비활성화됨"
                    }
                }"

                val currentNumOfMembers = lands.memberMap.filter { (_, memberData) ->
                    memberData.rank != Rank.VISITOR && memberData.rank != Rank.PARTTIMEJOB
                }.size

                this.setDisplayName(lands.landsName)
                this.lore = listOf(
                    "${ChatColor.WHITE}현재 연료 가치: ${secondsToDateString(lands.fuelSecLeft)}",
                    "${ChatColor.WHITE}(* 멤버 증가시 연료 가치: ${
                        secondsToDateString(configuration.fuel.getFuelAddAmount(lands.getFullTimeMemberSize() + 1).addAmount)
                    })",
                    timeLeft,
                    "${ChatColor.WHITE}점유한 지역: ${ChatColor.GOLD}${lands.landMap.size}${ChatColor.WHITE}개",
                    "${ChatColor.WHITE}멤버 수: ${ChatColor.GOLD}${currentNumOfMembers}${ChatColor.WHITE}명",
                    "${ChatColor.WHITE}추천 수: ${ChatColor.GOLD}${lands.recommend}",
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
            this.setDisplayName(
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
                ) return

                Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                    val addedFuelItem = event.inventory.getItem(4) ?: return@Runnable
                    if (!addedFuelItem.isSimilar(fuelItem)) return@Runnable

                    lands.fuelSecLeft += addedFuelItem.amount * configuration.fuel.getFuelAddAmount(lands).addAmount

                    if (!lands.enable) {
                        lands.enable = true
                        displayManager.resumeDisplay(lands)?.update()
                    } else displayManager.updateDisplay(lands)

                    val timeLeftInSeconds = lands.fuelSecLeft
                    val timeLeft = when {
                        timeLeftInSeconds > 0 -> {
                            val days = timeLeftInSeconds / 86400
                            val hours = (timeLeftInSeconds / 3600) % 24
                            val minutes = (timeLeftInSeconds / 60) % 60
                            val seconds = timeLeftInSeconds % 60

                            when {
                                days > 0 -> "예상: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초 남음"
                                hours > 0 -> "예상: ${hours}시간 ${minutes}분 ${seconds}초 남음"
                                minutes > 0 -> "예상: ${minutes}분 ${seconds}초 남음"
                                seconds > 0 -> "예상: ${seconds}초 남음"
                                else -> "예상: 0초 남음"
                            }
                        }

                        else -> "예상: 0초 남음"
                    }

                    // DO NOT CHANGE MESSAGE FORMAT. LINKED WITH KIBANA
                    tunaLands.logger.info("${player.name} (${player.uniqueId}) burned fuel x${addedFuelItem.amount}")

                    player.sendMessage("${ChatColor.WHITE}연료 ${ChatColor.GOLD}${addedFuelItem.amount}개${ChatColor.WHITE}가 추가되었습니다! ".infoFormat())
                    player.sendMessage("${ChatColor.WHITE}남은 시간: ${ChatColor.GOLD}$timeLeft".infoFormat())
                    player.playSound(player.location, Sound.BLOCK_BLASTFURNACE_FIRE_CRACKLE, 2.5f, 1f)
                    event.inventory.setItem(4, null)

                    Bukkit.getPluginManager().callEvent(
                        LandFuelAddEvent(lands, player, addedFuelItem.amount)
                    )
                }, 2)
            }

            10 -> {
                when (rank) {
                    Rank.OWNER, Rank.DELEGATOR -> {
                        if (!lands.enable) {
                            player.sendMessage("땅을 먼저 재활성화 시켜야합니다!".errorFormat())
                            return
                        }

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
                        if (!lands.enable) {
                            player.sendMessage("땅을 먼저 재활성화 시켜야합니다!".errorFormat())
                            return
                        }

                        lockButton.apply {
                            this.setDisplayName(
                                "${ChatColor.WHITE}지역을 ${
                                    when (lands.open) {
                                        true -> {
                                            lands.open = false
                                            player.playSound(player.location, Sound.BLOCK_WOODEN_DOOR_CLOSE, .5f, 1f)
                                            "${ChatColor.GREEN}공개"
                                        }

                                        false -> {
                                            lands.open = true
                                            player.playSound(player.location, Sound.BLOCK_WOODEN_DOOR_OPEN, .5f, 1f)
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
                val addedFuelItem = event.inventory.getItem(4) ?: return@Runnable
                if (!addedFuelItem.isSimilar(fuelItem)) return@Runnable
                lands.fuelSecLeft += addedFuelItem.amount * configuration.fuel.getFuelAddAmount(lands).addAmount

                if (!lands.enable) {
                    lands.enable = true
                    displayManager.resumeDisplay(lands)?.update()
                }

                val timeLeftInSeconds = lands.fuelSecLeft
                val timeLeft = when {
                    timeLeftInSeconds > 0 -> {
                        val days = timeLeftInSeconds / 86400
                        val hours = (timeLeftInSeconds / 3600) % 24
                        val minutes = (timeLeftInSeconds / 60) % 60
                        val seconds = timeLeftInSeconds % 60

                        when {
                            days > 0 -> "예상: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초 남음"
                            hours > 0 -> "예상: ${hours}시간 ${minutes}분 ${seconds}초 남음"
                            minutes > 0 -> "예상: ${minutes}분 ${seconds}초 남음"
                            seconds > 0 -> "예상: ${seconds}초 남음"
                            else -> "예상: 0초 남음"
                        }
                    }

                    else -> "예상: 0초 남음"
                }

                // DO NOT CHANGE MESSAGE FORMAT. LINKED WITH KIBANA
                tunaLands.logger.info("${player.name} (${player.uniqueId}) burned fuel x${addedFuelItem.amount}")

                player.sendMessage("${ChatColor.WHITE}연료 ${ChatColor.GOLD}${addedFuelItem.amount}개${ChatColor.WHITE}가 추가되었습니다! ".infoFormat())
                player.sendMessage("${ChatColor.WHITE}남은 시간: ${ChatColor.GOLD}$timeLeft".infoFormat())
                player.playSound(player.location, Sound.BLOCK_BLASTFURNACE_FIRE_CRACKLE, 2.5f, 1f)
                event.inventory.setItem(4, null)

                Bukkit.getPluginManager().callEvent(
                    LandFuelAddEvent(lands, player, addedFuelItem.amount)
                )
            }, 2)
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        task.cancel()
        guiManager.guiMap.remove(event.view)
        val remainItem = event.inventory.getItem(4) ?: return
        player.giveOrDrop(remainItem)
    }
}

fun Player.openMainGui(lands: Lands, rank: Rank) {
    val inventory = Bukkit.createInventory(null, 27, "TunaLands")
    val gui = MainGui(this, lands, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui

    Bukkit.getPluginManager().callEvent(LandGUIOpenEvent(this, LandGUIOpenEvent.GUIType.MAIN))
}
