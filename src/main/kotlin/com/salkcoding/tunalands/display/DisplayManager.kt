package com.salkcoding.tunalands.display

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.toQuery
import org.bukkit.Bukkit
import org.bukkit.Chunk

class DisplayManager {
    //Chunk query
    private val displayMap = mutableMapOf<String, Display>()

    //Auto update scheduler
    private val task = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, Runnable {
        displayMap.forEach { (_, display) ->
            if (!display.pause)
                display.update()
        }
    }, 20, 20)

    fun createDisplay(lands: Lands) {
        val query = lands.upCoreLocation.chunk.toQuery()
        if (query in displayMap)
            return

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

    fun pauseDisplay(lands: Lands) {
        val query = lands.upCoreLocation.chunk.toQuery()
        if (query !in displayMap)
            return

        displayMap[query]!!.pause()
    }

    fun resumeDisplay(lands: Lands) {
        val query = lands.upCoreLocation.chunk.toQuery()
        if (query !in displayMap)
            return

        displayMap[query]!!.resume()
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