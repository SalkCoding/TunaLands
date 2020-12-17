package com.salkcoding.tunalands.events

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent

class BlockBreak : Listener {

    @EventHandler
    fun onProtect(event: BlockBreakEvent) {
        if (event.isCancelled) return

        val player = event.player
        player.sendMessage(event.block.type.toString())
        val chunk = event.block.chunk
        val protected = landManager.isProtectedLand(chunk)
        val owned = landManager.isPlayerLand(player, chunk)
        if (protected and owned) {
            val block = event.block
            //Trying to break core or chest of core.
            if ((block.type == Material.CHEST) or (block.type == configuration.protect.coreBlock)) {
                player.sendMessage("You can't break core or core of chest".warnFormat())
                event.isCancelled = true
            }
        } else if (protected and !owned) {
            player.sendMessage("This land protected by ${landManager.getLandOwnerName(chunk)}".warnFormat())
            event.isCancelled = true
        }
    }

}