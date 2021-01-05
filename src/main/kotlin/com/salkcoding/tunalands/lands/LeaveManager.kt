package com.salkcoding.tunalands.lands

import com.salkcoding.tunalands.configuration
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

private val leftMap = mutableMapOf<UUID, Long>()

fun OfflinePlayer.canRejoin(): Boolean {
    if (this.uniqueId !in leftMap)
        return true

    val left = Calendar.getInstance()
    left.timeInMillis = leftMap[this.uniqueId]!!
    val present = Calendar.getInstance()
    return present.before(left)
}

fun OfflinePlayer.recordLeft() {
    leftMap[this.uniqueId] = System.currentTimeMillis() + (configuration.command.rejoinCooldown * 50)
}

fun OfflinePlayer.getRejoinCooldown(): Long? {
    return leftMap[this.uniqueId]
}