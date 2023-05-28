package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.gui.render.releaseFarmFlagItem
import com.salkcoding.tunalands.gui.render.releaseProtectFlagItem
import com.salkcoding.tunalands.gui.render.takeFarmFlagItem
import com.salkcoding.tunalands.gui.render.takeProtectFlagItem
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

    @EventHandler(priority = EventPriority.LOW)
    fun onFlag(event: PlayerInteractEvent) {
        if (event.useInteractedBlock() == Event.Result.DENY) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val flag = event.item ?: return
        val under = event.clickedBlock ?: return
        val player = event.player
        when (flag.type) {
            //Protect flag
            Material.GREEN_BANNER -> {
                if (flag.isSimilar(takeProtectFlagItem)) landManager.buyLand(player, flag, under)
                else if (flag.isSimilar(releaseProtectFlagItem)) landManager.sellLand(player, flag, under)
                event.isCancelled = true
            }
            //Farm flag
            Material.BROWN_BANNER -> {
                if (flag.isSimilar(takeFarmFlagItem)) landManager.setLandType(player, flag, under, LandType.FARM)
                else if (flag.isSimilar(releaseFarmFlagItem)) landManager.setLandType(player, flag, under, LandType.NORMAL)
                event.isCancelled = true
            }

            else -> return
        }
    }
}