package com.salkcoding.tunalands.lands

import com.salkcoding.tunalands.file.PlayerLeftMapReader
import com.salkcoding.tunalands.file.PlayerLeftMapWriter
import java.util.*

class LeftManager(private val cooldown: Long) {

    private val leftMap = PlayerLeftMapReader.loadPlayerLeftMap()

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

    fun resetMilliseconds(uuid: UUID) {
        leftMap.remove(uuid)
    }

    fun getLeftMap() = leftMap

    fun dispose() {
        PlayerLeftMapWriter.savePlayerLeftMap()
    }

}

