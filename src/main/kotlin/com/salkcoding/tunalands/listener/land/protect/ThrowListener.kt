package com.salkcoding.tunalands.listener.land.protect

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.sendErrorTipMessage
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent

class ThrowListener : Listener {

    @EventHandler
    fun onEggThrow(event: ProjectileLaunchEvent) {
        if (event.isCancelled) return
        val player = event.entity.shooter as? Player ?: return
        if (player.isOp) return

        val lands = landManager.getLandsWithChunk(player.chunk) ?: return
        if (!lands.enable) {
            player.sendMessage("땅을 다시 활성화 해야합니다!".errorFormat())
            event.isCancelled = true
            return
        }

        when (event.entity.type) {
            EntityType.EGG -> {
                if (player.uniqueId in lands.memberMap) {
                    val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
                        Rank.OWNER, Rank.DELEGATOR -> return
                        Rank.MEMBER -> lands.memberSetting
                        Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                        Rank.VISITOR -> lands.visitorSetting
                    }

                    if (!setting.throwEgg)
                        event.isCancelled = true
                } else event.isCancelled = true
            }
            else -> return
        }

        if (event.isCancelled)
            player.sendErrorTipMessage("${ChatColor.RED}권한이 없습니다!")
    }
}