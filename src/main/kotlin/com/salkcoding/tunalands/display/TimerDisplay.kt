package com.salkcoding.tunalands.display

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.gmail.filoghost.holographicdisplays.api.line.TextLine
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

    override fun update(): Boolean {
        try {
            if (hologram.isDeleted)
                throw IllegalStateException("Hologram already deleted!")
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
                val line = hologram.getLine(1) as TextLine
                when {
                    days > 0 -> line.text = "남은 연료: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초"
                    hours > 0 -> line.text = "남은 연료: ${hours}시간 ${minutes}분 ${seconds}초"
                    minutes > 0 -> line.text = "남은 연료: ${minutes}분 ${seconds}초"
                    seconds > 0 -> line.text = "남은 연료: ${seconds}초"
                }
            })
        } else return false

        return true
    }

    override fun pause() {
        val line = hologram.getLine(1) as TextLine
        line.text = "${ChatColor.RED}비활성화"
        pause = true
    }

    override fun resume() {
        update()
        pause = false
    }

    override fun remove() {
        try {
            hologram.delete()
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("Hologram not created!")
        }
    }
}