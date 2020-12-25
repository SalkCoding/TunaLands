package com.salkcoding.tunalands.util

import br.com.devsrsouza.kotlinbukkitapi.extensions.location.dropItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.giveOrDrop(item: ItemStack) {
    val left = this.inventory.addItem(item)
    for (entry in left) {
        this.eyeLocation.dropItem(entry.value)
    }
}