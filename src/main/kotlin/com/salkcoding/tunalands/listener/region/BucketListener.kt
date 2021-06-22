package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.sendErrorTipMessage
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent

class BucketListener : Listener {

    @EventHandler
    fun onEmpty(event: PlayerBucketEmptyEvent) {
        if (event.isCancelled) return
        if (event.player.isOp) return

        val block = event.blockClicked
        val lands = landManager.getLandsWithChunk(block.chunk)
        if (lands == null) {
            event.player.sendErrorTipMessage("${ChatColor.RED}중립 지역에서는 양동이를 사용할 수 없습니다!")
            event.isCancelled = true
            return
        }

        val player = event.player
        if (!lands.enable) {
            player.sendMessage("땅을 다시 활성화 해야합니다!".errorFormat())
            event.isCancelled = true
            return
        }

        if (player.uniqueId in lands.memberMap) {
            val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> return
                Rank.MEMBER -> lands.memberSetting
                Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                Rank.VISITOR -> lands.visitorSetting
            }

            if (!setting.useBucket)
                event.isCancelled = true
        } else event.isCancelled = true

        if (event.isCancelled)
            player.sendErrorTipMessage("${ChatColor.RED}권한이 없습니다!")
    }

    @EventHandler
    fun onFill(event: PlayerBucketFillEvent) {
        if (event.isCancelled) return
        if (event.player.isOp) return

        val player = event.player
        val block = event.blockClicked
        val lands = landManager.getLandsWithChunk(block.chunk)
        if (lands == null) {
            player.sendErrorTipMessage("${ChatColor.RED}중립 지역에서는 양동이를 사용할 수 없습니다!")
            event.isCancelled = true
            return
        }

        if (player.uniqueId in lands.memberMap) {
            val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> return
                Rank.MEMBER -> lands.memberSetting
                Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                Rank.VISITOR -> lands.visitorSetting
            }

            if (!setting.useBucket)
                event.isCancelled = true
        } else event.isCancelled = true

        if (event.isCancelled)
            player.sendErrorTipMessage("${ChatColor.RED}권한이 없습니다!")
    }
}