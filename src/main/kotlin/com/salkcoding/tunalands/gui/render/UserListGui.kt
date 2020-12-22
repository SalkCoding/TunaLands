package com.salkcoding.tunalands.gui.render

import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Rank
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory

class UserListGui(private val player: Player, private val rank: Rank) : GuiInterface {

    override fun render(inv: Inventory) {
        TODO("Not yet implemented")
    }

    override fun onClick(event: InventoryClickEvent) {
        TODO("Not yet implemented")
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }

}

fun Player.openUserListGui(rank: Rank) {
    val inventory = Bukkit.createInventory(null, 54, "Main GUI")
    val gui = UserListGui(this, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}