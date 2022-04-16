package com.salkcoding.tunalands.gui.render

import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.gui.render.settinggui.openSettingGui
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
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*

class MainGui(private val player: Player, private val lands: Lands, private val rank: Rank) : GuiInterface {

    //Dynamic
    private val totalInfoIcon = (Material.CAMPFIRE * 1)

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
            this.setDisplayName("${ChatColor.WHITE}지역 관리")
            this.lore = listOf(
                "${ChatColor.WHITE}지역의 세부 설정을 변경합니다."
            )
        }

        private val shopButton = (Material.HEART_OF_THE_SEA * 1).apply {
            this.setDisplayName("${ChatColor.WHITE}지역 상점")
            this.lore = listOf(
                "${ChatColor.WHITE}지역에 관련된 물품을 구매할 수 있습니다."
            )
        }

        private val userListButton = (Material.WRITABLE_BOOK * 1).apply {
            this.setDisplayName("${ChatColor.WHITE}사용자 목록")
            this.lore = listOf(
                "${ChatColor.WHITE}지역의 사용자 목록을 확인합니다."
            )
        }

        private val banListButton = (Material.CRIMSON_SIGN * 1).apply {
            this.setDisplayName("${ChatColor.WHITE}밴 목록")
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

        task = Bukkit.getScheduler().runTaskTimer(tunaLands, Runnable {
            val timeLeftUntilExpiration = lands.getEstimatedMillisecondsLeftWithCurrentFuel()
            //Expired
            if (lands.enable && timeLeftUntilExpiration < 1000) {//Just close, DO NOT DELETE DATA OR BLOCK HERE
                player.sendMessage("보호 기간이 만료되어, 지역 보호가 비활성화됩니다!".warnFormat())
                task.cancel()
                Bukkit.getScheduler().runTask(tunaLands, Runnable(player::closeInventory))
                return@Runnable
            }
            //Not expired or already disabled
            totalInfoIcon.apply {
                val days = timeLeftUntilExpiration / 86400000
                val hours = (timeLeftUntilExpiration / 3600000) % 24
                val minutes = (timeLeftUntilExpiration / 60000) % 60
                val seconds = (timeLeftUntilExpiration / 1000) % 60
                val timeLeft = when {
                    days > 0 -> "${ChatColor.WHITE}예상: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초 남음"
                    hours > 0 -> "${ChatColor.WHITE}예상: ${hours}시간 ${minutes}분 ${seconds}초 남음"
                    minutes > 0 -> "${ChatColor.WHITE}예상: ${minutes}분 ${seconds}초 남음"
                    seconds > 0 -> "${ChatColor.WHITE}예상: ${seconds}초 남음"
                    else -> "${ChatColor.RED}예상: 0초 남음"
                }


                val fuelPerHour = 3600000.0 / lands.getMillisecondsPerFuel()
                val fuelPerDay = 86400000.0 / lands.getMillisecondsPerFuel()
                val fuelInfo = String.format("${ChatColor.WHITE}*시간 당 %.2f개 소모 (하루에 %.2f개)", fuelPerHour, fuelPerDay)

                val currentNumOfMembers = lands.memberMap.filter { (_, memberData) ->
                    memberData.rank != Rank.VISITOR && memberData.rank != Rank.PARTTIMEJOB
                }.size

                val currentFuelRequirement = configuration.fuel.getFuelRequirement(lands)
                val nextFuelRequirements = configuration.fuel.fuelRequirements.filter {
                    it.numOfChunks >= lands.landList.size
                            && it.numOfMembers >= currentNumOfMembers
                            && currentFuelRequirement != it
                }

                var nextBestFuelRequirementForChunk: Config.FuelRequirement? = null
                var nextBestFuelRequirementForMembers: Config.FuelRequirement? = null

                for (requirement in nextFuelRequirements) {
                    if (nextBestFuelRequirementForChunk != null) {
                        if (requirement.secondsPerFuel >= nextBestFuelRequirementForChunk.secondsPerFuel
                            && requirement.numOfChunks <= nextBestFuelRequirementForChunk.numOfChunks) {
                            nextBestFuelRequirementForChunk = requirement
                        }
                    } else {
                        nextBestFuelRequirementForChunk = requirement
                    }

                    if (nextBestFuelRequirementForMembers != null) {
                        if (requirement.secondsPerFuel >= nextBestFuelRequirementForMembers.secondsPerFuel
                            && requirement.numOfMembers <= nextBestFuelRequirementForChunk.numOfMembers) {
                            nextBestFuelRequirementForMembers = requirement
                        }
                    } else {
                        nextBestFuelRequirementForMembers = requirement
                    }
                }

                val chunksLeftUntilNextRequirement = if (nextBestFuelRequirementForChunk != null) {
                    "(하루 당 연료 증가까지 남은 청크: ${max(0, nextBestFuelRequirementForChunk.numOfChunks - lands.landList.size)}개)"
                } else {
                    ""
                }

                val membersLeftUntilNextRequirement = if (nextBestFuelRequirementForMembers != null) {
                    "(하루 당 연료 증가까지 남은 인원 수: ${max(0, nextBestFuelRequirementForMembers.numOfMembers - currentNumOfMembers)}명)"
                } else {
                    ""
                }

                this.setDisplayName(lands.landsName)
                this.lore = listOf(
                    "${ChatColor.WHITE}현재 연료: ${lands.fuelLeft}개",
                    timeLeft,
                    fuelInfo,
                    "${ChatColor.WHITE}점유한 지역: ${ChatColor.GOLD}${lands.landList.size}${ChatColor.WHITE}개 $chunksLeftUntilNextRequirement",
                    "${ChatColor.WHITE}멤버 수: ${ChatColor.GOLD}${lands.memberMap.size}${ChatColor.WHITE}명 $membersLeftUntilNextRequirement",
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
                )
                    return
                Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                    val addedFuelItem = event.inventory.getItem(4) ?: return@Runnable
                    if (!addedFuelItem.isSimilar(ShopGui.fuelItem)) return@Runnable

                    lands.fuelLeft += addedFuelItem.amount

                    if (!lands.enable) {
                        lands.enable = true
                        displayManager.resumeDisplay(lands)
                    }

                    alarmManager.resetAlarm(lands)

                    val newlyAddedMilliseconds = lands.getMillisecondsPerFuel() * addedFuelItem.amount
                    val days = newlyAddedMilliseconds / 86400000
                    val hours = (newlyAddedMilliseconds / 3600000) % 24
                    val minutes = (newlyAddedMilliseconds / 60000) % 60
                    val seconds = (newlyAddedMilliseconds / 1000) % 60

                    val addedTimeEstimate = when {
                        days > 0 -> "${days}일 ${hours}시간 ${minutes}분 ${seconds}초 (예상)"
                        hours > 0 -> "${hours}시간 ${minutes}분 ${seconds}초 (예상)"
                        minutes > 0 -> "${minutes}분 ${seconds}초 (예상)"
                        seconds > 0 -> "${seconds}초"
                        else -> "0초 (예상)"
                    }

                    player.sendMessage("${ChatColor.WHITE}연료 ${ChatColor.GOLD}${addedFuelItem.amount}개${ChatColor.WHITE}가 추가되었습니다! ".infoFormat())
                    player.sendMessage("${ChatColor.WHITE}현재 마을 규모 기준 추가 된 시간: ${ChatColor.GOLD}$addedTimeEstimate".infoFormat())
                    player.playSound(player.location, Sound.BLOCK_BLASTFURNACE_FIRE_CRACKLE, 2.5f, 1f)
                    event.inventory.setItem(4, null)
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
                if (!addedFuelItem.isSimilar(ShopGui.fuelItem)) return@Runnable
                lands.fuelLeft += addedFuelItem.amount

                if (!lands.enable) {
                    lands.enable = true
                    displayManager.resumeDisplay(lands)
                }

                alarmManager.resetAlarm(lands)

                val newlyAddedMilliseconds = lands.getMillisecondsPerFuel() * addedFuelItem.amount
                val days = newlyAddedMilliseconds / 86400000
                val hours = (newlyAddedMilliseconds / 3600000) % 24
                val minutes = (newlyAddedMilliseconds / 60000) % 60
                val seconds = (newlyAddedMilliseconds / 1000) % 60

                val addedTimeEstimate = when {
                    days > 0 -> "${days}일 ${hours}시간 ${minutes}분 ${seconds}초 (예상)"
                    hours > 0 -> "${hours}시간 ${minutes}분 ${seconds}초 (예상)"
                    minutes > 0 -> "${minutes}분 ${seconds}초 (예상)"
                    seconds > 0 -> "${seconds}초"
                    else -> "0초 (예상)"
                }

                player.sendMessage("${ChatColor.WHITE}연료 ${ChatColor.GOLD}${addedFuelItem.amount}개${ChatColor.WHITE}가 추가되었습니다! ".infoFormat())
                player.sendMessage("${ChatColor.WHITE}(현재 청크 갯수 및 인원기준 추가된 시간: ${ChatColor.GOLD}$addedTimeEstimate)".infoFormat())
                player.playSound(player.location, Sound.BLOCK_BLASTFURNACE_FIRE_CRACKLE, 2.5f, 1f)
                event.inventory.setItem(4, null)
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
}
