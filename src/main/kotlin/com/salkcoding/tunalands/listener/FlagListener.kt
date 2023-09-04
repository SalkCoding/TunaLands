package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.gui.releaseFarmFlagItem
import com.salkcoding.tunalands.gui.releaseProtectFlagItem
import com.salkcoding.tunalands.gui.takeFarmFlagItem
import com.salkcoding.tunalands.gui.takeProtectFlagItem
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.LandType
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class FlagListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onFlag(event: PlayerInteractEvent) {
        if (event.useInteractedBlock() == Event.Result.DENY) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val flag = event.item ?: return
        if (flag.type != Material.GREEN_BANNER && flag.type != Material.BROWN_BANNER) return

        val under = event.clickedBlock ?: return
        val player = event.player

        event.isCancelled = true

        when {
            //Protect flag
            flag.isSimilar(takeProtectFlagItem) -> landManager.buyChunk(player, flag, under)
            flag.isSimilar(releaseProtectFlagItem) -> landManager.sellChunk(player, flag, under)
            //Farm flag
            flag.isSimilar(takeFarmFlagItem) -> landManager.setLandType(player, flag, under, LandType.FARM)
            flag.isSimilar(releaseFarmFlagItem) -> landManager.setLandType(
                player,
                flag,
                under,
                LandType.NORMAL
            )

            //Normal flag
            else -> event.isCancelled = false
        }
    }
}