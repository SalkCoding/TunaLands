package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.util.isSameLocation
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class CoreListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onCorePlace(event: BlockPlaceEvent) {
        if (event.isCancelled) return

        val chest = event.block
        if (chest.type == Material.CHEST) {
            val coreBlock = chest.getRelative(0, -1, 0)
            if (coreBlock.type == configuration.protect.coreBlock) {
                val player = event.player
                landManager.buyLand(player, chest, coreBlock)
            }
        }
    }

    @EventHandler
    fun onCoreBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return

        val chunk = event.block.chunk
        val lands = landManager.getLandsWithChunk(chunk) ?: return
        if (!lands.enable) return

        val player = event.player
        if (landManager.isProtectedLand(chunk)) {
            //Core protection
            val block = event.block
            if (block.type == Material.CHEST || block.type == configuration.protect.coreBlock) {
                val upCore = lands.upCore
                val downCore = lands.downCore

                if (block.isSameLocation(upCore.world, upCore.x, upCore.y, upCore.z) ||
                    block.isSameLocation(downCore.world, downCore.x, downCore.y, downCore.z)
                ) {
                    player.sendMessage("You can't break core or core of chest".warnFormat())
                    event.isCancelled = true
                }
            }
        }
    }
}