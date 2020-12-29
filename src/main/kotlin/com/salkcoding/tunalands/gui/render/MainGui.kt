package com.salkcoding.tunalands.gui.render

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import br.com.devsrsouza.kotlinbukkitapi.extensions.player.playSound
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.gui.render.settinggui.openSettingGui
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.blackPane
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.times
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.math.floor

class MainGui(private val player: Player, private val rank: Rank) : GuiInterface {

    private val lands: Lands = landManager.getPlayerLands(player.uniqueId)!!

    //Dynamic
    private val totalInfoIcon = (Material.CAMPFIRE * 1).apply {
        this.displayName("${player.name}의 지역")
    }

    private val lockButton = (Material.IRON_DOOR * 1)

    //Static
    companion object {
        private val settingButton = (Material.BONE * 1).apply {
            this.displayName("지역 관리")
            this.lore = listOf(
                "지역의 세부 설정을 변경합니다."
            )
        }

        private val shopButton = (Material.HEART_OF_THE_SEA * 1).apply {
            this.displayName("지역 상점")
            this.lore = listOf(
                "지역에 관련된 물품을 구매할 수 있습니다."
            )
        }

        private val userListButton = (Material.WRITABLE_BOOK * 1).apply {
            this.displayName("사용자 목록")
            this.lore = listOf(
                "지역의 사용자 목록을 확인합니다."
            )
        }

        private val banListButton = (Material.CRIMSON_SIGN * 1).apply {
            this.displayName("밴 목록")
            this.lore = listOf(
                "밴 목록을 확인합니다."
            )
        }

        private val timeRegex = Regex("[\\d]+")
        private val measureRegex = Regex("[가-힣]+")
    }

    lateinit var task: BukkitTask

    override fun render(inv: Inventory) {
        for (i in 0..9) {
            inv.setItem(i, blackPane)
        }

        val landHistory = lands.landHistory
        val created = Calendar.getInstance()
        created.timeInMillis = landHistory.createdMillisecond
        var expired = lands.expiredMillisecond - System.currentTimeMillis()

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, Runnable {
            //Expired
            if (expired < 1000 || !lands.enable) {
                player.sendMessage("Protect expired! All of your lands are going to be unprotected!".warnFormat())
                player.sendMessage("If you want to reactivate protection, refill fuels.".warnFormat())

                totalInfoIcon.apply {
                    this.lore = listOf(
                        "보호: 비활성화",
                        "점유한 지역: ${lands.landList.size}개",
                        "멤버 수: ${lands.memberMap.size}명",
                        "생성일: ${created.get(Calendar.YEAR)}/${created.get(Calendar.MONTH) + 1}/${created.get(Calendar.DATE)}"
                    )
                }
                if (lands.enable)
                    lands.enable = false
                task.cancel()
                return@Runnable
            }
            //Not expired
            expired -= 1000
            totalInfoIcon.apply {
                this.lore = listOf(
                    "남은 연료: ${
                        expired / 86400000
                    }일 ${
                        (expired / 3600000) % 24
                    }시간 ${
                        (expired / 60000) % 60
                    }분 ${
                        (expired / 1000) % 60
                    }초",
                    "점유한 지역: ${lands.landList.size}개",
                    "멤버 수: ${lands.memberMap.size}명",
                    "생성일: ${created.get(Calendar.YEAR)}/${created.get(Calendar.MONTH) + 1}/${created.get(Calendar.DATE)}"
                )
            }
            Bukkit.getScheduler().runTask(tunaLands, Runnable {
                inv.setItem(4, totalInfoIcon)
            })
        }, 0, 20)

        lockButton.apply {
            this.displayName(
                "지역을 ${
                    when (lands.open) {
                        true -> "비공개"
                        false -> "공개"
                    }
                }로 전환"
            )
            this.lore = listOf(
                "지역의 공개 여부를 변경합니다.",
                "현재 상태: ${
                    when (lands.open) {
                        true -> "공개"
                        false -> "비공개"
                    }
                }"
            )
        }

        inv.setItem(4, totalInfoIcon)
        for (i in 17..45 step 9) {
            inv.setItem(i, blackPane)
            inv.setItem(i + 1, blackPane)
        }
        inv.setItem(46, blackPane)
        inv.setItem(47, lockButton)
        inv.setItem(48, settingButton)
        inv.setItem(49, shopButton)
        inv.setItem(50, userListButton)
        inv.setItem(51, banListButton)
        inv.setItem(52, blackPane)
        inv.setItem(53, blackPane)
    }

    override fun onClick(event: InventoryClickEvent) {
        if (event.isCancelled) return

        event.isCancelled = true

        val clicked = event.rawSlot
        when (clicked) {
            47 -> {
                when (rank) {
                    Rank.OWNER, Rank.DELEGATOR -> {
                        lockButton.apply {
                            this.displayName(
                                "지역을 ${
                                    when (lands.open) {
                                        true -> {
                                            lands.open = false
                                            player.playSound(Sound.BLOCK_WOODEN_DOOR_CLOSE, .5f, 1f)
                                            "공개"
                                        }
                                        false -> {
                                            lands.open = true
                                            player.playSound(Sound.BLOCK_WOODEN_DOOR_OPEN, .5f, 1f)
                                            "비공개"
                                        }
                                    }
                                }로 전환"
                            )
                            this.lore = listOf(
                                "지역의 공개 여부를 변경합니다.",
                                "현재 상태: ${
                                    when (lands.open) {
                                        true -> "공개"
                                        false -> "비공개"
                                    }
                                }"
                            )
                        }
                        event.inventory.setItem(47, lockButton)
                    }
                    else -> {
                        player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
                        player.sendMessage("You don't have a permission to access".errorFormat())
                    }
                }
            }
            48 -> {
                when (rank) {
                    Rank.OWNER, Rank.DELEGATOR -> {
                        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                        player.openSettingGui(rank)
                    }
                    else -> {
                        player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
                        player.sendMessage("You don't have a permission to access".errorFormat())
                    }
                }
            }
            49 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                player.openShopGui(rank)
            }
            50 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                player.openUserListGui(rank)
            }
            51 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                player.openBanListGui(false, rank)
            }
        }

        when (event.action) {
            InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME -> {
                if (event.clickedInventory == player.openInventory.topInventory)
                    return
            }
            InventoryAction.COLLECT_TO_CURSOR -> {
                return
            }
            InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME -> {//Ignore
            }
            else -> {
                if (clicked < 54) return
            }
        }

        event.isCancelled = false
    }

    override fun onClose(event: InventoryCloseEvent) {
        task.cancel()
        guiManager.guiMap.remove(event.view)

        val inv = event.inventory
        val fuelList = mutableListOf<ItemStack>()
        for (i in 1..4) {
            for (j in 1..7) {
                fuelList.add(inv.getItem((i * 9) + j) ?: continue)
            }
        }

        for (fuel in fuelList) {
            val lore = fuel.lore ?: continue
            if (lore.size != 2) continue
            if (fuel.itemMeta.displayName != "연료") continue

            val timeLore = lore[1].split(" ")[0]
            val time = timeRegex.find(timeLore)!!.value.toInt()
            //Expired refresh
            if (!lands.enable)
                lands.expiredMillisecond = System.currentTimeMillis()

            lands.expiredMillisecond += (fuel.amount * time * when (measureRegex.find(timeLore)!!.value) {
                "분" -> 60000
                "시간" -> 3600000
                else -> 0
            })
        }
    }
}

fun Player.openMainGui(rank: Rank) {
    val inventory = Bukkit.createInventory(null, 54, "Main GUI")
    val gui = MainGui(this, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}
