package com.salkcoding.tunalands.util

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import org.bukkit.*
import org.bukkit.entity.Player

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

    val chunks: List<Pair<Int, Int>> = landList.map { query ->
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
    chunks.forEach { (x, z) ->
        val a = x - xMin + 1
        val b = z - zMin + 1

        array[a][b] = 1
    }

    //Perform flood fill
    calculateFloodFill(array, 0, 0, xLen, zLen)

    //Check and return value
    array.forEach { subArray ->
        subArray.forEach { element ->
            if (element == 0) {
                return false
            }
        }
    }

    // 2. Check that chunks are all connected
    val connectedCheckArray = Array(xMax - xMin + 1) { Array(zMax - zMin + 1) { 0 } }
    var cx: Int = 0
    var cz: Int = 0
    chunks.forEach { (x, z) ->
        connectedCheckArray[x - xMin][z - zMin] = 1
        cx = x - xMin
        cz = z - zMin
    }

    isLandsDisconnected(connectedCheckArray, cx, cz, xMax - xMin + 1, zMax - zMin + 1)

    connectedCheckArray.forEach { subArray ->
        subArray.forEach { element ->
            if (element == 1) {
                return false
            }
        }
    }

    return true
}

fun isLandsDisconnected(array: Array<Array<Int>>, x: Int, z: Int, xLimit: Int, zLimit: Int) {
    if (x < 0 || x >= xLimit || z < 0 || z >= zLimit) return
    if (array[x][z] != 1) return

    array[x][z] = 2

    isLandsDisconnected(array, x, z - 1, xLimit, zLimit)//South
    isLandsDisconnected(array, x, z + 1, xLimit, zLimit)//North
    isLandsDisconnected(array, x - 1, z, xLimit, zLimit)//West
    isLandsDisconnected(array, x + 1, z, xLimit, zLimit)//East
}

/*
DO NOT USE the tailrec keyword! It makes performance worst.
And a recursion flood fill is more efficient than an iterator flood fill(e.g. using queue for BFS)
*/
fun calculateFloodFill(array: Array<Array<Int>>, x: Int, z: Int, xLimit: Int, zLimit: Int) {
    if (x < 0 || x >= xLimit || z < 0 || z >= zLimit) return
    if (array[x][z] != 0) return

    array[x][z] = 1

    calculateFloodFill(array, x, z - 1, xLimit, zLimit)//South
    calculateFloodFill(array, x, z + 1, xLimit, zLimit)//North
    calculateFloodFill(array, x - 1, z, xLimit, zLimit)//West
    calculateFloodFill(array, x + 1, z, xLimit, zLimit)//East
}