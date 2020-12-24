package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerLeashEntityEvent

class LeashEntityListener : Listener {

    @EventHandler
    fun onEntityInteract(event: PlayerLeashEntityEvent) {
        if (event.isCancelled) return

        val lands = landManager.getLandsWithChunk(event.entity.chunk) ?: return
        if (!lands.enable) return

        val player = event.player
        val setting = when (lands.getRank(player.uniqueId)) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            Rank.VISITOR -> lands.visitorSetting
            else -> null
        } ?: return

        if (!setting.useLead)
            event.isCancelled = true
    }
}