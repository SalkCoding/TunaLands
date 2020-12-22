package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class CakeListener : Listener {

    @EventHandler
    fun onEat(event: PlayerInteractEvent) {
        if (event.useInteractedBlock() == Event.Result.DENY) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val block = event.clickedBlock!!

        if (block.type != Material.CAKE) return
        val lands = landManager.getLandsWithChunk(block.chunk) ?: return
        if (!lands.enable) return

        val player = event.player
        val setting = when (lands.getRank(player.uniqueId)) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            Rank.VISITOR -> lands.visitorSetting
            else -> null
        } ?: return

        if (!setting.eatCake)
            event.isCancelled = true
    }
}