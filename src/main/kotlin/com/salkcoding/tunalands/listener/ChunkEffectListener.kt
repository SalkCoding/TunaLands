package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.util.toQuery
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.entity.EntityExplodeEvent

class ChunkEffectListener : Listener {

    companion object {
        val effectSet = mutableSetOf<String>()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBreak(event: BlockBreakEvent) {
        if (event.block.chunk.toQuery() in effectSet)
            event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onExplode(event: EntityExplodeEvent) {
        event.blockList().forEach {
            if (it.chunk.toQuery() in effectSet) {
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPushed(event: BlockPistonExtendEvent) {
        event.blocks.forEach {
            if (it.chunk.toQuery() in effectSet) {
                event.isCancelled = true
                return
            }
        }
    }
}