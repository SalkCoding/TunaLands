package com.salkcoding.tunalands.listener.region

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PickupExpListener : Listener {

    @EventHandler
    fun onPickupExp(event: PlayerPickupExperienceEvent) {
        if (event.isCancelled) return

        val lands = landManager.getLandsWithChunk(event.experienceOrb.chunk) ?: return
        if (!lands.enable) return

        val player = event.player
        val setting = when (lands.getRank(player.uniqueId)) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            Rank.VISITOR -> lands.visitorSetting
            else -> null
        } ?: return

        if (!setting.pickupExp)
            event.isCancelled = true
    }
}