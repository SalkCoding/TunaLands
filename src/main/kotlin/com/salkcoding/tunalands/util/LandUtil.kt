package com.salkcoding.tunalands.util

import com.salkcoding.tunalands.lands.Lands
import kotlin.math.abs

/*
* return
*   true: After performed,when a empty area is not existed
*   false: After performed,when a empty area is existed
*/
fun Lands.checkFloodFill(): Boolean {
    val xList = mutableListOf<Int>()
    val yList = mutableListOf<Int>()
    landList.forEach { query ->
        val split = query.split(":")
        xList.add(split[0].toInt())
        yList.add(split[1].toInt())
    }
    val xMin = abs(xList.minByOrNull { it }!!) + 1
    val xMax = abs(xList.maxByOrNull { it }!!) + 1
    val yMin = abs(yList.minByOrNull { it }!!) + 1
    val yMax = abs(yList.maxByOrNull { it }!!) + 1

    //Array[xMin+xMax+1][yMin+yMax+1]
    val xLen = xMin + xMax + 1
    val yLen = yMin + yMax + 1
    val array = Array(xLen) { Array(yLen) { 0 } }

    //Setting array (xList size always equals with yList size)
    for (i in 0 until xList.size) {
        val x = xList[i] + xMin
        val y = yList[i] + yMin

        array[x][y] = 1
    }

    //Perform flood fill
    floodFill(array, 0, 0, xLen, yLen)

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