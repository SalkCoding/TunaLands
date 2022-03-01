package com.salkcoding.tunalands.util

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import org.bukkit.*
import org.bukkit.entity.Player
import java.util.*

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
    val landList = this.landList
    val borderList = mutableListOf<String>()
    landList.forEach { chunk ->
        val split = chunk.splitQuery()
        val x = split.first
        val z = split.second

        val blockX = x shl 4
        val blockZ = z shl 4

        //Checking sides
        if ("${x - 1}:$z" !in landList) {//Left side
            val leftBottom = "${blockX}:${blockZ}"
            if (leftBottom !in borderList)
                borderList.add(leftBottom)

            val leftTop = "${blockX}:${blockZ + 16}"
            if (leftTop !in borderList)
                borderList.add(leftTop)
        }
        if ("$x:${z + 1}" !in landList) {//Top side
            val leftTop = "${blockX}:${blockZ + 16}"
            if (leftTop !in borderList)
                borderList.add(leftTop)

            val rightTop = "${blockX + 16}:${blockZ + 16}"
            if (rightTop !in borderList)
                borderList.add(rightTop)
        }
        if ("${x + 1}:$z" !in landList) {//Right side
            val rightTop = "${blockX + 16}:${blockZ + 16}"
            if (rightTop !in borderList)
                borderList.add(rightTop)

            val rightBottom = "${blockX + 16}:${blockZ}"
            if (rightBottom !in borderList)
                borderList.add(rightBottom)
        }
        if ("$x:${z - 1}" !in landList) {//Bottom side
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
fun Lands.checkFloodFill(): Boolean {
    if (landList.isEmpty()) return true

    val xList = mutableListOf<Int>()
    val zList = mutableListOf<Int>()
    landList.forEach { query ->
        val result = query.splitQuery()
        xList.add(result.first)
        zList.add(result.second)
    }
    val xMin = xList.minByOrNull { it }!!
    val zMin = zList.minByOrNull { it }!!

    //Array
    val xLen = xList.toSet().size + 2
    val zLen = zList.toSet().size + 2
    val array = Array(xLen) { Array(zLen) { 0 } }

    //Setting array (xList size always equals with yList size)
    for (i in 0 until xList.size) {
        val x = xList[i] + (-xMin) + 1
        val y = zList[i] + (-zMin) + 1

        array[x][y] = 1
    }

    //Perform flood fill
    calculateFloodFill(array, 0, 0, xLen, zLen)

    //Check and return value
    array.forEach { subArray ->
        subArray.forEach { element ->
            if (element == 0)
                return false
        }
    }
    return true
}

/*
DO NOT USE the tailrec keyword! It makes performance worst.
And a recursion flood fill is more efficient than an iterator flood fill(e.g. using queue for BFS)
*/
fun calculateFloodFill(array: Array<Array<Int>>, x: Int, y: Int, xLimit: Int, zLimit: Int) {
    if (x < 0 || y < 0) return
    if (xLimit <= x || zLimit <= y) return
    if (array[x][y] == 1) return
    if (array[x][y] != 0) return
    array[x][y] = 1
    calculateFloodFill(array, x, y - 1, xLimit, zLimit)//South
    calculateFloodFill(array, x, y + 1, xLimit, zLimit)//North
    calculateFloodFill(array, x - 1, y, xLimit, zLimit)//West
    calculateFloodFill(array, x + 1, y, xLimit, zLimit)//East
}