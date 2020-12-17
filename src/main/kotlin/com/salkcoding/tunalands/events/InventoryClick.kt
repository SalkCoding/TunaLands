package com.salkcoding.tunalands.events

import com.salkcoding.tunalands.guiManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class InventoryClick() : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        guiManager.onClick(event)
    }

}