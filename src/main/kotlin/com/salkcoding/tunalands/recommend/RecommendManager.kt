package com.salkcoding.tunalands.recommend

import com.salkcoding.tunalands.file.PlayerRecommendMapReader
import com.salkcoding.tunalands.file.PlayerRecommendMapWriter
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import org.bukkit.Bukkit
import java.util.*

class RecommendManager(reset: Long, private val cooldown: Long) {

    private val recommendMap = PlayerRecommendMapReader.loadPlayerRecommendMap()

    private var nextReset = System.currentTimeMillis() + reset
    private val task = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, Runnable {
        val present = System.currentTimeMillis()
        if (nextReset < present) {
            nextReset = present + reset
            landManager.getPlayerLandMap().forEach { (_, lands) ->
                lands.recommend = 0
            }
        }
    }, 1200, 1200)

    fun recommend(uuid: UUID, lands: Lands) {
        if (uuid !in recommendMap) {
            recommendMap[uuid] = System.currentTimeMillis() + cooldown
            lands.recommend += 1
        }
    }

    fun canRecommend(uuid: UUID): Boolean {
        if (uuid !in recommendMap) return true

        return recommendMap[uuid]!! < System.currentTimeMillis()
    }

    fun remainMilliseconds(uuid: UUID): Long {
        return when (uuid in recommendMap) {
            true -> recommendMap[uuid]!! - System.currentTimeMillis()
            false -> 0
        }
    }

    fun resetMilliseconds(uuid: UUID) {
        recommendMap.remove(uuid)
    }

    fun dispose() {
        task.cancel()
        PlayerRecommendMapWriter.savePlayerRecommendMap()
    }

    fun getRecommendMap() = recommendMap
}