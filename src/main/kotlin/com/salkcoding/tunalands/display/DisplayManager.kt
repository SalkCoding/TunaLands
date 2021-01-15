package com.salkcoding.tunalands.display

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.toQuery
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

class DisplayManager {
    //Chunk query
    private val displayMap = mutableMapOf<String, Display>()

    fun createDisplay(location: Location, lands: Lands) {
        val hologram = HologramsAPI.createHologram(tunaLands, location)
        hologram.appendTextLine("${lands.ownerName}의 지역")
        hologram.appendTextLine("남은 연료: NULL")
        val query = location.chunk.toQuery()
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
                Bukkit.getScheduler().runTask(tunaLands, Runnable {
                    hologram.removeLine(1)
                    hologram.appendTextLine(
                        "남은 연료: ${
                            expired / 86400000
                        }일 ${
                            (expired / 3600000) % 24
                        }시간 ${
                            (expired / 60000) % 60
                        }분 ${
                            (expired / 1000) % 60
                        }초"
                    )
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
}