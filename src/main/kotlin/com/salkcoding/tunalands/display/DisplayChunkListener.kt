package com.salkcoding.tunalands.display

import com.salkcoding.tunalands.displayManager
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.util.isSameChunk
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent

class DisplayChunkListener : Listener {

    @EventHandler
    fun onLoad(event: ChunkLoadEvent) {
        //Protect lands can't be new chunks
        if (event.isNewChunk) return

        val chunk = event.chunk
        if (landManager.isProtectedLand(chunk)) {
            val lands = landManager.getLandsWithChunk(chunk)!!
            val coreChunk = lands.upCoreLocation.chunk
            //Core chunk
            if (chunk.isSameChunk(coreChunk)) {
                displayManager.createDisplay(lands)
            }
        }
    }

    @EventHandler
    fun onUnload(event: ChunkUnloadEvent) {
        displayManager.removeDisplayInChunk(event.chunk)
    }
}