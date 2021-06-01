package com.salkcoding.tunalands.util

import br.com.devsrsouza.kotlinbukkitapi.extensions.location.dropItem
import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.economy
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.giveOrDrop(item: ItemStack) {
    val left = this.inventory.addItem(item)
    for (entry in left) {
        this.eyeLocation.dropItem(entry.value)
    }
}

fun Player.hasNotEnoughMoney(price: Double): Boolean {
    return economy.getBalance(this) < price
}