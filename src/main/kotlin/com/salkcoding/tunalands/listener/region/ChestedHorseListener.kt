package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.entity.ChestedHorse
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class ChestedHorseListener : Listener {

    @EventHandler
    fun onChestOpen(event: PlayerInteractEntityEvent) {
        if (event.isCancelled) return

        val entity = event.rightClicked as? ChestedHorse ?: return
        val lands = landManager.getLandsWithChunk(entity.chunk) ?: return
        if (!lands.enable) return

        val player = event.player
        val setting = when (lands.getRank(player.uniqueId)) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            Rank.VISITOR -> lands.visitorSetting
            else -> null
        } ?: return

        if (!setting.useChestedHorse) {
            player.sendMessage("You don't have a permission!".errorFormat())
            event.isCancelled = true
        }
    }
}