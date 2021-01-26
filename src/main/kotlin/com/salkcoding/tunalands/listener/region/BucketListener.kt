package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.data.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
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

        val player = event.player
        if (player.uniqueId in lands.memberMap) {
            val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> return
                Rank.MEMBER -> lands.memberSetting
                Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                Rank.VISITOR -> lands.visitorSetting
            }

            if (setting.useBucket)
                event.isCancelled = true
        } else event.isCancelled = true

        if (event.isCancelled)
            player.sendMessage("권한이 없습니다!".errorFormat())
    }

    @EventHandler
    fun onFill(event: PlayerBucketFillEvent) {
        if (event.isCancelled) return
        val block = event.blockClicked

        val lands = landManager.getLandsWithChunk(block.chunk) ?: return

        val player = event.player
        if (player.uniqueId in lands.memberMap) {
            val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> return
                Rank.MEMBER -> lands.memberSetting
                Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                Rank.VISITOR -> lands.visitorSetting
            }

            if (setting.useBucket)
                event.isCancelled = true
        } else event.isCancelled = true

        if (event.isCancelled)
            player.sendMessage("권한이 없습니다!".errorFormat())
    }
}