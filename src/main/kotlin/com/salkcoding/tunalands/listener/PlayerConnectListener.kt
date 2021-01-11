package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.bungee.proxyPlayerSet
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.tunaLands
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerConnectListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        proxyPlayerSet.add(event.player.uniqueId)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val uuid = event.player.uniqueId
        proxyPlayerSet.remove(uuid)
        val landsList = landManager.getPlayerLandsList(uuid)
        landsList.forEach { lands ->
            lands.memberMap[uuid]!!.lastLogin = System.currentTimeMillis()
        }
    }
}