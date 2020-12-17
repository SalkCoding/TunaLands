package com.salkcoding.tunalands.gui

import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.InventoryView

class GuiManager {

    val guiMap = HashMap<InventoryView, GuiInterface>()

    fun onClick(event: InventoryClickEvent) {
        if (event.view in guiMap)
            guiMap[event.view]!!.onClick(event)
    }

    fun onDrag(event: InventoryDragEvent) {
        if (event.view in guiMap)
            guiMap[event.view]!!.onDrag(event)
    }

    fun onClose(event: InventoryCloseEvent) {
        if (event.view in guiMap) {
            guiMap[event.view]!!.onClose(event)
            guiMap.remove(event.view)
        }
    }

    fun allClose() {
        guiMap.forEach { (view, _) ->
            view.player.sendMessage("Your GUI was closed by administration.".warnFormat())
            view.close()
        }
    }

}