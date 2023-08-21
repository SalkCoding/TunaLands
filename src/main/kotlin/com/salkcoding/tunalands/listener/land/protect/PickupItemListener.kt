package com.salkcoding.tunalands.listener.land.protect

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.sendErrorTipMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import java.util.*

class PickupItemListener : Listener {

    private val messageSet = mutableSetOf<UUID>()

    @EventHandler
    fun onPickupItem(event: EntityPickupItemEvent) {
        if (event.isCancelled) return
        if (event.entity.isOp) return

        val lands = landManager.getLandsWithChunk(event.item.chunk) ?: return
        val player = event.entity as? Player ?: return

        if (player.uniqueId in lands.memberMap) {
            val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> return
                Rank.MEMBER -> if (lands.enable) lands.memberSetting else return
                Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                Rank.VISITOR -> lands.visitorSetting
            }

            if (!setting.pickupItem)
                event.isCancelled = true
        } else event.isCancelled = true

        if (event.isCancelled && player.uniqueId !in messageSet) {
            messageSet.add(player.uniqueId)
            player.sendErrorTipMessage("${ChatColor.RED}권한이 없습니다!")
            Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                messageSet.remove(player.uniqueId)
            }, 100)
        }
    }

}