package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
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
        val lands = landManager.getLandsWithChunk(player.chunk) ?: return
        if (!lands.enable) return

        when (event.entity.type) {
            EntityType.EGG -> {
                if (player.uniqueId in lands.memberMap) {
                    val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
                        Rank.OWNER, Rank.DELEGATOR -> return
                        Rank.MEMBER -> lands.memberSetting
                        Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                        Rank.VISITOR -> lands.visitorSetting
                    }

                    if (setting.throwEgg)
                        event.isCancelled = true
                } else event.isCancelled = true
            }
            else -> return
        }

        if (event.isCancelled)
            player.sendMessage("권한이 없습니다!".errorFormat())
    }
}