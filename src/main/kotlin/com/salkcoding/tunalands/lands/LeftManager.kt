package com.salkcoding.tunalands.lands

import java.util.*

class LeftManager(private val cooldown: Long) {

    private val leftMap = mutableMapOf<UUID, Long>()

    fun canRejoin(uuid: UUID): Boolean {
        if (uuid !in leftMap)
            return true

        return leftMap[uuid]!! < System.currentTimeMillis()
    }

    fun recordLeft(uuid: UUID) {
        leftMap[uuid] =
            System.currentTimeMillis() + cooldown
    }

    fun getRejoinCooldown(uuid: UUID): Long? {
        return leftMap[uuid]
    }
}

