package com.salkcoding.tunalands.listener.land.protect

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.sendErrorTipMessage
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class PVPListener : Listener {

    @EventHandler
    fun onPVP(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        if (event.damager.isOp) return

        var damager = event.damager
        damager = if (damager is Projectile)
            damager.shooter as? Player ?: return
        else
            damager as? Player ?: return

        val lands = landManager.getLandsWithChunk(damager.chunk) ?: return

        if (!lands.enable) return

        if (damager.uniqueId in lands.memberMap) {
            val damagerSetting = when (lands.memberMap[damager.uniqueId]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> return
                Rank.MEMBER -> lands.memberSetting
                Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                Rank.VISITOR -> lands.visitorSetting
            }

            if (!damagerSetting.canPVP)
                event.isCancelled = true
        } else event.isCancelled = true

        if (event.isCancelled)
            damager.sendErrorTipMessage("${ChatColor.RED}권한이 없습니다!")
    }
}