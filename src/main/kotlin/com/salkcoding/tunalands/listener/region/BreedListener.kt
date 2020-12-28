package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityBreedEvent

class BreedListener : Listener {

    @EventHandler
    fun onBreed(event: EntityBreedEvent) {
        if (event.isCancelled) return
        val entity = event.entity

        val lands = landManager.getLandsWithChunk(entity.chunk) ?: return
        if (!lands.enable) return

        val player = event.breeder as? Player ?: return
        val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            else -> lands.visitorSetting
        }

        if (setting.canBreed) {
            player.sendMessage("권한이 없습니다.".errorFormat())
            event.isCancelled = true
        }
    }
}