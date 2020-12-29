package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class HurtListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onHurt(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return

        val victim = event.entity as? Player ?: return
        val lands = landManager.getLandsWithChunk(victim.chunk) ?: return
        if (!lands.enable) return

        val victimSetting = when (lands.memberMap[victim.uniqueId]!!.rank) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            else -> lands.visitorSetting
        }

        if (!victimSetting.canHurt)
            event.isCancelled = true
    }
}