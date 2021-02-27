package com.salkcoding.tunalands.display

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.gmail.filoghost.holographicdisplays.api.line.TextLine
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.data.lands.Lands
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.toQuery
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Chunk
import org.bukkit.Material
import java.util.*

class DisplayManager {
    //Chunk query
    private val displayMap = mutableMapOf<String, Display>()

    fun createDisplay(lands: Lands) {
        val location = lands.upCore.toCenterLocation()
        location.y += 1
        val query = location.chunk.toQuery()
        //Prevent duplication
        if (query in displayMap) return

        val hologram = HologramsAPI.createHologram(tunaLands, location)
        hologram.appendTextLine("${ChatColor.GOLD}${lands.ownerName}${ChatColor.WHITE}의 지역")
        hologram.appendTextLine("남은 연료: NULL")
        val created = Calendar.getInstance()
        created.timeInMillis = lands.landHistory.createdMillisecond
        val task = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, Runnable {
            val expired = lands.expiredMillisecond - System.currentTimeMillis()
            if (expired <= 0) {
                Bukkit.getScheduler().runTask(tunaLands, Runnable {
                    lands.memberMap.forEach { (uuid, _) ->
                        val player = Bukkit.getPlayer(uuid) ?: return@forEach
                        player.sendMessage("보호 기간이 만료되어, 지역 보호가 해제됩니다.".warnFormat())
                    }
                    lands.upCore.block.type = Material.AIR
                    lands.downCore.block.type = Material.AIR
                    landManager.deleteLands(lands.ownerUUID, lands.ownerName)
                    val display = displayMap[query]!!
                    display.task.cancel()
                    display.hologram.delete()
                })
            } else {
                val days = expired / 86400000
                val hours = (expired / 3600000) % 24
                val minutes = (expired / 60000) % 60
                val seconds = (expired / 1000) % 60
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
            }
        }, 5, 20)
        displayMap[query] = Display(hologram, task)
    }

    fun removeDisplayInChunk(chunk: Chunk) {
        val query = chunk.toQuery()
        if (query in displayMap) {
            val display = displayMap[query]!!
            display.task.cancel()
            display.hologram.delete()
        }
    }

    fun deleteAll() {
        displayMap.forEach { (_, display) ->
            display.task.cancel()
            display.hologram.delete()
        }
    }
}