package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class PVPListener : Listener {

    @EventHandler
    fun onPVP(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return

        var damager = event.damager
        damager = if (damager is Projectile)
            damager.shooter as? Player ?: return
        else
            damager as? Player ?: return

        val lands = landManager.getLandsWithChunk(damager.chunk) ?: return
        if (!lands.enable) return

        val damagerSetting = when (lands.memberMap[damager.uniqueId]!!.rank) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            else -> lands.visitorSetting
        }

        if (!damagerSetting.canPVP) {
            damager.sendMessage("권한이 없습니다.".errorFormat())
            event.isCancelled = true
        }
    }
}