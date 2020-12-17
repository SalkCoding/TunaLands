package com.salkcoding.tunalands.events

import com.salkcoding.tunalands.guiManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryClose : Listener {

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        guiManager.onClose(event)
    }

}