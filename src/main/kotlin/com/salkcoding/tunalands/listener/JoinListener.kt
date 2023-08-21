package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinListener : Listener {

    @EventHandler
    fun onJoinAlarm(event: PlayerJoinEvent) {
        val player = event.player
        val lands = landManager.getPlayerLands(player.uniqueId) ?: return

        if (!lands.enable) return

        val timeLeftInSeconds = lands.fuelSecLeft
        val timeLeft = "예상: ${when {
            timeLeftInSeconds > 0 -> {
                val days = timeLeftInSeconds / 86400
                val hours = (timeLeftInSeconds / 3600) % 24
                val minutes = (timeLeftInSeconds / 60) % 60
                val seconds = timeLeftInSeconds% 60

                when {
                    days > 0 -> "${days}일 ${hours}시간 ${minutes}분 ${seconds}초"
                    hours > 0 -> "${hours}시간 ${minutes}분 ${seconds}초"
                    minutes > 0 -> "${minutes}분 ${seconds}초"
                    seconds > 0 -> "${seconds}초"
                    else -> "0초"
                }
            }

            else -> "0초"
        }} 남음"
        player.sendMessage(timeLeft.infoFormat())
    }
}