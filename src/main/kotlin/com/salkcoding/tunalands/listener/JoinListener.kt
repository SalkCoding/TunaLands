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

        val expired = lands.getEstimatedMillisecondsLeftWithCurrentFuel()
        if (expired > 0) {
            val days = expired / 86400000
            val hours = (expired / 3600000) % 24
            val minutes = (expired / 60000) % 60
            val seconds = (expired / 1000) % 60
            lateinit var text: String
            when {
                days > 0 -> text = "남은 연료: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초 (예상)"
                hours > 0 -> text = "남은 연료: ${hours}시간 ${minutes}분 ${seconds}초 (예상)"
                minutes > 0 -> text = "남은 연료: ${minutes}분 ${seconds}초 (예상)"
                seconds > 0 -> text = "남은 연료: ${seconds}초 (예상)"
            }
            player.sendMessage(text.infoFormat())
        }
    }
}