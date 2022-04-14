package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.landManager
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent

class ExplodeListener : Listener {

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        if (event.isCancelled) return

        for (block in event.blockList()) {
            if (landManager.isProtectedLand(block.chunk))
                event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (event.isCancelled) return

        for (block in event.blockList()) {
            if (landManager.isProtectedLand(block.chunk))
                event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityChange(event: EntityChangeBlockEvent) {
        if (event.isCancelled) return

        if (landManager.isProtectedLand(event.block.chunk)) {
            if (event.entity !is FallingBlock) {
                event.isCancelled = true
            }
        }
    }
}