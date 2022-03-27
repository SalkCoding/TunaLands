package com.salkcoding.tunalands.display

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.gmail.filoghost.holographicdisplays.api.line.TextLine
import com.salkcoding.tunalands.displayManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import org.bukkit.Bukkit
import org.bukkit.ChatColor

class TimerDisplay(
    private val lands: Lands
) : Display() {

    override fun create() {
        val location = lands.upCoreLocation.toCenterLocation()
        location.y += 1

        hologram = HologramsAPI.createHologram(tunaLands, location)
        hologram.appendTextLine(lands.landsName)
        hologram.appendTextLine("준비중...")
    }

    //If returns false this instance going to pause
    override fun update(): Boolean {
        try {
            if (hologram.isDeleted){
                displayManager.removeDisplay(lands)
                return true
            }
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("Hologram not initialized!")
        }

        val expired = lands.expiredMillisecond - System.currentTimeMillis()
        if (expired > 0) {
            val days = expired / 86400000
            val hours = (expired / 3600000) % 24
            val minutes = (expired / 60000) % 60
            val seconds = (expired / 1000) % 60
            //Sync invoke
            Bukkit.getScheduler().runTask(tunaLands, Runnable {
                //Flicker prevent
                if(hologram.isDeleted) {
                    displayManager.removeDisplay(lands)
                    return@Runnable
                }
                val line = hologram.getLine(1) as TextLine
                line.text = when {
                    days > 0 -> "남은 연료: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초"
                    hours > 0 -> "남은 연료: ${hours}시간 ${minutes}분 ${seconds}초"
                    minutes > 0 -> "남은 연료: ${minutes}분 ${seconds}초"
                    seconds > 0 -> "남은 연료: ${seconds}초"
                    else -> "준비중..."
                }
            })
        } else return false

        return true
    }

    override fun pause() {
        val line = hologram.getLine(1) as TextLine
        line.text = "${ChatColor.RED}비활성화"
        isPaused = true
    }

    override fun resume() {
        update()
        isPaused = false
    }

    override fun remove() {
        try {
            hologram.delete()
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("Hologram not created!")
        }
    }
}