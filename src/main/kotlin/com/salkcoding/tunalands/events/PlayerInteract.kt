package com.salkcoding.tunalands.events

import com.salkcoding.tunalands.gui.render.openMainGui
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.isSameLocation
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteract : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        //Open gui with core
        if (block.type == Material.CHEST) {
            val player = event.player
            val chunk = block.chunk
            val lands = landManager.getLandsWithChunk(chunk) ?: return
            val core = lands.core
            if (!block.isSameLocation(core.world, core.x, core.y, core.z)) return

            when (val rank = lands.getRank(player.uniqueId)) {
                Rank.MEMBER -> player.sendMessage("You don't have a permission to access setting gui".errorFormat())
                else -> player.openMainGui(rank)
            }
        }
    }

}