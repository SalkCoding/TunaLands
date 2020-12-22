package com.salkcoding.tunalands.events

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent


class BlockPlace : Listener {

    @EventHandler
    fun onProtect(event: BlockPlaceEvent) {
        if (event.isCancelled) return

        val player = event.player
        val chunk = event.block.chunk
        if (landManager.isProtectedLand(event.block.chunk) && !landManager.isPlayerLand(player, chunk)) {
            player.sendMessage("This land is protected by ${landManager.getLandOwnerName(chunk)}".warnFormat())
            event.isCancelled = true
        }
    }

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

}