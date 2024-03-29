package com.salkcoding.tunalands.border

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.LandType
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.borderFinder
import com.salkcoding.tunalands.util.splitQuery
import org.bukkit.Bukkit
import org.bukkit.Particle

class AsyncBorderPainter : Runnable {
    override fun run() {
        landManager.getPlayerLandMap().values.forEach { lands ->
            if (!lands.enable) return@forEach

            val borderMap = lands.borderFinder()
            if (borderMap.isEmpty()) return@forEach

            Bukkit.getScheduler().runTask(tunaLands, Runnable {
                val world = lands.upCoreLocation.world
                borderMap.forEach { (type, borderList) ->
                    borderList.forEach { coordinate ->
                        val split = coordinate.splitQuery()
                        val x = split.first
                        val z = split.second

                        val chunkX = x shr 4
                        val chunkZ = z shr 4

                        if (world.isChunkLoaded(chunkX, chunkZ)) {
                            val location = world.getHighestBlockAt(x, z).location.add(0.0, 3.0, 0.0)
                            world.spawnParticle(
                                when (type) {
                                    LandType.NORMAL -> Particle.FIREWORKS_SPARK
                                    LandType.FARM -> Particle.VILLAGER_HAPPY
                                }, location, 0, .0, .0, .0, .0, null, true
                            )
                        }
                    }
                }
            })
        }
    }
}