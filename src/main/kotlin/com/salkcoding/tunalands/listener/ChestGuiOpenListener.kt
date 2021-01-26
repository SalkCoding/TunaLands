package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.gui.render.openMainGui
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.data.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.isSameLocation
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class ChestGuiOpenListener : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun onInteract(event: PlayerInteractEvent) {
        if (event.useInteractedBlock() == Event.Result.DENY) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val block = event.clickedBlock!!

        //Open gui with core
        if (block.type == Material.CHEST) {
            val player = event.player
            val chunk = block.chunk
            val lands = landManager.getLandsWithChunk(chunk) ?: return
            val upCore = lands.upCore
            if (!block.isSameLocation(upCore.world.name, upCore.blockX, upCore.blockY, upCore.blockZ)) return
            event.isCancelled = true
            if (player.uniqueId in lands.memberMap) {
                when (val rank = lands.memberMap[player.uniqueId]!!.rank) {
                    Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER -> player.openMainGui(
                        lands,
                        rank
                    )
                    else -> player.sendMessage("권한이 없습니다!".errorFormat())
                }
            } else player.sendMessage("권한이 없습니다!".errorFormat())
        }
    }

}