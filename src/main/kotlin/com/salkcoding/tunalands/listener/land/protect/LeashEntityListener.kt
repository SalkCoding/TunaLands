package com.salkcoding.tunalands.listener.land.protect

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.sendErrorTipMessage
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerLeashEntityEvent

class LeashEntityListener : Listener {

    @EventHandler
    fun onEntityInteract(event: PlayerLeashEntityEvent) {
        if (event.isCancelled) return
        if (event.player.isOp) return

        val lands = landManager.getLandsWithChunk(event.entity.chunk) ?: return
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

            if (!setting.useLead)
                event.isCancelled = true
        } else event.isCancelled = true

        if (event.isCancelled)
            player.sendErrorTipMessage("${ChatColor.RED}권한이 없습니다!")
    }
}