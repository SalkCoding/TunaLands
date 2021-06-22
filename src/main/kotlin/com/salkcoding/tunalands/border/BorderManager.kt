package com.salkcoding.tunalands.border

import com.salkcoding.tunalands.tunaLands
import org.bukkit.Bukkit

class BorderManager {

    val task = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, AsyncBorderPainter(), 20, 30)

    fun dispose() {
        task.cancel()
    }
}