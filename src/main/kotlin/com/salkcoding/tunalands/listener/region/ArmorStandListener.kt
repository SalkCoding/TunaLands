package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerArmorStandManipulateEvent

class ArmorStandListener : Listener {

    @EventHandler
    fun onArmorStand(event: PlayerArmorStandManipulateEvent) {
        if (event.isCancelled) return

        val player = event.player
        val lands = landManager.getLandsWithChunk(player.chunk) ?: return
        if (!lands.enable) return

        val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            else -> lands.visitorSetting
        }

        if (!setting.useArmorStand) {
            player.sendMessage("권한이 없습니다.".errorFormat())
            event.isCancelled = true
        }
    }
}