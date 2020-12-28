package com.salkcoding.tunalands.listener.region

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PickupExpListener : Listener {

    @EventHandler
    fun onPickupExp(event: PlayerPickupExperienceEvent) {
        if (event.isCancelled) return

        val lands = landManager.getLandsWithChunk(event.experienceOrb.chunk) ?: return
        if (!lands.enable) return

        val player = event.player
        val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            else -> lands.visitorSetting
        }

        if (!setting.pickupExp) {
            player.sendMessage("권한이 없습니다.".errorFormat())
            event.isCancelled = true
        }
    }
}