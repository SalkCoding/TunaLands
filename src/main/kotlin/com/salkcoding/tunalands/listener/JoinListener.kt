package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener : Listener {

    @EventHandler
    fun onJoinAlarm(event: PlayerJoinEvent) {
        val player = event.player
        val lands = landManager.getPlayerLands(player.uniqueId) ?: return
        val fuelLeft = lands.fuelLeft / lands.secondPerFuel

        val days = (fuelLeft / 86400).toLong()
        val hours = ((fuelLeft / 3600) % 24).toLong()
        val minutes = ((fuelLeft / 60) % 60).toLong()
        val seconds = (fuelLeft % 60).toLong()
        val timeLeft = when {
            days > 0 -> "${ChatColor.WHITE}남은 연료: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초 남음"
            hours > 0 -> "${ChatColor.WHITE}남은 연료: ${hours}시간 ${minutes}분 ${seconds}초 남음"
            minutes > 0 -> "${ChatColor.WHITE}남은 연료: ${minutes}분 ${seconds}초 남음"
            seconds > 0 -> "${ChatColor.WHITE}남은 연료: ${seconds}초 남음"
            else -> "${ChatColor.RED}남은 연료: 0초 남음"
        }
        player.sendMessage(timeLeft.infoFormat())
    }
}