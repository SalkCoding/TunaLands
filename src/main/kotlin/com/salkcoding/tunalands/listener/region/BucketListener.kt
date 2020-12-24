package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent

class BucketListener : Listener {

    @EventHandler
    fun onEmpty(event: PlayerBucketEmptyEvent) {
        if (event.isCancelled) return
        val block = event.blockClicked

        val lands = landManager.getLandsWithChunk(block.chunk) ?: return
        if (!lands.enable) return

        val player = event.player
        val setting = when (lands.getRank(player.uniqueId)) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            Rank.VISITOR -> lands.visitorSetting
            else -> null
        } ?: return

        if (setting.useBucket)
            event.isCancelled = true
    }

    @EventHandler
    fun onFill(event: PlayerBucketFillEvent) {
        if (event.isCancelled) return
        val block = event.blockClicked

        val lands = landManager.getLandsWithChunk(block.chunk) ?: return
        if (!lands.enable) return

        val player = event.player
        val setting = when (lands.getRank(player.uniqueId)) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            Rank.VISITOR -> lands.visitorSetting
            else -> null
        } ?: return

        if (setting.useBucket)
            event.isCancelled = true
    }
}