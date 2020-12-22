package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

class PickupItemListener : Listener {

    @EventHandler
    fun onPickupItem(event: EntityPickupItemEvent) {
        if (event.isCancelled) return

        val lands = landManager.getLandsWithChunk(event.item.chunk) ?: return
        if (!lands.enable) return

        val player = event.entity as? Player ?: return
        val setting = when (lands.getRank(player.uniqueId)) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            Rank.VISITOR -> lands.visitorSetting
            else -> null
        } ?: return

        if(!setting.pickupItem)
            event.isCancelled = true
    }

}