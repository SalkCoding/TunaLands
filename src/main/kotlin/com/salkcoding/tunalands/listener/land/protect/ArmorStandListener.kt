package com.salkcoding.tunalands.listener.land.protect

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.util.sendErrorTipMessage
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerArmorStandManipulateEvent

class ArmorStandListener : Listener {

    @EventHandler
    fun onArmorStand(event: PlayerArmorStandManipulateEvent) {
        if (event.isCancelled) return
        if (event.player.isOp) return

        val player = event.player
        if (player.world.name in configuration.ignoreWorld) return

        val lands = landManager.getLandsWithChunk(player.chunk)
        if (lands == null) {
            player.sendErrorTipMessage("${ChatColor.RED}중립 지역에서는 갑옷거치대를 사용할 수 없습니다!")
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

            if (!setting.useArmorStand)
                event.isCancelled = true
        } else event.isCancelled = true

        if (event.isCancelled)
            player.sendErrorTipMessage("${ChatColor.RED}권한이 없습니다!")
    }
}