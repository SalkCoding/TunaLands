package com.salkcoding.tunalands.util

import com.salkcoding.tunalands.economy
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.giveOrDrop(item: ItemStack) {
    val left = this.inventory.addItem(item)
    for (entry in left) {
        this.eyeLocation.world.dropItem(this.eyeLocation, entry.value)
    }
}

fun Player.hasNotEnoughMoney(price: Double): Boolean {
    return economy.getBalance(this) < price
}