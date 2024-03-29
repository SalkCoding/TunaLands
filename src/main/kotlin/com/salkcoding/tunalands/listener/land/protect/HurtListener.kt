package com.salkcoding.tunalands.listener.land.protect

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class HurtListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onHurt(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        if (event.entity.isOp) return

        val victim = event.entity as? Player ?: return
        val lands = landManager.getLandsWithChunk(victim.chunk) ?: return

        if (!lands.enable) return

        if (victim.uniqueId in lands.memberMap) {
            val victimSetting = when (lands.memberMap[victim.uniqueId]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER -> lands.memberSetting
                Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                Rank.VISITOR -> lands.visitorSetting
            }

            if (!victimSetting.canHurt)
                event.isCancelled = true
        } else event.isCancelled = true
    }
}
