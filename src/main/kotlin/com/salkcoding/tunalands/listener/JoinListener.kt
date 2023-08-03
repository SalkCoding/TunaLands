package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

class JoinListener : Listener {

    @EventHandler
    fun onJoinAlarm(event: PlayerJoinEvent) {
        val player = event.player
        val lands = landManager.getPlayerLands(player.uniqueId) ?: return

        val timeLeftInDay = lands.fuelLeft / lands.dayPerFuel
        val expired =
            LocalDateTime.now().plusDays(timeLeftInDay.toLong()).withHour(6).withMinute(0).withSecond(0).withNano(0)
        val between = Duration.between(LocalDateTime.now(), expired)
        val timeLeftInMilliseconds = if (between.isNegative) 0 else between.toMillis()

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