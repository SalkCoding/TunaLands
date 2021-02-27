package com.salkcoding.tunalands.util

import com.salkcoding.tunalands.data.lands.Lands
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
* return
*   true: After performed,when a empty area is not existed
*   false: After performed,when a empty area is existed
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
    floodFill(array, 0, 0, xLen, zLen)

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
Max array input almost 75x75(5625chunks).
If pass over value then function throws StackOverFlowException
*/

fun floodFill(array: Array<Array<Int>>, x: Int, y: Int, xLimit: Int, zLimit: Int) {
    if (x < 0 || y < 0) return
    if (xLimit <= x || zLimit <= y) return
    if (array[x][y] == 1) return
    if (array[x][y] != 0) return
    array[x][y] = 1
    floodFill(array, x, y - 1, xLimit, zLimit)//South
    floodFill(array, x, y + 1, xLimit, zLimit)//North
    floodFill(array, x - 1, y, xLimit, zLimit)//West
    floodFill(array, x + 1, y, xLimit, zLimit)//East
}

/*  Implements with recursive function
*   Better performance
*   Worst memory efficiency
fun floodFill(array: Array<Array<Int>>, x: Int, y: Int, xLimit: Int, yLimit: Int) {
    if (x < 0 || y < 0) return
    if (xLimit <= x || yLimit <= y) return
    if (array[x][y] == 1) return
    if (array[x][y] != 0) return
    array[x][y] = 1
    floodFill(array, x, y - 1, xLimit, yLimit)//South
    floodFill(array, x, y + 1, xLimit, yLimit)//North
    floodFill(array, x - 1, y, xLimit, yLimit)//West
    floodFill(array, x + 1, y, xLimit, yLimit)//East
}*/

/*  Implements with queue
*   Better memory efficiency
*   Worst performance
fun floodFill(array: Array<Array<Int>>, x: Int, y: Int, xLimit: Int, yLimit: Int) {
    val queue: Queue<Point> = LinkedList()
    queue.add(Point(x, y))
    while (queue.isNotEmpty()) {
        val node = queue.remove()
        if (array[node.x][node.y] == 0) {
            array[node.x][node.y] = 1
            if (node.x < xLimit)
                queue.add(Point(node.x + 1, node.y))//East
            if (0 < node.x)
                queue.add(Point(node.x - 1, node.y))//West
            if (node.y < yLimit)
                queue.add(Point(node.x, node.y + 1))//North
            if (0 < node.y)
                queue.add(Point(node.x, node.y - 1))//South
        }
    }
}
data class Point(val x: Int, val y: Int)
*/