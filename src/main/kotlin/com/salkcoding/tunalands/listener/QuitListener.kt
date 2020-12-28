package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.landManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class QuitListener : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val lands = landManager.getPlayerLands(player.uniqueId) ?: return
        lands.memberMap[player.uniqueId]!!.lastLogin = System.currentTimeMillis()
    }
}