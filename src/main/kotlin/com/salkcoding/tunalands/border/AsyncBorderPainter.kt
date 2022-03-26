package com.salkcoding.tunalands.border

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.borderFinder
import com.salkcoding.tunalands.util.splitQuery
import org.bukkit.Bukkit
import org.bukkit.Particle

class AsyncBorderPainter : Runnable {
    override fun run() {
        landManager.getPlayerLandMap().values.forEach { lands ->
            if (!lands.enable) return@forEach

            val borderList = lands.borderFinder()
            if (borderList.isEmpty()) return@forEach

            Bukkit.getScheduler().runTask(tunaLands, Runnable {
                val world = lands.upCoreLocation.world
                borderList.forEach { coordinate ->
                    val split = coordinate.splitQuery()
                    val x = split.first
                    val z = split.second

                    if (world.isChunkLoaded(x, z)) {
                        val location = world.getHighestBlockAt(x, z).location.add(0.0, 3.0, 0.0)
                        world.spawnParticle(Particle.FIREWORKS_SPARK, location, 0, .0, .0, .0, .0, null, true)
                    }
                }
            })
        }
    }
}