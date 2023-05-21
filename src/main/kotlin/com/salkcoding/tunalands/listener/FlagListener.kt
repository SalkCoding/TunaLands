package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.gui.render.releaseProtectFlagItem
import com.salkcoding.tunalands.gui.render.takeProtectFlagItem
import com.salkcoding.tunalands.landManager
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
        when (flag.type) {
            Material.GREEN_BANNER -> {
                if (!flag.isSimilar(takeProtectFlagItem)) return
                landManager.buyLand(event.player, flag, under)
                event.isCancelled = true
            }
            Material.RED_BANNER -> {
                if (!flag.isSimilar(releaseProtectFlagItem)) return
                landManager.sellLand(event.player, flag, under)
                event.isCancelled = true
            }
            else -> return
        }
    }
}