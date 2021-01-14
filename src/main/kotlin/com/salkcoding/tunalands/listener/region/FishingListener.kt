package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

class FishingListener : Listener {

    @EventHandler
    fun onFishing(event: PlayerFishEvent) {
        if(event.isCancelled) return

        val player = event.player
        val lands = landManager.getLandsWithChunk(player.chunk) ?: return

        if (player.uniqueId in lands.memberMap) {
            val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> return
                Rank.MEMBER -> lands.memberSetting
                Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                Rank.VISITOR -> lands.visitorSetting
            }

            if (setting.canFishing)
                event.isCancelled = true
        }

        if (event.isCancelled)
            player.sendMessage("권한이 없습니다!".errorFormat())
    }
}