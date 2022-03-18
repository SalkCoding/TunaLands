package com.salkcoding.tunalands.util

import com.salkcoding.tunalands.listener.ChunkEffectListener
import com.salkcoding.tunalands.tunaLands
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class ChunkEffect(val world: World, val chunk: Chunk, val type: Material) : Runnable {
    var task: BukkitTask? = null
    private val restoreMap = ConcurrentHashMap<String, BlockData>()
    private val startX = chunk.x shl 4
    private val startZ = chunk.z shl 4
    private val endX = startX + 15
    private val endZ = startZ + 15

    private var count = 0
    private val effectBlockData: BlockData
    private val effectedPlayers: List<Player?>

    init {
        ChunkEffectListener.effectSet.add(chunk.toQuery())
        effectBlockData = type.createBlockData()
        effectedPlayers = world.getNearbyPlayers(world.getHighestBlockAt((startX + endX)/2, (startZ + endZ)/2).location, 64.0).toList()
    }

    override fun run() {
        if (count < 50) {
            //Get a block until block is not same
            for (i in 0..1) {
                var x: Int = Random.nextInt(startX, endX)
                var z: Int = Random.nextInt(startZ, endZ)
                var block: Block = world.getHighestBlockAt(x, z)
                while (restoreMap.containsKey(block.toQuery())) {
                    x = Random.nextInt(startX, endX)
                    z = Random.nextInt(startZ, endZ)
                    block = world.getHighestBlockAt(x, z)
                }
                val query = block.toQuery()
                //Change
                restoreMap[query] = block.blockData
                Bukkit.getScheduler().runTask(tunaLands, Runnable {
                    effectedPlayers.filterNotNull().forEach { p->
                        p.sendBlockChange(block.location, effectBlockData)
                    }
                })
            }
            count += 2
        } else {
            //Restore and exit
            var tick = 1f
            restoreMap.forEach { (query, originalBlockData) ->
                Bukkit.getScheduler().runTaskLaterAsynchronously(tunaLands, Runnable {
                    val location = query.splitQuery()
                    val block = world.getBlockAt(location.first, location.second, location.third)
                    Bukkit.getScheduler().runTask(tunaLands, Runnable {

                        effectedPlayers.filterNotNull().forEach { p->
                            p.sendBlockChange(block.location, originalBlockData)
                        }
                    })
                }, tick.toLong())
                tick += 0.5f
            }
            ChunkEffectListener.effectSet.remove(chunk.toQuery())
            task!!.cancel()
            return
        }
    }

    private fun Block.toQuery(): String {
        return "${this.x}:${this.y}:${this.z}"
    }

    private fun String.splitQuery(): Triple<Int, Int, Int> {
        val split = this.split(":")
        if (split.isEmpty() || split.size > 3) throw IllegalArgumentException()

        try {
            //X, Y, Z
            return Triple(split[0].toInt(), split[1].toInt(), split[2].toInt())
        } catch (e: NumberFormatException) {
            throw java.lang.IllegalArgumentException("")
        }
    }
}