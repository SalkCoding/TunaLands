package com.salkcoding.tunalands.gui.render

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import br.com.devsrsouza.kotlinbukkitapi.extensions.player.playSound
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.blackPane
import com.salkcoding.tunalands.util.times
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.math.floor

class MainGui(private val player: Player) : GuiInterface {

    //Dynamic
    private val totalInfoIcon = (Material.CAMPFIRE * 1).apply {
        this.displayName("${player.name}의 지역")
    }

    private val lockButton = (Material.IRON_DOOR * 1).apply {
        this.lore = listOf(
            "지역의 공개 여부를 변경합니다."
        )
    }

    //Static
    companion object {
        val settingButton = (Material.BONE * 1).apply {
            this.displayName("지역 관리")
            this.lore = listOf(
                "지역의 세부 설정을 변경합니다."
            )
        }

        val shopButton = (Material.HEART_OF_THE_SEA * 1).apply {
            this.displayName("지역 상점")
            this.lore = listOf(
                "지역에 관련된 물품을 구매할 수 있습니다."
            )
        }

        val userListButton = (Material.WRITABLE_BOOK * 1).apply {
            this.displayName("사용자 목록")
            this.lore = listOf(
                "지역의 사용자 목록을 확인합니다."
            )
        }
    }

    lateinit var task: BukkitTask

    override fun render(inv: Inventory) {
        for (i in 0..9) {
            inv.setItem(i, blackPane)
        }

        val uuid = player.uniqueId
        val lands = landManager.getPlayerLands(uuid)!!
        val landInfo = lands.landInfo
        val created = Calendar.getInstance()
        created.timeInMillis = landInfo.createdMillisecond
        var expired = landInfo.expiredMillisecond - System.currentTimeMillis()

        task = Bukkit.getScheduler().runTaskTimer(tunaLands, Runnable {
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
                    "멤버 수: ${landInfo.memberList.size}명",
                    "생성일: ${created.get(Calendar.YEAR)}/${created.get(Calendar.MONTH) + 1}/${created.get(Calendar.DATE)}"
                )
            }
            inv.setItem(4, totalInfoIcon)
        }, 0, 20)

        val setting = lands.setting
        lockButton.apply {
            this.displayName(
                when (setting.open) {
                    true -> "지역을 비공개로 전환"
                    false -> "지역을 공개로 전환"
                }
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
        inv.setItem(51, (Material.CRIMSON_SIGN * 1))
        inv.setItem(52, blackPane)
        inv.setItem(53, blackPane)
    }

    override fun onClick(event: InventoryClickEvent) {
        //Another menu clicked
        event.isCancelled = true

        val clicked = event.rawSlot
        val lands = landManager.getPlayerLands(player.uniqueId)!!
        val setting = lands.setting
        when (clicked) {
            47 -> {
                //TODO If public turns to private, make a sound door closed, else door opened
                lockButton.apply {
                    this.displayName(
                        when (setting.open) {
                            true -> {
                                setting.open = false
                                player.playSound(Sound.BLOCK_WOODEN_DOOR_CLOSE, .5f, 1f)
                                "지역을 공개로 전환"
                            }
                            false -> {
                                setting.open = true
                                player.playSound(Sound.BLOCK_WOODEN_DOOR_OPEN, .5f, 1f)
                                "지역을 비공개로 전환"
                            }
                        }
                    )
                }
                event.inventory.setItem(47, lockButton)
            }
            48 -> {
                player.openSettingGui()
            }
            49 -> {
                player.openShopGui()
            }
            50 -> {
                player.openUserListGui()
            }
            51 -> {
                TODO("Not implemented")
            }
        }

        //Exclude 0~9 and 45~53, only allow 10~44 but its mod not has to 0 or 8
        val mod = clicked % 9
        if ((clicked in 0..9) or (clicked in 45..53) or ((mod == 0) or (mod == 8) and (clicked < 54))) {
            return
        }

        event.isCancelled = false
    }

    //Implementation not needed
    override fun onDrag(event: InventoryDragEvent) {}

    override fun onClose(event: InventoryCloseEvent) {
        task.cancel()
        val inv = event.inventory
        for (i in 10..44) {
            val mod = i % 9
            if ((mod == 0) or (mod == 8))
                continue
            val item = inv.getItem(i)
            if (item != null)
                event.player.inventory.addItem(inv.getItem(i))
        }
        guiManager.guiMap.remove(event.view)
    }

}

fun Player.openMainGui() {
    val inventory = Bukkit.createInventory(null, 54, "Main GUI")
    val gui = MainGui(this)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}
