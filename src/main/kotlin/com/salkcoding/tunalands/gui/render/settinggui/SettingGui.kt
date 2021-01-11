package com.salkcoding.tunalands.gui.render.settinggui

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.gui.render.openMainGui
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.backButton
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.times
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class SettingGui(private val player: Player, private val lands: Lands, private val rank: Rank) : GuiInterface {

    companion object {
        val visitorSettingButton = (Material.OAK_SIGN * 1).apply {
            this.displayName("방문자 설정")
            this.lore = listOf("방문자의 권한을 설정합니다.")
        }

        val memberSettingButton = (Material.PAINTING * 1).apply {
            this.displayName("멤버 설정")
            this.lore = listOf("멤버의 권한을 설정합니다.")
        }

        val delegatorSettingButton = (Material.ITEM_FRAME * 1).apply {
            this.displayName("관리대리인 설정")
            this.lore = listOf("관리대리인의 권한을 설정합니다.")
        }

        val partTimeJobSettingButton = (Material.NAME_TAG * 1).apply {
            this.displayName("알바 설정")
            this.lore = listOf("알바의 권한을 설정합니다.")
        }

        val regionSettingButton = (Material.GRASS_BLOCK * 1).apply {
            this.displayName("지역 설정")
            this.lore = listOf("업데이트 예정입니다.")
        }
    }

    override fun render(inv: Inventory) {
        inv.setItem(0, backButton)
        inv.setItem(2, visitorSettingButton)
        inv.setItem(3, memberSettingButton)
        inv.setItem(4, delegatorSettingButton)
        inv.setItem(6, partTimeJobSettingButton)
        inv.setItem(7, regionSettingButton)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val setting = lands.delegatorSetting
        when (event.rawSlot) {
            0 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                player.openMainGui(lands, rank)
            }
            2 -> {
                when (rank) {
                    Rank.OWNER -> {
                        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                        player.openVisitorSettingGui(lands, rank)
                    }
                    else -> when {
                        setting.canSetVisitorSetting -> {
                            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                            player.openVisitorSettingGui(lands, rank)
                        }
                        else -> {
                            player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
                            player.sendMessage("권한이 없습니다!".errorFormat())
                        }
                    }
                }
            }
            3 -> {
                when (rank) {
                    Rank.OWNER -> {
                        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                        player.openMemberSettingGui(lands, rank)
                    }
                    else -> when {
                        setting.canSetMemberSetting -> {
                            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                            player.openMemberSettingGui(lands, rank)
                        }
                        else -> {
                            player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
                            player.sendMessage("권한이 없습니다!".errorFormat())
                        }
                    }
                }
            }
            4 -> {
                when (rank) {
                    Rank.OWNER -> {
                        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                        player.openDelegatorSettingGui(lands, rank)
                    }
                    else -> {
                        player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
                        player.sendMessage("권한이 없습니다!".errorFormat())
                    }
                }
            }
            6 -> {
                when (rank) {
                    Rank.OWNER -> {
                        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                        player.openPartTimeJobSettingGui(lands, rank)
                    }
                    else -> when {
                        setting.canSetPartTimeJobSetting -> {
                            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                            player.openPartTimeJobSettingGui(lands, rank)
                        }
                        else -> {
                            player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
                            player.sendMessage("권한이 없습니다!".errorFormat())
                        }
                    }
                }
            }
            7 -> {
                player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f)
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }
}

fun Player.openSettingGui(lands: Lands, rank: Rank) {
    val inventory = Bukkit.createInventory(null, 9, "설정")
    val gui = SettingGui(this, lands, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}