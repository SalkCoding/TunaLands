package com.salkcoding.tunalands.util

import org.bukkit.block.Block

fun Block.isSameLocation(worldName: String, x: Int, y: Int, z: Int): Boolean {
    return this.world.name == worldName && this.x == x && this.y == y && this.z == z
}