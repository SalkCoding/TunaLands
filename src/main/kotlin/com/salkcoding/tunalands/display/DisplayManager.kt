package com.salkcoding.tunalands.display

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.toChunkQuery
import com.salkcoding.tunalands.util.toQuery
import org.bukkit.Bukkit
import org.bukkit.Chunk

class DisplayManager {
    //Chunk query
    private val displayMap = mutableMapOf<String, Display>()

    //Auto update scheduler
    private val task = Bukkit.getScheduler().runTaskTimer(tunaLands, Runnable {
        displayMap.forEach { (_, display) ->
            if (!display.isPause)
                display.update()
        }
    }, 20, 20)

    fun createDisplay(lands: Lands) {
        val query = lands.upCoreLocation.chunk.toQuery()
        if (query in displayMap) {
            val display = displayMap[query]!!
            if (!display.isAlive()) display.create()
            return
        }

        val display = TimerDisplay(lands)
        display.create()
        displayMap[query] = display
    }

    fun updateDisplay(lands: Lands) {
        val query = lands.upCoreLocation.chunk.toQuery()
        if (query !in displayMap)
            return

        displayMap[query]!!.update()
    }

    fun setDisplay(lands: Lands, vararg messages: String) {
        val query = lands.upCoreLocation.chunk.toQuery()
        if (query !in displayMap)
            return

        val display = displayMap[query]!! as TimerDisplay
        display.setMessage(*messages)
    }

    fun pauseDisplay(lands: Lands): Display? {
        val query = lands.upCoreLocation.chunk.toQuery()
        if (query !in displayMap)
            return null

        return displayMap[query]!!.apply {
            this.isPause = true
        }
    }

    fun pauseDisplayIfNotPaused(lands: Lands): Display? {
        val query = lands.upCoreLocation.toChunkQuery()
        if (query !in displayMap)
            return null

        val display = displayMap[query]!!
        if (!display.isPause) display.isPause = true
        return display
    }

    fun resumeDisplay(lands: Lands): Display? {
        val query = lands.upCoreLocation.chunk.toQuery()
        if (query !in displayMap)
            return null

        return displayMap[query]!!.apply {
            this.isPause = false
        }
    }

    fun removeDisplay(lands: Lands) {
        val query = lands.upCoreLocation.chunk.toQuery()
        if (query !in displayMap)
            return

        val display = displayMap[query]!!
        display.remove()
        displayMap.remove(query)
    }

    fun removeDisplay(chunk: Chunk) {
        val query = chunk.toQuery()
        if (query !in displayMap)
            return

        val display = displayMap[query]!!
        display.remove()
        displayMap.remove(chunk.toQuery())
    }

    fun dispose() {
        displayMap.forEach { (_, display) ->
            display.remove()
        }
        task.cancel()
    }
}