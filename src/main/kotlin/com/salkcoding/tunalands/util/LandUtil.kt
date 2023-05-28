package com.salkcoding.tunalands.util

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import org.bukkit.*
import org.bukkit.entity.Player
import java.util.LinkedList
import java.util.Queue

fun World.playBuyChunkEffect(player: Player, chunk: Chunk) {
    val effect = ChunkEffect(this, chunk, Material.LIME_TERRACOTTA)
    effect.task = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, effect, 0, 1)
    for (i in 0..2) {
        Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f + (i * 0.5f))
        }, i * 10L)
    }
}

fun World.playSellChunkEffect(player: Player, chunk: Chunk) {
    val effect = ChunkEffect(this, chunk, Material.RED_TERRACOTTA)
    effect.task = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, effect, 0, 1)
    player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1f)
}

/*
Check borders of lands with landList
It checks coordinates clockwise and finding edge of lands
*/
fun Lands.borderFinder(): List<String> {
    val landMap = this.landMap
    val borderList = mutableListOf<String>()
    landMap.forEach { (query, type) ->
        val split = query.splitQuery()
        val x = split.first
        val z = split.second

        val blockX = x shl 4
        val blockZ = z shl 4

        //Checking sides
        if ("${x - 1}:$z" !in landMap) {//Left side
            val leftBottom = "${blockX}:${blockZ}"
            if (leftBottom !in borderList)
                borderList.add(leftBottom)

            val leftTop = "${blockX}:${blockZ + 16}"
            if (leftTop !in borderList)
                borderList.add(leftTop)
        }
        if ("$x:${z + 1}" !in landMap) {//Top side
            val leftTop = "${blockX}:${blockZ + 16}"
            if (leftTop !in borderList)
                borderList.add(leftTop)

            val rightTop = "${blockX + 16}:${blockZ + 16}"
            if (rightTop !in borderList)
                borderList.add(rightTop)
        }
        if ("${x + 1}:$z" !in landMap) {//Right side
            val rightTop = "${blockX + 16}:${blockZ + 16}"
            if (rightTop !in borderList)
                borderList.add(rightTop)

            val rightBottom = "${blockX + 16}:${blockZ}"
            if (rightBottom !in borderList)
                borderList.add(rightBottom)
        }
        if ("$x:${z - 1}" !in landMap) {//Bottom side
            val rightBottom = "${blockX + 16}:${blockZ}"
            if (rightBottom !in borderList)
                borderList.add(rightBottom)

            val leftBottom = "${blockX}:${blockZ}"
            if (leftBottom !in borderList)
                borderList.add(leftBottom)
        }
    }
    return borderList
}

/*
* return
*   true: After performed,when an empty area is not existed
*   false: After performed,when an empty area is existed
*/
fun Lands.hasConnectedComponent(): Boolean {
    if (landMap.isEmpty()) return true

    val chunks: List<Pair<Int, Int>> = landMap.map { (query,_) ->
        val result = query.splitQuery()
        Pair(result.first, result.second)
    }

    val xMin = chunks.minOf { it.first }
    val zMin = chunks.minOf { it.second }

    val xMax = chunks.maxOf { it.first }
    val zMax = chunks.maxOf { it.second }

    //Array size: 0 ~ n
    val xLen = xMax - xMin + 1
    val zLen = zMax - zMin + 1
    val array = Array(xLen) { Array(zLen) { 0 } }

    //Setting array (0 based indexing)
    val occupied = 1
    for ((x, z) in chunks) {
        array[x][z] = occupied
    }
    //Perform BFS, change 1 to 2 about a connected component
    calculateFloodFill(array, chunks[0], xLen, zLen, 0)

    //Check connected components
    for (i in 0..xLen + 1) {
        for (j in 0..zLen + 1) {
            //Check connected components are existed
            if (array[i][j] == occupied) return true
        }
    }
    return false
}


//Check Connected components, implement using BFS
private val dx = arrayOf(1, 0, -1, 0)
private val dz = arrayOf(0, 1, 0, -1)
private val queue = LinkedList<Pair<Int, Int>>() as Queue<Pair<Int, Int>>
fun calculateFloodFill(visited: Array<Array<Int>>, start: Pair<Int, Int>, xLimit: Int, zLimit: Int, fillValue: Int) {
    //Init
    queue.clear()
    queue.add(start)
    visited[start.first][start.second] = fillValue
    //BFS
    while (queue.isNotEmpty()) {
        val pair = queue.remove()
        for (i in 0 until 4) {
            val x = pair.first + dx[i]
            val z = pair.second + dz[i]

            //Out of boundary
            if (x < 0 || x >= xLimit || z < 0 || z >= zLimit || visited[x][z] == fillValue) continue

            queue.add(Pair(x, z))
            visited[x][z] = fillValue
        }
    }
}