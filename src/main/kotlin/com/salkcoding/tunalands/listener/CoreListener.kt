package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.api.event.LandCreateEvent
import com.salkcoding.tunalands.util.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent

class CoreListener : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun onCorePlace(event: BlockPlaceEvent) {
        val placedBlock = event.block
        val player = event.player
        if (placedBlock.type == configuration.protect.coreBlockType && player.isSneaking) {
            event.isCancelled = true
            if (configuration.limitWorld.contains(placedBlock.world.name)) {
                player.sendErrorTipMessage("${ChatColor.RED}해당 월드에서는 코어를 만들 수 없습니다!")
                return
            }

            if (landManager.isProtectedLand(placedBlock.chunk)) {
                player.sendErrorTipMessage("${ChatColor.RED}다른 사람의 땅에는 코어를 만들 수 없습니다!")
                return
            }

            if (landManager.getPlayerLands(player.uniqueId) != null) {
                player.sendErrorTipMessage("${ChatColor.RED}이미 땅을 소유하고있습니다!")
                return
            }

            val price = configuration.protect.createPrice.toDouble()
            if (player.hasNotEnoughMoney(price)) {
                val delta = price - economy.getBalance(player)
                player.sendErrorTipMessage("${ChatColor.RED}${"%.2f".format(delta)}캔이 부족합니다.")
                return
            }
            economy.withdrawPlayer(player, price)

            val chest = placedBlock.getRelative(0, 1, 0)
            chest.type = Material.CHEST

            val lands = landManager.createLand(player, chest, placedBlock)
            Bukkit.getPluginManager().callEvent(
                LandCreateEvent(
                    lands,
                    player,
                    placedBlock.location
                )
            )
            event.isCancelled = false
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onCoreBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return

        val chunk = event.block.chunk
        val lands = landManager.getLandsWithChunk(chunk) ?: return

        val player = event.player
        if (landManager.isProtectedLand(chunk)) {
            //Core protection
            val block = event.block
            if (block.type == Material.CHEST || block.type == configuration.protect.coreBlockType) {
                val upCoreLocation = lands.upCoreLocation
                val downCoreLocation = lands.downCoreLocation

                if (block.location == upCoreLocation || block.location == downCoreLocation) {
                    player.sendErrorTipMessage("${ChatColor.RED}코어 블럭과 코어 창고는 부술 수 없습니다.")
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onCoreExpanded(event: BlockPistonExtendEvent) {
        val lands = landManager.getLandsWithChunk(event.block.chunk) ?: return
        event.blocks.forEach { block ->
            if (lands.downCoreLocation == block.location || lands.upCoreLocation == block.location) {
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onCoreRetract(event: BlockPistonRetractEvent) {
        val lands = landManager.getLandsWithChunk(event.block.chunk) ?: return
        event.blocks.forEach { block ->
            if (lands.downCoreLocation == block.location || lands.upCoreLocation == block.location) {
                event.isCancelled = true
                return
            }
        }
    }
}