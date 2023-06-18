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
    landMap.forEach { (query, _) ->
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
private val dx = arrayOf(1, 0, -1, 0)
private val dz = arrayOf(0, 1, 0, -1)
fun Lands.checkFloodFill(): Boolean {
    val chunks: List<Pair<Int, Int>> = landMap.map { (query, _) ->
        val result = query.splitQuery()
        Pair(result.first, result.second)
    }

    val xMin = chunks.minOf { it.first }
    val zMin = chunks.minOf { it.second }

    val xMax = chunks.maxOf { it.first }
    val zMax = chunks.maxOf { it.second }

    //Array
    val xLen = xMax - xMin + 3
    val zLen = zMax - zMin + 3
    val array = Array(xLen) { Array(zLen) { 0 } }

    // 1. Check that chunks don't do 땅따먹기

    //Setting array (xList size always equals with yList size)
    //사이드 한칸씩 띄워져 있음
    chunks.forEach { (x, z) ->
        val a = x - xMin + 1
        val b = z - zMin + 1

        array[a][b] = 1
    }

    //BFS search
    val queue: Queue<Pair<Int, Int>> = LinkedList()
    val visited = Array(xLen) { Array(zLen) { 0 } }
    queue.add(Pair(0, 0))
    visited[0][0] = 1
    while (queue.isNotEmpty()) {
        val now = queue.remove()
        for (i in 0..3) {
            val nx = dx[i] + now.first
            val nz = dz[i] + now.second
            if (nx < 0 || nz < 0 || nx >= xLen || nz >= zLen || array[nx][nz] == 1 || visited[nx][nz] == 1) continue
            queue.add(Pair(nx, nz))
            visited[nx][nz] = 1
        }
    }

    for (i in 0 until xLen) {
        for (j in 0 until zLen) {
            if (array[i][j] == 0 && visited[i][j] == 0)
                return false
        }
    }

    return true
}