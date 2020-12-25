package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.gui.render.ShopGui
import com.salkcoding.tunalands.landManager
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class FlagListener : Listener {

    @EventHandler
    fun onFlag(event: PlayerInteractEvent) {
        if (event.useInteractedBlock() == Event.Result.DENY) return

        val flag = event.item ?: return
        val under = event.clickedBlock ?: return
        when (flag.type) {
            Material.GREEN_BANNER -> {
                if (!flag.isSimilar(ShopGui.takeFlag)) return
                landManager.buyLand(event.player, under)
                event.isCancelled = true
            }
            Material.RED_BANNER -> {
                if (!flag.isSimilar(ShopGui.releaseFlag)) return
                landManager.sellLand(event.player, under.chunk)
                event.isCancelled = true
            }
            else -> return
        }
    }
}