package com.salkcoding.tunalands.events

import com.salkcoding.tunalands.guiManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryDragEvent

class InventoryDrag : Listener {

    @EventHandler
    fun onDrag(event: InventoryDragEvent) {
        guiManager.onDrag(event)
    }

}