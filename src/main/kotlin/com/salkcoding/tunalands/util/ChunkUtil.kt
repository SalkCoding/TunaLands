package com.salkcoding.tunalands.util

import org.bukkit.Chunk
import org.bukkit.Location

fun Chunk.toQuery(): String {
    return "${this.x}:${this.z}"
}

fun Location.toChunkQuery(): String {
    return "${this.blockX shr 4}:${this.blockZ shr 4}"
}

fun String.splitQuery(): Pair<Int, Int> {
    val split = this.split(":")
    if (split.isEmpty() || split.size > 2) throw IllegalArgumentException()

    try {
        //X, Z
        return Pair(split[0].toInt(), split[1].toInt())
    } catch (e: NumberFormatException) {
        throw java.lang.IllegalArgumentException("")
    }
}

fun Chunk.isMeetOtherChunk(chunkList: List<String>): Boolean {
    val left = "${this.x + 1}:${this.z}"
    val top = "${this.x}:${this.z + 1}"
    val right = "${this.x - 1}:${this.z}"
    val bottom = "${this.x}:${this.z - 1}"

    return (left in chunkList) or
            (top in chunkList) or
            (right in chunkList) or
            (bottom in chunkList)
}

fun Chunk.isSameChunk(other: Chunk): Boolean {
    return (this.world.name == other.world.name && this.x == other.x && this.z == other.z)
}