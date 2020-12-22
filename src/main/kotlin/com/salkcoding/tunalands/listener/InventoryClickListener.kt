package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.guiManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class InventoryClickListener : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        guiManager.onClick(event)
    }

}