package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.displayManager
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
        //이미 있으면 안만들어짐
        displayManager.createDisplay(lands)

        if (!lands.enable) return

        val timeLeftInMilliseconds = lands.getExpiredDateToMilliseconds()
        val timeLeft = when {
            timeLeftInMilliseconds > 0 -> {
                val days = timeLeftInMilliseconds / 86400000
                val hours = (timeLeftInMilliseconds / 3600000) % 24
                val minutes = (timeLeftInMilliseconds / 60000) % 60
                val seconds = (timeLeftInMilliseconds / 1000) % 60

                when {
                    days > 0 -> "예상: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초 남음"
                    hours > 0 -> "예상: ${hours}시간 ${minutes}분 ${seconds}초 남음"
                    minutes > 0 -> "예상: ${minutes}분 ${seconds}초 남음"
                    seconds > 0 -> "예상: ${seconds}초 남음"
                    else -> "예상: 0초 남음"
                }
            }

            else -> "예상: 0초 남음"
        }
        player.sendMessage(timeLeft.infoFormat())
    }
}